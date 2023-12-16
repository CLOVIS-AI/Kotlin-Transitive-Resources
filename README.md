# Kotlin/JS Resources Plugin

In the JVM ecosystem, any file created in `src/main/resources/` is included in the binary output of the project, and is thus available to any other project depending on the one containing the files.

When doing Kotlin Multiplatform development, this behavior is sadly not yet implemented for the JS platform. The files are present in the `klib` file, but they are mixed with other files, which makes them hard to extract. The Gradle plugin also doesn't generate `klib` files for projects in the same build, which makes extracting them impossible for local projects.

This repository exposes two Gradle plugins:
- `dev.opensavvy.resources.producer`,
- `dev.opensavvy.resources.consumer`.

When creating a library that must expose its resources to users, apply the `producer` plugin. When creating a project that must access resources created by its dependencies, apply the `consumer` plugin. Both plugins can be applied to a single project, if you need to consume and expose resources.

## License

This project is licensed under the [Apache 2.0 license](LICENSE).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).
- To learn more about our coding conventions and workflow, see the [OpenSavvy Wiki](https://gitlab.com/opensavvy/wiki/-/blob/main/README.md#wiki).
- This project is based on the [OpenSavvy Playground](docs/playground/README.md), a collection of preconfigured project templates.
