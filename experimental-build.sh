#!/bin/sh

lein uberjar

for archive in \
  org/clojure/clojure/1.9.0/clojure-1.9.0.jar \
  org/clojure/core.specs.alpha/0.1.24/core.specs.alpha-0.1.24.jar \
  org/clojure/spec.alpha/0.1.143/spec.alpha-0.1.143.jar \
  org/apache/httpcomponents/httpmime/4.5.3/httpmime-4.5.3.jar \ 
  org/clojure/core.async/0.4.474/core.async-0.4.474.jar \ 
  clj-time/clj-time/0.14.2/clj-time-0.14.2.jar \ 
  org/apache/httpcomponents/httpcore-nio/4.4.6/httpcore-nio-4.4.6.jar \ 
  commons-codec/commons-codec/1.10/commons-codec-1.10.jar \ 
  potemkin/potemkin/0.4.3/potemkin-0.4.3.jar \ 
  org/clojure/tools.analyzer/0.6.9/tools.analyzer-0.6.9.jar \ 
  org/clojure/tools.nrepl/0.2.12/tools.nrepl-0.2.12.jar \ 
  clojure-complete/clojure-complete/0.2.4/clojure-complete-0.2.4.jar \ 
  org/clojure/tools.reader/1.0.0-beta4/tools.reader-1.0.0-beta4.jar \ 
  clj-http/clj-http/3.7.0/clj-http-3.7.0.jar \ 
  org/clojure/clojure/1.9.0/clojure-1.9.0.jar \ 
  org/clojure/core.memoize/0.5.9/core.memoize-0.5.9.jar \ 
  commons-io/commons-io/2.5/commons-io-2.5.jar \ 
  clj-tuple/clj-tuple/0.2.2/clj-tuple-0.2.2.jar \ 
  org/clojure/core.specs.alpha/0.1.24/core.specs.alpha-0.1.24.jar \ 
  org/clojure/tools.analyzer.jvm/0.7.0/tools.analyzer.jvm-0.7.0.jar \ 
  org/apache/httpcomponents/httpcore/4.4.6/httpcore-4.4.6.jar \ 
  joda-time/joda-time/2.9.7/joda-time-2.9.7.jar \ 
  org/ow2/asm/asm-all/4.2/asm-all-4.2.jar \ 
  org/clojure/data.json/0.2.6/data.json-0.2.6.jar \ 
  org/clojure/data.priority-map/0.0.7/data.priority-map-0.0.7.jar \ 
  riddley/riddley/0.1.12/riddley-0.1.12.jar \ 
  commons-logging/commons-logging/1.2/commons-logging-1.2.jar \ 
  slingshot/slingshot/0.12.2/slingshot-0.12.2.jar \ 
  org/clojure/core.cache/0.6.5/core.cache-0.6.5.jar \ 
  org/apache/httpcomponents/httpasyncclient/4.1.3/httpasyncclient-4.1.3.jar \ 
  org/apache/httpcomponents/httpclient/4.5.3/httpclient-4.5.3.jar \ 
  org/clojure/spec.alpha/0.1.143/spec.alpha-0.1.143.jar ; do
  (
    cd target/classes && \
    jar xf ~/.m2/repository/$archive
  )
done

docker build . -t hubstats

docker run --rm -it hubstats

docker inspect hubstats | jq .[0].Size