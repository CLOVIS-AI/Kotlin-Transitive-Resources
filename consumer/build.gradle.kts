plugins {
	alias(opensavvyConventions.plugins.plugin)
}

dependencies {
	implementation(projects.shared)
}

gradlePlugin {
	plugins {
		register("producer") {
			id = "dev.opensavvy.resources.consumer"
			implementationClass = "opensavvy.gradle.resources.consumer.ConsumerPlugin"
		}
	}
}
