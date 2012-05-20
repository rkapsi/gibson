# Gibson

The [Gibson](http://en.wikipedia.org/wiki/Hackers_\(film\)) is a highly scalable (TBD) logging backend and analysis tool. The initial focus of the project is on `Exceptions` but it may be extended to any type of (logging) information.

## Modules

### Core

The core provides the model objects and some basic functionality that get dumped into  [MongoDB](http://www.mongodb.org).

### Appender

The appender is based on [Logback](http://logback.qos.ch).

### Dashboard

The dashboard is using the [Play!](http://www.playframework.org) Framework.

## Building

### Prerequisite

#### Gradle

We use [Gradle](http://gradle.org) as the build system. Run `gradle eclipse` or `gradle idea` to generate the respective project files. Use `gradle tasks` to get a list of build targets. There is a `mvn-install.sh` script you must run before you can start working on the dashboard.

#### SBT

The [Play!](http://www.playframework.org) framework is using Scala's Simple Build Tool (SBT). Gibson's Gradle build scripts are not directly integrated with it and you must run `mvn-install.sh` before you can start working on the dashboard code.

## Configuration

The Gibson appender and dashboard connect by default to `mongodb://localhost` and a database called `Gibson`. You can change the appender's configuration through the `logback.xml` file (or Groovy) and the dashboard's configuration can be found in the `conf/application.conf` file.

## Examples

There are a few screenshots under `docs/examples` if you're interested in seeing how it looks like. The appender comes also with a very simple `ExampleIT` that can be used to populate a test database.

## License

Apache Software License 2.0 (ASL).