package opensavvy.gradle.resources.test

import io.kotest.matchers.paths.shouldExist
import opensavvy.prepared.compat.gradle.buildKts
import opensavvy.prepared.compat.gradle.gradle
import opensavvy.prepared.runner.kotest.PreparedSpec
import opensavvy.prepared.suite.config.CoroutineTimeout
import opensavvy.prepared.suite.prepared
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.writeText
import kotlin.time.Duration.Companion.minutes

class ImportDirectoryTest : PreparedSpec({

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
				transitiveJsResources(project(":core"))
			}
		""".trimIndent())

		project
	}

	suite("Test", config = CoroutineTimeout(1.minutes)) {
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

			app().buildDir().resolve("kjs-transitive-assets/imported/test.txt").shouldExist()
		}
	}

})
