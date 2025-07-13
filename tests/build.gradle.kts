plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.plugin)
	alias(libsCommon.plugins.testBalloon)
}

dependencies {
	api(projects.producer)
	api(projects.consumer)

	api("org.jetbrains.kotlin:kotlin-gradle-plugin:${libsCommon.versions.kotlin.get()}") {
		because("Without it, any usage of any symbol from the Kotlin plugin results in test projects not being configured at all.")
	}

	testImplementation(libsCommon.opensavvy.prepared.testBalloon)
	testImplementation(libsCommon.opensavvy.prepared.gradle)
}

tasks.configureEach {
	if (name.startsWith("publish")) {
		onlyIf("The :tests repository shouldn't be published") { false }
	}
}
