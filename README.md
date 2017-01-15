hubstats [![Build Status](https://travis-ci.org/nicokosi/hubstats.svg?branch=master)](https://travis-ci.org/nicokosi/hubstats)
============================

`hubstats` is a command line tool to compute statistics for GitHub pull requests.

## Prerequisite

[Install leiningen](http://leiningen.org/#install).


## Usage

For a public GitHub repository:
```shell
lein run --organization $organization --repository $repository
```

For a private GitHub repository, you have to provide an 
[access token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/):
```shell
lein run --organization $organization --repository $repository --token $token
```

Output example:

```shell
lein run --organization softwarevidal --repository arthur --token $token
pull requests for softwarevidal/arthur ->
	since 1 week(s):
		9 opened / 56 commented / 5 closed
		opened per author: {"cmahalin" 5, "jcgay" 2, "AElMehdiVidal" 2}
		commented per author: {"vidal-rm" 30, "jcgay" 17, "cmahalin" 9}
		closed per author: {"cmahalin" 2, "AElMehdiVidal" 2, "jprudent" 1}
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
	--since-weeks		output events that occcured since this number of weeks (optional, default: 1)
	-w			output events that occcured since this number of weeks (shorthand, optional, default: 1)
	--since-days		output events that occcured since this number of days (optional)
	-d			output events that occcured since this number of days (shorthand, optional)
	--since			output events that occcured since a date with format '"yyyy-MM-dd'T'HH:mm:ssZ' (optional)
	-s			output events that occcured since a date with format '"yyyy-MM-dd'T'HH:mm:ssZ' (shorthand, optional)
```

## Command line installation

Create JAR with all dependencies:
```shell
lein uberjar
```
Then launch (note that Java is required):
```shell
java -jar /home/nkosinski/perso/hubstats/target/hubstats-*-standalone.jar
```

On Unix-like systems, you can create an executable via the following command:
```shell
./make-exec.sh hubstats
```
This executable, which requires Java, can be moved to `/usr/local/bin`, for example.

