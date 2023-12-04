package opensavvy.gradle.resources.producer

import opensavvy.gradle.resources.shared.ResourceAttribute
import opensavvy.gradle.resources.shared.ResourceAttributeType
import org.gradle.api.Project

internal fun Project.initializeForKotlin() {
	configurations.register("exposedResources") {
		isCanBeConsumed = true
		isCanBeResolved = false

		// TODO add artifacts

		attributes {
			attribute(ResourceAttribute, ResourceAttributeType.Regular)
		}
	}
}
