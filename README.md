# Gibson

> Their Crime is Curiosity

[Gibson](http://en.wikipedia.org/wiki/Hackers_\(film\)) is a highly scalable (TBD) logging backend and analysis tool. The initial focus of the project is on `Exceptions` but may be extended to any type of logging information.

## Modules

### Riak

Riak has a pretty decent Java Client but they're making certain assumptions regards use-cases and there are many inefficiencies. The Riak module is an attempt to address some of them but for the purpose of this exercise it's not really necessary.  

### Core

This is the model that gets serialized into JSON.

### Appender

We use [SLF4J](http://www.slf4j.org) and [Logback](http://logback.qos.ch). The appender is actually dependent on Logback and think of it as a Logback plugin. It's responsible for writing logging information into Riak.

### Dashboard

TBD. I'd love to use something like [Play!](http://www.playframework.org) but I have a feeling there is not enough time to learn `Play!` and get this done in a few hours. Unless we start cranking on this now of course. :)

## Riak

We use [Riak](http://basho.com/products/riak-overview) to store the logging information.

## Gradle

We use [Gradle](http://gradle.org) as the build system. Use `gradle eclipse` or `gradle idea` to generate the respective project files. Use `gradle tasks` to get a list of build targets.

## Package

TBD - I'm using `org.ardverk.*` out of convenience.

## License

TBD