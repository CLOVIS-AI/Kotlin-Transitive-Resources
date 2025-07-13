package opensavvy.gradle.resources.producer

import opensavvy.gradle.resources.shared.ResourceAttribute
import opensavvy.gradle.resources.shared.ResourceAttributeType
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.tasks.bundling.Zip
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMetadataTarget
import java.lang.reflect.Field

internal fun Project.initializeForKotlin() {
	val kotlin = kotlinExtension as KotlinMultiplatformExtension // cast is safe because this function is only called when the multiplatform is applied

	kotlin.targets.configureEach {
		val target = this
		if (target is KotlinMetadataTarget) {
			// We don't care about the metadata target
			return@configureEach
		}

		logger.info("Configuring resource production for target $target…")

		val archiveTask = tasks.register("${targetName}ResourceArchive", Zip::class.java) {
			from(
				provider {
					compilations.getByName("main").allKotlinSourceSets
						.map { it.resources.sourceDirectories }
				}
			)

			archiveClassifier.set("resources-$targetName")
		}

		val archiveConfiguration = configurations.register("${targetName}ProducedResources") {
			isCanBeConsumed = true
			isCanBeResolved = false

			attributes {
				attribute(ResourceAttribute, ResourceAttributeType.Regular)
				attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
				attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements::class.java, LibraryElements.RESOURCES))
				attribute(Attribute.of("org.jetbrains.kotlin.platform.type", String::class.java), target.platformType.name)
				
				if (target.platformType == KotlinPlatformType.native) {
					val nativeTarget = Attribute.of("org.jetbrains.kotlin.native.target", String::class.java)
					attribute(nativeTarget, target.attributes.getAttribute(nativeTarget)!!)
				}
			}
		}

		artifacts {
			add(archiveConfiguration.name, archiveTask)
		}

		// We now have to expose the artifact we built as part of the Kotlin Multiplatform publication.
		// According to the Gradle documentation, this should be done by accessing an AdhocComponentWithVariants.
		// However, the Kotlin plugin doesn't expose it.
		// Instead, we're poking inside the Kotlin plugin's internals to get it by force.
		// Yes, this is quite brittle.
		val adhocField = Class.forName("org.jetbrains.kotlin.gradle.plugin.mpp.KotlinTargetSoftwareComponentImpl")
			.declaredFields
			.find { it.type == AdhocComponentWithVariants::class.java }
			?.also(Field::trySetAccessible)

		if (adhocField == null) {
			logger.error("Could not access the Kotlin plugin's AdhocComponentWithVariants; the resources will not be published.\nPlease report this to https://gitlab.com/opensavvy/automation/kotlin-js-resources/-/issues/new with a reproduction example, including the exact version of the Kotlin plugin you are using.")
		} else {
			target.components
				.map { adhocField.get(it) as AdhocComponentWithVariants } // safe because of the find above
				.forEach { component ->
					component.addVariantsFromConfiguration(archiveConfiguration.get()) {
						mapToMavenScope("runtime") // A Maven-based project can't do anything with the JAR, so we just tell Maven it's not transitive
						mapToOptional()
					}
				}
		}
	}
}
