@file:OptIn(ExperimentalPathApi::class)

package opensavvy.gradle.resources.test

import opensavvy.prepared.compat.filesystem.createRandomDirectory
import opensavvy.prepared.compat.gradle.buildKts
import opensavvy.prepared.compat.gradle.gradle
import opensavvy.prepared.runner.testballoon.preparedSuite
import opensavvy.prepared.suite.config.CoroutineTimeout
import opensavvy.prepared.suite.prepared
import kotlin.io.path.*
import kotlin.time.Duration.Companion.minutes

val EmptyResourcesTest by preparedSuite(preparedConfig = CoroutineTimeout(5.minutes)) {
	val mavenLocal by createRandomDirectory(prefix = "mavenLocal")

	val core by prepared {
		val project = gradle.project("core")
		project.dir().createDirectory()

		project.buildKts(
			"""
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
				linuxX64()
				macosX64()
				macosArm64()
			}
		""".trimIndent()
		)

		project
	}

	val jsResourceDir by prepared {
		core().dir().resolve("src/jsMain/resources")
			.also { it.createDirectories() }
	}

	val commonSourcesDir by prepared {
		core().dir().resolve("src/commonMain/kotlin")
			.also { it.createDirectories() }
	}

	test("Do not crash when some platforms have no resources") {
		createBuild("core").immediate("build")
		core()

		jsResourceDir().resolve("test.txt").writeText("test-empty")
		commonSourcesDir().resolve("main.kt").writeText("""
			fun main() {
				println("Hello world!")
			}
		""".trimIndent())

		val result = gradle.runner()
			.withArguments("publishToMavenLocal", "-Dmaven.repo.local=${mavenLocal().absolutePathString()}", "--configuration-cache")
			.withPluginClasspath()
			.build()

		println(result.output)

		check(mavenLocal().listDirectoryEntries().isNotEmpty())
	}

	val app by prepared {
		val project = gradle.project("app")
		project.dir().createDirectory()

		project.buildKts(
			"""
			plugins {
				kotlin("multiplatform")
				id("dev.opensavvy.resources.consumer")
				id("maven-publish")
			}
	
			kotlin {
				jvm()
				js(IR) {
					browser()
				}
			}
			
			dependencies {
				commonConsumedResources(project(":core"))
			}
		""".trimIndent()
		)

		project
	}

	test("Dependent projects should obtain the resources even if some modules have no resources") {
		createBuild("core", "app").immediate("build")
		core()
		app()

		jsResourceDir().resolve("test.txt").writeText("test-empty")
		commonSourcesDir().resolve("main.kt").writeText("""
			fun main() {
				println("Hello world!")
			}
		""".trimIndent())

		val result = gradle.runner()
			.withArguments("publishToMavenLocal", "app:assemble", "-Dmaven.repo.local=${mavenLocal().absolutePathString()}", "--configuration-cache")
			.withPluginClasspath()
			.build()

		println(result.output)

		check(mavenLocal().listDirectoryEntries().isNotEmpty())
		check(app().buildDir().resolve("transitive-resources-js").exists())
	}
}
