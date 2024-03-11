package opensavvy.gradle.resources.test

import opensavvy.prepared.compat.gradle.buildKts
import opensavvy.prepared.compat.gradle.gradle
import opensavvy.prepared.compat.gradle.settingsKts
import opensavvy.prepared.suite.prepared

fun createBuild(vararg projectName: String) = prepared {
	gradle.settingsKts("""
		rootProject.name = "test"
		
		dependencyResolutionManagement {
			repositories {
				mavenCentral()
			}
		}

		include(${projectName.joinToString { "\"$it\"" }})
	""".trimIndent())

	gradle.rootProject.buildKts("""
		plugins {
			kotlin("multiplatform") apply false
		}
	""".trimIndent())
}
