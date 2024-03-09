package opensavvy.gradle.resources.test

import io.kotest.matchers.paths.shouldExist
import opensavvy.prepared.compat.gradle.buildKts
import opensavvy.prepared.compat.gradle.gradle
import opensavvy.prepared.runner.kotest.PreparedSpec
import opensavvy.prepared.suite.config.CoroutineTimeout
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.writeText
import kotlin.time.Duration.Companion.minutes

class BasicTest : PreparedSpec({

	val basicBuild by createBuild("app", "core")

	suite("Test", config = CoroutineTimeout(1.minutes)) {
		test("Default configuration") {
			basicBuild()

			val app = gradle.project("app")
			val core = gradle.project("core")

			core.dir().createDirectory()
			app.dir().createDirectory()

			core.buildKts("""
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

			val file = core.dir().resolve("src/jsMain/resources/test.txt")
			file.parent.createDirectories()
			file.writeText("hello-world")

			app.buildKts("""
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

			val result = gradle.runner()
				.withArguments("app:assemble")
				.withPluginClasspath()
				.build()

			println(result.output)

			val resultFile = app.buildDir().resolve("kjs-transitive-assets/imported/test.txt")
			resultFile.shouldExist()
		}
	}

})
