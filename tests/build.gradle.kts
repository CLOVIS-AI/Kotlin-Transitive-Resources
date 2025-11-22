import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.plugin)
	alias(libsCommon.plugins.testBalloon)
	id("org.jetbrains.kotlin.plugin.power-assert")
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

@OptIn(ExperimentalKotlinGradlePluginApi::class)
powerAssert {
	functions = listOf(
		// Standard library
		"kotlin.assert",
		"kotlin.require",
		"kotlin.requireNotNull",
		"kotlin.check",
		"kotlin.checkNotNull",

		// Standard test library
		"kotlin.test.assertTrue",
		"kotlin.test.assertFalse",
		"kotlin.test.assertEquals",
		"kotlin.test.assertNull",

		// Prepared
		"opensavvy.prepared.suite.assertions.log",
	)
}
