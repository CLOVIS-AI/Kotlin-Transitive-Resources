plugins {
	kotlin("multiplatform")
	id("dev.opensavvy.resources.consumer") version "CHANGE-THIS"
}

repositories {
	mavenCentral()
}

kotlin {
	js(IR) {
		browser()
		binaries.executable()
	}

	sourceSets.jsMain.dependencies {
		implementation(project(":core"))
	}
}
