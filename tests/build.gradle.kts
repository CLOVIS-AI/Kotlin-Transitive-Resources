plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.plugin)
}

dependencies {
	api(projects.producer)
	api(projects.consumer)

	api("org.jetbrains.kotlin:kotlin-gradle-plugin:${opensavvyConventions.versions.kotlin.get()}") {
		because("Without it, any usage of any symbol from the Kotlin plugin results in test projects not being configured at all.")
	}

	testImplementation(libs.prepared)
	testImplementation(libs.prepared.gradle)
}
