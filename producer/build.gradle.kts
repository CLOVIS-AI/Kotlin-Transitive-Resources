plugins {
	alias(opensavvyConventions.plugins.plugin)
}

dependencies {
	implementation(projects.shared)
}

gradlePlugin {
	plugins {
		register("producer") {
			id = "dev.opensavvy.resources.producer"
			implementationClass = "opensavvy.gradle.resources.producer.ProducerPlugin"
		}
	}
}
