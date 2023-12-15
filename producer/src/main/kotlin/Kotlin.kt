@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package opensavvy.gradle.resources.producer

import opensavvy.gradle.resources.shared.ResourceAttribute
import opensavvy.gradle.resources.shared.ResourceAttributeType
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.targets
import java.lang.reflect.Field

internal fun Project.initializeForKotlin() {
	var outgoingConfiguration: Provider<Configuration> = provider { error("Could not find the jsMain source set, so the KJS Resource plugin cannot function.") }

	kotlinExtension.sourceSets.configureEach {
		if (name != "jsMain")
			return@configureEach

		val archiveKotlinJsResources by tasks.registering(Zip::class) {
			from(resources.sourceDirectories)

			archiveClassifier.set("kjs-assets")
		}

		val exposedKotlinJsResources by configurations.registering {
			isCanBeConsumed = true
			isCanBeResolved = false

			attributes {
				attribute(ResourceAttribute, ResourceAttributeType.Regular)
				attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
				attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements::class.java, LibraryElements.RESOURCES))
			}
		}
		outgoingConfiguration = exposedKotlinJsResources

		artifacts {
			add(exposedKotlinJsResources.name, archiveKotlinJsResources)
		}
	}

	afterEvaluate {
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
			logger.error("Could not access the Kotlin plugin's AdhocComponentWithVariants; the JS resources will not be published.\nPlease report this to https://gitlab.com/opensavvy/automation/kotlin-js-resources/-/issues/new with a reproduction example, including the exact version of the Kotlin plugin you are using.")
		} else {
			kotlinExtension.targets
				.flatMap { it.components }
				.map { adhocField.get(it) as AdhocComponentWithVariants } // safe because of the find above
				.forEach { component ->
					component.addVariantsFromConfiguration(outgoingConfiguration.get()) {
						mapToMavenScope("runtime") // A Maven-based project can't do anything with the JAR, so we just tell Maven it's not transitive
						mapToOptional()
					}
				}
		}
	}
}
