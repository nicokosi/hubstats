hubstats [![Build Status](https://travis-ci.org/nicokosi/hubstats.svg?branch=master)](https://travis-ci.org/nicokosi/hubstats)
============================

`hubstats` is a command line tool to compute statistics for GitHub pull requests.

## Prerequisite

[Install leiningen](http://leiningen.org/#install).


## Usage

```shell
lein run --organization $organization --repository $repository
```

If an [access token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/) is required:
```shell
lein run --organization $organization --repository $repository --token $token
```

Output example:

```shell
pull requests for softwarevidal/arthur ->
        since 1 week(s):
                10 opened / 14 closed / 10 commented (90 comments)
                opened per author: {"jprudent" 4, "cmahalin" 4, "omabrouki" 1, "AElMehdiVidal" 1}
                comments per author: {"vidal-rm" 68, "jcgay" 11, "nicokosi" 3, "jprudent" 3, "AElMehdiVidal" 3, "omabrouki" 2}
                closed per author: {"cmahalin" 7, "nicokosi" 3, "jcgay" 2, "jprudent" 1, "AElMehdiVidal" 1}
```

List of all parameters:
```shell
lein run
Display statistics for GitHub pull requests.
Mandatory parameters:
	--organization		GitHub organization
	-o			GitHub organization (shorthand)
	--repository		GitHub repository
	-r			GitHub repository (shorthand)
	--token			GitHub access token (optional)
	-t			GitHub access token (shorthand, optional)
Optional parameters:
	--repositories		Comma-separated list of repositories (optional)
	--since-weeks		output events that occcured since this number of weeks (optional, default: 1)
	-w			output events that occcured since this number of weeks (shorthand, optional, default: 1)
	--since-days		output events that occcured since this number of days (optional)
	-d			output events that occcured since this number of days (shorthand, optional)
	--since			output events that occcured since a date with format '"yyyy-MM-dd'T'HH:mm:ssZ' (optional)
	-s			output events that occcured since a date with format '"yyyy-MM-dd'T'HH:mm:ssZ' (shorthand, optional)

Examples:
	lein run --organization docker --repository containerd --token $token
	lein run --organization docker --repository containerd --since "2017-01-17T00:00:00Z"
	lein run --organization docker --repository containerd --since-days 10
	lein run --organization docker --repositories docker,containerd
```

## Command line installation

### Standard installation (via any Java VM)

Create JAR with all dependencies:
```shell
lein uberjar
```
Then launch (note that Java is required):
```shell
java -jar target/hubstats-*-standalone.jar
```

On Unix-like systems, you can create an executable via the following command:
```shell
./make-exec.sh hubstats
```
This executable, which requires Java, can be moved to `/usr/local/bin`, for example.

### Experimental installation (via GraalVM)

Run:
```shell
lein uberjar

for archive in \
  org/clojure/clojure/1.9.0/clojure-1.9.0.jar \
  org/clojure/core.specs.alpha/0.1.24/core.specs.alpha-0.1.24.jar \
  org/clojure/spec.alpha/0.1.143/spec.alpha-0.1.143.jar \
  joda-time/joda-time/2.9.7/joda-time-2.9.7.jar ; do
  (
    cd target/classes && \
    jar xf ~/.m2/repository/$archive
  )
done

#/home/nkosinski/work/hubstats/test:/home/nkosinski/work/hubstats/src:/home/nkosinski/work/hubstats/dev-resources:/home/nkosinski/work/hubstats/resources:/home/nkosinski/work/hubstats/target/classes:/home/nkosinski/.m2/repository/org/apache/httpcomponents/httpmime/4.5.3/httpmime-4.5.3.jar:/home/nkosinski/.m2/repository/org/clojure/core.async/0.4.474/core.async-0.4.474.jar:/home/nkosinski/.m2/repository/clj-time/clj-time/0.14.2/clj-time-0.14.2.jar:/home/nkosinski/.m2/repository/org/apache/httpcomponents/httpcore-nio/4.4.6/httpcore-nio-4.4.6.jar:/home/nkosinski/.m2/repository/commons-codec/commons-codec/1.10/commons-codec-1.10.jar:/home/nkosinski/.m2/repository/potemkin/potemkin/0.4.3/potemkin-0.4.3.jar:/home/nkosinski/.m2/repository/org/clojure/tools.analyzer/0.6.9/tools.analyzer-0.6.9.jar:/home/nkosinski/.m2/repository/org/clojure/tools.nrepl/0.2.12/tools.nrepl-0.2.12.jar:/home/nkosinski/.m2/repository/clojure-complete/clojure-complete/0.2.4/clojure-complete-0.2.4.jar:/home/nkosinski/.m2/repository/org/clojure/tools.reader/1.0.0-beta4/tools.reader-1.0.0-beta4.jar:/home/nkosinski/.m2/repository/clj-http/clj-http/3.7.0/clj-http-3.7.0.jar:/home/nkosinski/.m2/repository/org/clojure/clojure/1.9.0/clojure-1.9.0.jar:/home/nkosinski/.m2/repository/org/clojure/core.memoize/0.5.9/core.memoize-0.5.9.jar:/home/nkosinski/.m2/repository/commons-io/commons-io/2.5/commons-io-2.5.jar:/home/nkosinski/.m2/repository/clj-tuple/clj-tuple/0.2.2/clj-tuple-0.2.2.jar:/home/nkosinski/.m2/repository/org/clojure/core.specs.alpha/0.1.24/core.specs.alpha-0.1.24.jar:/home/nkosinski/.m2/repository/org/clojure/tools.analyzer.jvm/0.7.0/tools.analyzer.jvm-0.7.0.jar:/home/nkosinski/.m2/repository/org/apache/httpcomponents/httpcore/4.4.6/httpcore-4.4.6.jar:/home/nkosinski/.m2/repository/joda-time/joda-time/2.9.7/joda-time-2.9.7.jar:/home/nkosinski/.m2/repository/org/ow2/asm/asm-all/4.2/asm-all-4.2.jar:/home/nkosinski/.m2/repository/org/clojure/data.json/0.2.6/data.json-0.2.6.jar:/home/nkosinski/.m2/repository/org/clojure/data.priority-map/0.0.7/data.priority-map-0.0.7.jar:/home/nkosinski/.m2/repository/riddley/riddley/0.1.12/riddley-0.1.12.jar:/home/nkosinski/.m2/repository/commons-logging/commons-logging/1.2/commons-logging-1.2.jar:/home/nkosinski/.m2/repository/slingshot/slingshot/0.12.2/slingshot-0.12.2.jar:/home/nkosinski/.m2/repository/org/clojure/core.cache/0.6.5/core.cache-0.6.5.jar:/home/nkosinski/.m2/repository/org/apache/httpcomponents/httpasyncclient/4.1.3/httpasyncclient-4.1.3.jar:/home/nkosinski/.m2/repository/org/apache/httpcomponents/httpclient/4.5.3/httpclient-4.5.3.jar:/home/nkosinski/.m2/repository/org/clojure/spec.alpha/0.1.143/spec.alpha-0.1.143.jar


docker build . -t hubstats

docker run --rm -it hubstats

docker inspect hubstats | jq .[0].Size
```
