# Gibson

[Gibson](http://en.wikipedia.org/wiki/Hackers_\(film\)) is a [Logback](http://logback.qos.ch) 
appender with instrumentation for [Spunk](http://www.splunk.com). It is a fork/rewrite of an 
earlier implementation that was a stand alone system with its own Dashboard.

  import com.squarespace.gibson.GibsonAppender
  
  final PATTERN = "[signature=%X{Gibson.SIGNATURE}] %level %logger{0} - %msg%n"
  
  appender("STDOUT", GibsonAppender) {
    appender(ConsoleAppender) {
      encoder(PatternLayoutEncoder) {
        pattern = "${PATTERN}"
      }
    }
  }