# Module KJS Resources Shared code

Shared code between the producer and the consumer.

<a href="https://search.maven.org/search?q=dev.opensavvy.resources"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.gradle.kotlin.resources/shared.svg?label=Maven%20Central"></a>

> Although this project is built as a Gradle plugin, you should think of it as an internal library aimed for the development of the two other plugins. Unless you are making your own plugin based on ours, you should never need to depend on this module directly.

## Technical explanation

We tend to think of Maven coordinates as unique identifiers for a single artifact.
However, developers soon wanted to publish additional information, most commonly, an additional JAR with sources, and another one with the documentation.

Gradle generalized this concept to _capabilities_ and _variants_: each Maven coordinate is home to multiple artifacts.
When we declare a dependency, _our project knows what kind of artifacts it wants_. Based on this, Gradle looks at the available artifacts at the mentioned Maven coordinates, and decides on which one to use.

For example, when we compile a Java project, we know that `implementation` dependencies must be JARs that contain bytecode, and that bytecode must have been built with a Java version that is greater than the one used by the project. Using variants, a library could publish a Java 8 JAR and a Java 17 JAR, and Gradle would automatically use the one compatible with the project that requested it.

This is also how Kotlin Multiplatform is implemented: each platform is variant of the main artifact. This way, Gradle can automatically find the correct artifact for the platform you're compiling to, even when you only specify the main coordinates.

This project works in exactly the same way: we declare an additional variant, `dev.opensavvy.resources`, which contains the static files we want to expose.

- The Producer plugin is responsible for hooking into the Kotlin Multiplatform plugin to convince it that an additional variant exists.
- The Consumer plugin is responsible for configuring the consuming project to know which variant we're looking for.

To learn more, see [the Gradle documentation for variants](https://docs.gradle.org/current/userguide/feature_variants.html).
