# hubstats

A tool to compute statistics for GitHub pull requests.

## Prerequisite

[Install leiningen](http://leiningen.org/#install).


## Usage

For a public GitHub repository:
```shell
lein run $organisation $repository
```

For a private GitHub repository, you have to provide an 
[access token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/):
```shell
lein run $organisation $repository $access_token
```

Output example:

```shell
lein run softwarevidal arthur $GITHUB_TOKEN
softwarevidal/arthur pull requests:
2 open, 28 closed
last week: 5 opened, 6 updated, 4 closed
PR opened per author for last week: (["vivianechastagner" 2] ["yves-ducourneau" 1] ["jcgay" 1] ["omabrouki" 1])
PR closed per author for last week: (["cmahalin" 2] ["jcgay" 2])
review comments per author for last week: (["omabrouki" 18] ["nicokosi" 4] ["jcgay" 3] ["AElMehdiVidal" 2] ["vidal-rm" 2] ["cmahalin" 1] ["vivianechastagner" 1])
```
