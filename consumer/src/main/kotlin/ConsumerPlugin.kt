package opensavvy.gradle.resources.consumer

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

@Suppress("unused")
class ConsumerPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		val extension: ConsumerPluginExtension = target.extensions
			.create(
				"kotlinJsResConsumer",
				ConsumerPluginExtension::class
			).apply {
				directory.convention("imported")
			}

		target.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
			target.initializeForKotlin(extension)
		}
	}
}
