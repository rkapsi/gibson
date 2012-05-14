# Gibson

[Gibson](http://en.wikipedia.org/wiki/Hackers_\(film\)) is a highly scalable (TBD) logging backend and analysis tool. The initial focus of the project is on `Exceptions` but it may be extended to any type of (logging) information.

## Modules

### Core

This is the model that gets dumped into [MongoDB](http://www.mongodb.org).

### Appender

We use [SLF4J](http://www.slf4j.org) and [Logback](http://logback.qos.ch). The appender is actually dependent on Logback and think of it as a Logback plugin. It's responsible for writing logging information into [MongoDB](http://www.mongodb.org).

### Dashboard

The Dashboard is using the [Play!](http://www.playframework.org) Framework.

## Building

### Gradle

We use [Gradle](http://gradle.org) as the build system. Run `gradle eclipse` or `gradle idea` to generate the respective project files. Use `gradle tasks` to get a list of build targets.

### SBT

The [Play!](http://www.playframework.org) framework is using Scala's Simple Build Tool (SBT).

## License

TBD