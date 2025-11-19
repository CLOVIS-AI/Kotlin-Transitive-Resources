package opensavvy.gradle.resources.consumer

import opensavvy.gradle.resources.shared.RESOURCES_TASK_GROUP
import opensavvy.gradle.resources.shared.ResourceAttribute
import opensavvy.gradle.resources.shared.ResourceAttributeType
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMetadataTarget

internal fun Project.initializeForKotlin(extension: ConsumerPluginExtension) {
	dependencies {
		attributesSchema {
			attribute(ResourceAttribute)
			attribute(Category.CATEGORY_ATTRIBUTE)
			attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE)
			attribute(Attribute.of("org.jetbrains.kotlin.native.target", String::class.java))
		}
	}

	for (platform in KotlinPlatformType.entries) {
		configurations.register("${platform.name}ConsumedResources") {
			if (platform != KotlinPlatformType.common) {
				extendsFrom(configurations.named("commonConsumedResources").get())
			}

			attributes {
				attribute(ResourceAttribute, ResourceAttributeType.Regular)
				attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
				attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements::class.java, LibraryElements.RESOURCES))
				attribute(Attribute.of("org.jetbrains.kotlin.platform.type", String::class.java), platform.name)
			}
		}
	}

	val kotlin = kotlinExtension as KotlinMultiplatformExtension // cast is safe because this function is only called when the multiplatform is applied

	kotlin.targets.configureEach {
		val target = this
		if (target is KotlinMetadataTarget) {
			// We don't care about the metadata target
			return@configureEach
		}

		logger.info("Configuring resource consumption for target $target…")

		// For native, we need a per-target resolvable configuration so Gradle can
		// disambiguate between several native produced variants (linuxX64, macosX64, …).
		// Keep the platform-level configuration for dependency declarations and
		// create a child configuration that adds the native target attribute.
		val archiveConfiguration = when (target.platformType) {
			KotlinPlatformType.native -> {
				val base = configurations.named("${target.platformType.name}ConsumedResources").get()
				configurations.maybeCreate("${targetName}ConsumedResources").apply {
					extendsFrom(base)
					// Mirror the attributes from the base configuration and add the native target
					attributes {
						attribute(ResourceAttribute, ResourceAttributeType.Regular)
						attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
						attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements::class.java, LibraryElements.RESOURCES))
						attribute(Attribute.of("org.jetbrains.kotlin.platform.type", String::class.java), target.platformType.name)

						// Align on the exact native target, matching what the producer publishes
						val nativeTarget = Attribute.of("org.jetbrains.kotlin.native.target", String::class.java)
						val value = target.attributes.getAttribute(nativeTarget)
						if (value != null) attribute(nativeTarget, value)
					}
				}
				configurations.named("${targetName}ConsumedResources")
			}

			else -> configurations.named("${target.platformType.name}ConsumedResources")
		}

		val extractionDirectory = layout.buildDirectory.dir("transitive-resources-$targetName")

		val organizeResources = tasks.register("${targetName}OrganizeResources", Sync::class.java) {
			group = RESOURCES_TASK_GROUP
			description = "Downloads the declared Kotlin/JS transitive resources"

			val archives = serviceOf<ArchiveOperations>()

			val zips = archiveConfiguration.get().elements.map { elements ->
				elements.map { archives.zipTree(it) }
			}

			from(zips) {
				duplicatesStrategy = DuplicatesStrategy.WARN
			}

			val resDir by extension.directory
			if (resDir.isEmpty()) {
				into(extractionDirectory)
			} else {
				into(extractionDirectory.map { it.dir(resDir) })
			}
		}

		tasks.named("${targetName}ProcessResources", Copy::class.java) {
			dependsOn(organizeResources)
			from(extractionDirectory)
		}
	}
}
