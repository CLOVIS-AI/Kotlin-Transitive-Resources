plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.plugin)
	alias(opensavvyConventions.plugins.kotlin.abstractLibrary)
}

java {
	withSourcesJar()
}

dependencies {
	implementation(projects.shared)

	// We want to access things from the Kotlin plugin
	compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${libsCommon.versions.kotlin.get()}")
}

gradlePlugin {
	plugins {
		register("producer") {
			id = "dev.opensavvy.resources.producer"
			implementationClass = "opensavvy.gradle.resources.producer.ProducerPlugin"
		}
	}
}

library {
	name.set("KJS Resources Producer")
	description.set("Gradle plugin to expose Kotlin/JS resources to dependent projects")
	homeUrl.set("https://gitlab.com/opensavvy/automation/kotlin-js-resources")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
