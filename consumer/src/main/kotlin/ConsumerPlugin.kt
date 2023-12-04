package opensavvy.gradle.resources.consumer

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class ConsumerPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		target.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
			target.initializeForKotlin()
		}
	}
}
