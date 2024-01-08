package opensavvy.gradle.resources.consumer

import opensavvy.gradle.resources.shared.RESOURCES_TASK_GROUP
import opensavvy.gradle.resources.shared.ResourceAttribute
import opensavvy.gradle.resources.shared.ResourceAttributeType
import org.gradle.api.Project
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering
import org.gradle.kotlin.dsl.support.serviceOf

internal fun Project.initializeForKotlin() {
	dependencies {
		attributesSchema {
			attribute(ResourceAttribute)
			attribute(Category.CATEGORY_ATTRIBUTE)
			attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE)
		}
	}

	val transitiveJsResources by configurations.registering {
		attributes {
			attribute(ResourceAttribute, ResourceAttributeType.Regular)
			attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
			attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements::class.java, LibraryElements.RESOURCES))
		}
	}

	val extractionDirectory = layout.buildDirectory.dir("kjs-transitive-assets")

	val organizeTransitiveJsResources by tasks.registering(Sync::class) {
		group = RESOURCES_TASK_GROUP
		description = "Downloads the declared Kotlin/JS transitive resources"

		val archives = serviceOf<ArchiveOperations>()

		val zips = transitiveJsResources.get().elements.map { elements ->
			elements.map { archives.zipTree(it) }
		}

		from(zips)
		into(extractionDirectory.map { it.dir("imported") })
	}

	tasks.withType(Copy::class.java) {
		if (name != "jsProcessResources")
			return@withType

		dependsOn(organizeTransitiveJsResources)
		from(extractionDirectory)
	}
}
