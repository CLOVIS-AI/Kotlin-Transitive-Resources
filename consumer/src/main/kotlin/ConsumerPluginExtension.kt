package opensavvy.gradle.resources.consumer

import org.gradle.api.provider.Property

interface ConsumerPluginExtension {
	/**
	 * The directory in which the imported resources are extracted to.
	 * Set to null to use the resources base directory.
	 */
	val directory: Property<String?>
}
