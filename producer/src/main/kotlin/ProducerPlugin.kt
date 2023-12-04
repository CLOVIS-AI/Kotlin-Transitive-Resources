package opensavvy.gradle.resources.producer

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class ProducerPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
			target.initializeForKotlin()
		}
	}
}
