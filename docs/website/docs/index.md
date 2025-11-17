---
template: home.html
---

# Welcome!

In the JVM ecosystem, any file created in `src/main/resources` is included in the generated JAR. When a module depends on another one, it automatically gets access to all its static resources.

However, the Kotlin Gradle plugin [does not implement this behavior for other platforms](https://youtrack.jetbrains.com/issue/KT-38230/Gradle-MPP-JS-Process-JS-resources-from-dependencies). If you're creating a KotlinJS library and want to include a font or a TailwindCSS configuration file? Tough luck.

This project introduces two Gradle plugins that embed any file in a library module and access it in any project that depends on it. You can use it to share files in a large multi-module project, and the files will also be embedded when publishing to MavenCentral or any other Maven registry.

## On the library side

If you are creating a library and want to embed static resources, add the `producer` plugin:

```kotlin
// build.gradle.kts
plugins {
	kotlin("multiplatform") version "<add the version here>"
	id("dev.opensavvy.resources.producer") version "<add the version here>" //(1)!
}

kotlin {
	js {
		// …
	}
}
```

1. See the [list of versions](https://kotlin-resources.opensavvy.dev/news/).

That's it! Any files placed in `src/jsMain/resources` will be available to any project that uses the `consumer` plugin. This also works if you publish your library to a Maven or Ivy repository.

## On the consumer side

The Kotlin Gradle Plugin doesn't access the embedded files by itself. To do so, use the `consumer` plugin:

```kotlin
// build.gradle.kts
plugins {
	kotlin("multiplatform") version "<add the version here>"
	id("dev.opensavvy.resources.consumer") version "<add the version here>" //(1)!
}

kotlin {
	js {
		// …
	}
}
```

1. See the [list of versions](https://kotlin-resources.opensavvy.dev/news/).

The plugin doesn't extract the resources automatically from your other dependencies: you should explicitly declare from which library you want to extract dependencies.

For example, to extract all resources from a project called `:lib`:

```kotlin
kotlin {
	js {
		// …
	}

	sourceSets.jsMain.dependencies {
		implementation(project(":lib")) //(1)!
	}
}

dependencies {
	commonConsumedResources(project(":lib")) //(2)!
}
```

1. This is a regular dependency on the source code. This is not necessary: if you remove this line, you depend only on that library's static files and not its code.
2. Declares a dependency on the static files of the project `:lib`. Instead of `commonConsumedResources`, you can also depend only on a single platform, for example with `jsConsumedResources`.

To extract resources from an external library, use the same system:

```kotlin
kotlin {
	js {
		// …
	}

	sourceSets.jsMain.dependencies {
		implementation("foo:bar:1.0") //(1)!
	}
}

dependencies {
	commonConsumedResources("foo:bar:1.0") //(2)!
}
```

1. This is a regular dependency on the library `foo:bar:1.0`. This is not necessary: if you remove this line, you depend only on that library's static files and not its code.
2. Declares a dependency on the static files of the library `foo:bar:1.0`. Instead of `commonConsumedResources`, you can also depend only on a single platform, for example with `jsConsumedResources`.

The resources will automatically be copied as if they were present in your `src/commonMain/resources/imported` folder. Therefore, if you depend on a library which has a file `index.css`, you can access it as `imported/index.css`. The name of the directory [is configurable](https://kotlin-resources.opensavvy.dev/api-docs/consumer/opensavvy.gradle.resources.consumer/-consumer-plugin-extension/directory.html).

***

Please report [any issues you may have](https://gitlab.com/opensavvy/automation/kotlin-js-resources/-/issues/new) and [star the project](https://gitlab.com/opensavvy/automation/kotlin-js-resources)!
