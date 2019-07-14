hubstats [![Build Status](https://travis-ci.org/nicokosi/hubstats.svg?branch=master)](https://travis-ci.org/nicokosi/hubstats)
============================

`hubstats` is a command line tool to compute statistics for GitHub pull requests.

## Prerequisite

[Install leiningen](http://leiningen.org/#install) and run it on Java 8.

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

### As a Java application (portable)

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

### As a *nix native executable

Create a native executable via GraalVM running `./generate-executable.sh`. Only works for Linux and macOS.
