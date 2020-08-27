(ns hubstats.core
  (:gen-class)
  (:require
   [hubstats.github :as github]
   [hubstats.options :as opts]
   [clojure.string :as string]
   [clojure.core.async :refer [chan, go, <!, <!!, >!]]))

(defn- quit [err-message]
  (println
   (str
    "Display statistics for GitHub pull requests.
    Mandatory parameters:
    \t--organization\t\tGitHub organization
    \t-o\t\t\tGitHub organization (shorthand)
    \t--repository\t\tGitHub repository
    \t-r\t\t\tGitHub repository (shorthand)
    \t--token\t\t\tGitHub access token (optional)
    \t-t\t\t\tGitHub access token (shorthand, optional)
    Optional parameters:
    \t--repositories\t\tComma-separated list of repositories (optional)
    \t--since-weeks\t\toutput events that occurred since this number of weeks (optional, default: 1)
    \t-w\t\t\toutput events that occurred since this number of weeks (shorthand, optional, default: 1)
    \t--since-days\t\toutput events that occurred since this number of days (optional)
    \t-d\t\t\toutput events that occurred since this number of days (shorthand, optional)
    \t--since\t\t\toutput events that occurred since a date with format 'yyyy-MM-dd'T'HH:mm:ssZ' (optional)
    \t-s\t\t\toutput events that occurred since a date with format 'yyyy-MM-dd'T'HH:mm:ssZ' (shorthand, optional)

    Examples:
    \tlein run --organization docker --repository containerd --token $token
    \tlein run --organization docker --repository containerd --since 2017-01-17T00:00:00Z
    \tlein run --organization docker --repository containerd --since-days 10
    \tlein run --organization docker --repositories docker,containerd"))
  (when err-message (.println System/err err-message))
  (System/exit -1))

(defn display-stats [repo pr-stats]
  (println
   (str
    "pull requests for " (get-in pr-stats [:request :org]) "/" repo " ->\n"
    "\tsince " (get-in pr-stats [:request :since]) "\n"
    "\t\t" (get-in pr-stats [:opened :count]) " opened"
    " / " (get-in pr-stats [:closed :count]) " closed"
    " / " (get-in pr-stats [:commented :count]) " commented"
    " (" (get-in pr-stats [:comments :count]) " comments)") "\n"
   "\t\topened per author: " (get-in pr-stats [:opened :count-by-author]) "\n"
   "\t\tcomments per author: " (get-in pr-stats [:comments :count-by-author]) "\n"
   "\t\tclosed per author: " (get-in pr-stats [:closed :count-by-author])))

(defn -main [& args]
  (let [opts (opts/options (string/join " " args))]
    (when (empty? args) (quit nil))
    (when (some #(= % :missing-org) (opts :errors)) (quit "Missing organization"))
    (when (some #(= % :missing-repo) (opts :errors)) (quit "Missing repository"))
    (when (some #(= % :several-since) (opts :errors)) (quit "Only one 'since' option is possible"))

    (let [c (chan)]
      (doseq [repo (:all-repos opts)]
        (go
          (>! c {:repo repo, :pr-stats (github/pr-stats opts repo)})))

      (<!!
       (go
         (doseq [_ (:all-repos opts)]
           (let [stats-and-repo (<! c)
                 pr-stats (:pr-stats stats-and-repo)
                 repo (:repo stats-and-repo)]
             (display-stats repo pr-stats))))))))