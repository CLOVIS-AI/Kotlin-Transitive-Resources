package opensavvy.gradle.resources.test

import opensavvy.prepared.compat.gradle.buildKts
import opensavvy.prepared.compat.gradle.gradle
import opensavvy.prepared.runner.testballoon.preparedSuite
import opensavvy.prepared.suite.config.CoroutineTimeout
import opensavvy.prepared.suite.prepared
import kotlin.io.path.*
import kotlin.time.Duration.Companion.minutes

val ImportDirectoryTest by preparedSuite {
	val basicBuild by createBuild("app", "core")

	val core by prepared {
		val project = gradle.project("core")
		project.dir().createDirectory()

		project.buildKts("""
			plugins {
				kotlin("multiplatform")
				id("dev.opensavvy.resources.producer")
				id("maven-publish")
			}
	
			kotlin {
				jvm()
				js(IR) {
					browser()
				}
			}
		""".trimIndent())

		project
	}

	val coreResourceDir by prepared {
		core().dir().resolve("src/jsMain/resources")
			.also { it.createDirectories() }
	}

	val app by prepared {
		val project = gradle.project("app")
		project.dir().createDirectory()

		project.buildKts("""
			plugins {
				kotlin("multiplatform")
				id("dev.opensavvy.resources.consumer")
			}

			kotlin {
				js(IR) {
					browser()
				}

				sourceSets.jsMain.dependencies {
					implementation(project(":core"))
				}
			}

			dependencies {
				jsConsumedResources(project(":core"))
			}
		""".trimIndent())

		project
	}

	suite("Test", config = CoroutineTimeout(5.minutes)) {
		test("Default configuration") {
			basicBuild()

			app()
			core()

			coreResourceDir().resolve("test.txt").writeText("test-world")

			val result = gradle.runner()
				.withArguments("app:assemble")
				.withPluginClasspath()
				.build()

			println(result.output)

			check(app().buildDir().resolve("transitive-resources-js/imported/test.txt").exists())
		}

		test("Custom import directory") {
			basicBuild()

			app()
			core()

			coreResourceDir().resolve("test.txt").writeText("test-world")

			app().buildKts().appendText("""
				// this comment line is important (otherwise the concatenation with the existing config breaks)
				kotlinJsResConsumer {
					directory.set("a/custom/dir")
				}
			""".trimIndent())

			val result = gradle.runner()
				.withArguments("app:assemble")
				.withPluginClasspath()
				.build()

			println(result.output)

			check(app().buildDir().resolve("transitive-resources-js/a/custom/dir/test.txt").exists())
		}

		test("Custom import directory: resource root without nesting") {
			basicBuild()

			app()
			core()

			coreResourceDir().resolve("test.txt").writeText("test-world")

			app().buildKts().appendText("""
				// this comment line is important (otherwise the concatenation with the existing config breaks)
				kotlinJsResConsumer {
					directory.set("")
				}
			""".trimIndent())

			val result = gradle.runner()
				.withArguments("app:assemble")
				.withPluginClasspath()
				.build()

			println(result.output)

			check(app().buildDir().resolve("transitive-resources-js/test.txt").exists())
		}
	}
}
