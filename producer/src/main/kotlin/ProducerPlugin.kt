package opensavvy.gradle.resources.producer

import opensavvy.gradle.resources.shared.ResourceAttribute
import opensavvy.gradle.resources.shared.ResourceAttributeType
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class ProducerPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.configurations.register("exposedResources") {
			isCanBeConsumed = true
			isCanBeResolved = false

			// TODO add artifacts

			attributes {
				attribute(ResourceAttribute, ResourceAttributeType.Regular)
			}
		}
	}
}
