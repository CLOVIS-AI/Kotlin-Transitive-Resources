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
			id = "dev.opensavvy.resources.consumer"
			implementationClass = "opensavvy.gradle.resources.consumer.ConsumerPlugin"
		}
	}
}

library {
	name.set("KJS Resources Consumer")
	description.set("Gradle plugin to consume Kotlin/JS resources published by other projects")
	homeUrl.set("https://gitlab.com/opensavvy/automation/kotlin-js-resources")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
