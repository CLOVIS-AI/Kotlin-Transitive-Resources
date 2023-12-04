plugins {
	alias(opensavvyConventions.plugins.plugin)
}

dependencies {
	implementation(projects.shared)

	// We want to access things from the Kotlin plugin
	compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${opensavvyConventions.versions.kotlin.get()}")
}

gradlePlugin {
	plugins {
		register("producer") {
			id = "dev.opensavvy.resources.consumer"
			implementationClass = "opensavvy.gradle.resources.consumer.ConsumerPlugin"
		}
	}
}
