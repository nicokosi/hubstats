(ns hubstats.core
  (:gen-class)
  (:require
    [hubstats.github :as github]
    [hubstats.options :as opts]))

(defn- quit [err-message]
  (println "Display statistics for GitHub pull requests.")
  (println "Mandatory parameters:")
  (println "\t--organization\t\tGitHub organization")
  (println "\t-o\t\t\tGitHub organization (shorthand)")
  (println "\t--repository\t\tGitHub repository")
  (println "\t-r\t\t\tGitHub repository (shorthand)")
  (println "\t--token\t\t\tGitHub access token (optional)")
  (println "\t-t\t\t\tGitHub access token (shorthand, optional)")
  (println "Optional parameters:")
  (println "\t--since-weeks\t\toutput events that occcured since this number of weeks (optional, default: 1)")
  (println "\t-w\t\t\toutput events that occcured since this number of weeks (shorthand, optional, default: 1)")
  (println "\t--since-days\t\toutput events that occcured since this number of days (optional)")
  (println "\t-d\t\t\toutput events that occcured since this number of days (shorthand, optional)")
  (println "\t--since\t\t\toutput events that occcured since a date with format '\"yyyy-MM-dd'T'HH:mm:ssZ' (optional)")
  (println "\t-s\t\t\toutput events that occcured since a date with format '\"yyyy-MM-dd'T'HH:mm:ssZ' (shorthand, optional)")
  (if err-message (.println System/err err-message))
  (System/exit -1))

(defn -main [& args]
  (let [opts (opts/options (clojure.string/join " " args))]
    (if (empty? args) (quit nil))
    (if (some #(= % :missing-org) (opts :errors)) (quit "Missing organization"))
    (if (some #(= % :missing-repo) (opts :errors)) (quit "Missing repository"))
    (if (some #(= % :several-since) (opts :errors)) (quit "Only one 'since' option is possible"))
    (let
      [since-date (get opts :since nil)
       days (Integer/parseInt (get opts :days "0"))
       weeks (Integer/parseInt (get opts :weeks "1"))
       pr-stats (github/pr-stats opts)]
      (println (str "pull requests for " (get-in pr-stats [:request :org]) "/" (get-in pr-stats [:request :repo]) " ->"))
      (println (str "\tsince " (if since-date since-date (if (> days 0)
                                                           (str days " day(s):")
                                                           (str weeks " week(s):")))))
      (println (str "\t\t " (get-in pr-stats [:opened :count]) " opened / " (get-in pr-stats [:closed :count]) " closed"))
      (println (str "\t\topened per author: " (get-in pr-stats [:opened :count-by-author])))
      (println (str "\t\treviewed per author: " (get-in pr-stats [:reviewed :count-by-author])))
      (println (str "\t\tclosed per author: " (get-in pr-stats [:closed :count-by-author]))))))
