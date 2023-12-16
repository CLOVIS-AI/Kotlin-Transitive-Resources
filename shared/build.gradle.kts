plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.plugin)
	alias(opensavvyConventions.plugins.kotlin.abstractLibrary)
}

java {
	withSourcesJar()
}

library {
	name.set("KJS Resources Shared code")
	description.set("Shared code between the Producer and Consumers")
	homeUrl.set("https://gitlab.com/opensavvy/automation/kotlin-js-resources")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
