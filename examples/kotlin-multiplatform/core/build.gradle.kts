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
}
