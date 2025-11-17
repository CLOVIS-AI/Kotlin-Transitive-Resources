# Module KJS Resources Producer

Exposes Kotlin/JS resources to other projects.

<a href="https://search.maven.org/search?q=dev.opensavvy.resources"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.gradle.kotlin.resources/producer.svg?label=Maven%20Central"></a>
<a href="https://opensavvy.dev/open-source/stability.html"><img src="https://badgen.net/static/Stability/alpha/purple"></a>
<a href="https://javadoc.io/doc/dev.opensavvy.gradle.kotlin.resources/producer"><img src="https://badgen.net/static/Other%20versions/javadoc.io/blue"></a>

> For a technical explanation of how this plugin works, see the Shared module.

## Configuration

Currently, this plugin requires the [Kotlin Multiplatform plugin](https://kotlinlang.org/docs/multiplatform-dsl-reference.html).
Otherwise, it does nothing.

```kotlin
// build.gradle.kts
plugins {
	kotlin("multiplatform") version "<add the version here>"
	id("dev.opensavvy.resources.producer") version "<add the version here>"
}

kotlin {
	js {
		// …
	}
}
```

That's all! Now, you can use the Consumer plugin in your other projects that are part of the same build.

If you want to publish your project to a Maven or Ivy repository, follow the normal instructions on how to publish a Kotlin Multiplatform library and the resources will automatically be added as a variant. 
