package opensavvy.gradle.resources.shared

import org.gradle.api.attributes.Attribute

/**
 * Possible types of resource publications.
 */
enum class ResourceAttributeType {
	// Does nothing particular for now, it's future-proofing in case we ever want to add new types.
	Regular,
}

/**
 * Attribute that marks that a publication contains static resources files.
 */
val ResourceAttribute = Attribute.of("dev.opensavvy.resources", ResourceAttributeType::class.java)
