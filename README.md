# Kotlin/JS Resources Plugin

In the JVM ecosystem, any file created in `src/main/resources/` is included in the binary output of the project, and is thus available to any other project depending on the one containing the files.

When doing Kotlin Multiplatform development, this behavior is sadly not yet implemented for the JS platform. The files are present in the `klib` file, but they are mixed with other files, which makes them hard to extract. The Gradle plugin also doesn't generate `klib` files for projects in the same build, which makes extracting them impossible for local projects.

This repository exposes two Gradle plugins:
- `dev.opensavvy.resources.producer`,
- `dev.opensavvy.resources.consumer`.

When creating a library that must expose its resources to users, apply the `producer` plugin. When creating a project that must access resources created by its dependencies, apply the `consumer` plugin. Both plugins can be applied to a single project, if you need to consume and expose resources.

To discover how to configure the plugins, see [the documentation](https://opensavvy.gitlab.io/automation/kotlin-js-resources/api-docs/).

## License

This project is licensed under the [Apache 2.0 license](LICENSE).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).
- To learn more about our coding conventions and workflow, see the [OpenSavvy website](https://opensavvy.dev/open-source/index.html).
- This project is based on the [OpenSavvy Playground](docs/playground/README.md), a collection of preconfigured project templates.

If you don't want to clone this project on your machine, it is also available using [GitPod](https://www.gitpod.io/) and [DevContainer](https://containers.dev/) ([VS Code](https://code.visualstudio.com/docs/devcontainers/containers) • [IntelliJ & JetBrains IDEs](https://www.jetbrains.com/help/idea/connect-to-devcontainer.html)). Don't hesitate to create issues if you have problems getting the project up and running.
