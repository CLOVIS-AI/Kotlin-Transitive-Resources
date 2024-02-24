package opensavvy.gradle.resources.consumer

import org.gradle.api.provider.Property

interface ConsumerPluginExtension {
	/**
	 * The directory in which the imported resources are extracted to.
	 * Set to "" to use the resources base directory.
	 * The value is not checked for valid paths.
	 */
	val directory: Property<String>
}

fun ConsumerPluginExtension.setConventions() {
	directory.convention("imported")
}
