# hubstats

A command line tool to compute statistics for GitHub pull requests.

## Prerequisite

[Install leiningen](http://leiningen.org/#install).


## Usage

For a public GitHub repository:
```shell
lein run --organization $organization --repository $repository
```
or
```shell
lein run -o $organization -r $repository
```

For a private GitHub repository, you have to provide an 
[access token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/):
```shell
lein run --organization $organization --repository $repository --token $token
```
or
```shell
lein run -o $organization -r $repository -t $token
```

Output example:

```shell
lein run --organization softwarevidal --repository arthur --token $token
pull requests for softwarevidal/arthur ->
        since 1 week:
                3 opened / 66 updated / 3 closed
                opened per author: (["omabrouki" 1] ["jcgay" 1] ["cmahalin" 1])
                review comments per author: (["nicokosi" 4] ["omabrouki" 3] ["jcgay" 1] ["vivianechastagner" 1])
                closed per author: (["jcgay" 2] ["cmahalin" 1])
```

## Command line installation

Create JAR with all dependencies:
```shell
lein uberjar
```
Then launch (note that Java 8 is required):
```shell
java -jar /home/nkosinski/perso/hubstats/target/hubstats-*-standalone.jar
```

On Unix-like systems, an alias can be created for your convenience:
```shell
alias hubstats='java -jar /home/nkosinski/perso/hubstats/target/hubstats-*-standalone.jar'
```shell
