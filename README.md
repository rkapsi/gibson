# Gibson

[Gibson](http://en.wikipedia.org/wiki/Hackers_\(film\)) is a highly scalable (TBD) logging backend and analysis tool. The initial focus of the project is on `Exceptions` but it may be extended to any type of (logging) information.

## Modules

### Riak

[Riak](http://basho.com/products/riak-overview) has a pretty decent [Java Client](https://github.com/basho/riak-java-client) but they're making certain assumptions regards use-cases and there are many inefficiencies. The Riak module is an attempt to address some of them but for the purpose of this exercise it's not really necessary.  

### Core

This is the model that gets serialized into JSON. Every `non-null` field gets serialized into JSON and the `@RiakKey` annotated field gets stripped out and is used as the primary key in Riak.

### Appender

We use [SLF4J](http://www.slf4j.org) and [Logback](http://logback.qos.ch). The appender is actually dependent on Logback and think of it as a Logback plugin. It's responsible for writing logging information into Riak.

### Dashboard

TBD. I'd love to use something like [Play!](http://www.playframework.org) but I have a feeling there is not enough time to learn `Play!` and get this done in a few hours. Unless we start cranking on this now of course. :)

## Riak

We use [Riak](http://basho.com/products/riak-overview) to store the logging information. Any `Key-Value` store or other type storage system should be sufficient as long as it does what we need. Riak seems to be a pretty good fit. There are no special nodes, it does `Map-Reduce`, it scales simply by adding (or removing) nodes to the cluster.

Since we've all used [MongoDB](http://www.mongodb.org) quite a bit I'd like to use the opportunity to say that it'd be a bad (as in terrible) choice for this use case. Gibson is super heavy on writing and Mongo's global write-lock would become a huge bottleneck with the potential to take down the appenders if they don't back off. It scales in terms of storage and computational power through sharding and the last thing we want to do think about is shard-keys. It needs everything in RAM in order to perform well.

## Gradle

We use [Gradle](http://gradle.org) as the build system. Run `gradle eclipse` or `gradle idea` to generate the respective project files. Use `gradle tasks` to get a list of build targets.

## Package

TBD - I'm using `org.ardverk.*` out of convenience.

## License

TBD