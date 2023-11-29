plugins {
	kotlin("multiplatform")
	id("dev.opensavvy.resources.producer") version "CHANGE-THIS"
}

repositories {
	mavenCentral()
}

kotlin {
	js(IR) {
		browser()
	}
}
