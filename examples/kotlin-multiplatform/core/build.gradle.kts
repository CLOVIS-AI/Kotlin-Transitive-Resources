plugins {
	kotlin("multiplatform")
	id("dev.opensavvy.resources.producer") version "CHANGE-THIS"
	id("maven-publish")
}

repositories {
	mavenCentral()
}

kotlin {
	js(IR) {
		browser()
	}
	wasmJs {
		browser()
	}
	jvm()
	linuxX64()

	sourceSets {
		val webMain by creating

		val wasmJsMain by getting { dependsOn(webMain) }
		jsMain { dependsOn(webMain) }
	}
}
