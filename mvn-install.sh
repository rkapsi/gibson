#!/bin/sh

# http://issues.gradle.org/browse/GRADLE-2308
gradle gibson-core:install $*
gradle gibson-appender:install $*
