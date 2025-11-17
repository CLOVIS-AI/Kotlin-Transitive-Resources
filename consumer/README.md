# Module KJS Resources Consumer

Extracts Kotlin/JS resources from other projects and injects them into the current project's resources.

<a href="https://search.maven.org/search?q=dev.opensavvy.resources"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.gradle.kotlin.resources/consumer.svg?label=Maven%20Central"></a>
<a href="https://opensavvy.dev/open-source/stability.html"><img src="https://badgen.net/static/Stability/alpha/purple"></a>
<a href="https://javadoc.io/doc/dev.opensavvy.gradle.kotlin.resources/consumer"><img src="https://badgen.net/static/Other%20versions/javadoc.io/blue"></a>

> For a technical explanation of how this plugin works, see the Shared module.

## Applying the plugin

Currently, this plugin requires the [Kotlin Multiplatform plugin](https://kotlinlang.org/docs/multiplatform-dsl-reference.html).
Otherwise, it does nothing.

```kotlin
// build.gradle.kts
plugins {
	kotlin("multiplatform") version "<add the version here>"
	id("dev.opensavvy.resources.consumer") version "<add the version here>"
}

kotlin {
	js {
		// …
	}
}
```

Currently, the plugin does not extract the resource dependencies automatically from your other dependencies: you should explicitly declare from which Maven coordinates you want to extract dependencies.

## Declare dependencies

For example, to extract resources from another project called `lib`:

```kotlin
kotlin {
	js {
		// …
	}

	sourceSets.jsMain.dependencies {
		// this is a regular code dependency
		implementation(project(":lib"))
	}
}

dependencies {
	// declare a dependency on the resources
	commonConsumedResources(project(":lib"))
}
```

To declare a dependency on an external library, use the same mechanism:

```kotlin
kotlin {
	js {
		// …
	}

	sourceSets.jsMain.dependencies {
		// this is a regular code dependency
		implementation("foo:bar:1.0")
	}
}

dependencies {
	// declare a dependency on the resources
	commonConsumedResources("foo:bar:1.0")
}
```

You can also depend on the resources for only a single platform, for example with the scope `jsConsumedResources` instead of `commonConsumedResources`.

If you just want the resources but not the code, you can omit the `implementation` declaration.

## Accessing the resources

All resources from the dependencies are injected into the compilation phase as if they were present in `src/<platform>Main/resources/imported` directory.

Therefore, if you depend on a library which has a file `index.css`, you can access it as `imported/index.css`.
