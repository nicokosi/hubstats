(ns hubstats.core
  (:gen-class)
  (:require
    [clojure.data.json :as json]
    [clj-time.core :as time]
    [clj-time.format :as time-format]
    [hubstats.options :as opts])
  (:import (java.io IOException)))

(def date-format (time-format/formatter "yyyy-MM-dd'T'HH:mm:ssZ"))

(defn- events
  ([org repo token page]
   (try
     (json/read-str
       (slurp
         (str
           "https://api.github.com/repos/" org "/" repo "/events" "?access_token=" token "&page=" page)))
     (catch IOException _ nil)))
  ([org repo token page acc]
   (let [events (events org repo token page)]
     (if (nil? events)
       acc
       (recur org repo token (inc page) (concat acc events)))))
  ([org repo token]
   (events org repo token 1 [])))

(defn- since? [map key date]
  (time/after?
    (time-format/parse date-format (get map key))
    date))

(defn- created-since? [event date]
  (since? event "created_at" date))

(defn- action [event] (get-in event ["payload" "action"]))

(defn- pr-closed? [event] (= (action event) "closed"))

(defn- pr-opened? [event] (= (action event) "opened"))

(defn- pr-review-comment-evt? [event] (= "PullRequestReviewCommentEvent" (get event "type")))
(defn- pr-event? [event] (= "PullRequestEvent" (get event "type")))
(defn- created? [review-comment] (= (get-in review-comment ["payload" "action"]) "created"))

(defn- quit [message]
  (.println System/err message)
  (println "Usage:")
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
  (System/exit -1))

(defn -main [& args]
  (let [opts (opts/options (clojure.string/join " " args))]
    (if (some #(= % :missing-org) (opts :errors)) (quit "Missing organization"))
    (if (some #(= % :missing-repo) (opts :errors)) (quit "Missing repository"))
    (if (some #(= % :several-since) (opts :errors)) (quit "Only one 'since' option is possible"))
    (let
      [org (opts :org)
       repo (opts :repo)
       token (opts :token)
       raw-events (events org repo token)
       since-date (get opts :since nil)
       days (Integer/parseInt (get opts :days "0"))
       weeks (Integer/parseInt (get opts :weeks "1"))
       date (if since-date
              (time-format/parse date-format since-date)
              (time/ago
                (if (> days 0)
                  (time/days days)
                  (time/weeks weeks))))
       raw-events-this-week (filter #(created-since? % date) raw-events)
       pr-opened-this-week (filter pr-opened? raw-events-this-week)
       pr-closed-this-week (filter pr-closed? raw-events-this-week)]
      (println (str "pull requests for " org "/" repo " ->"))
      (println (str "\tsince " (if since-date since-date (if (> days 0)
                                                           (str days " day(s):")
                                                           (str weeks " week(s):")))))
      (println (str "\t\t" (count pr-opened-this-week) " opened / " (count raw-events-this-week) " updated / " (count pr-closed-this-week) " closed"))
      (println (str "\t\topened per author: "
                    (reverse
                      (sort-by last
                               (->> (filter pr-event? raw-events)
                                    (filter #(since? % "created_at" date))
                                    (filter #(= "opened" (get-in % ["payload" "action"])))
                                    (map #(get-in % ["actor" "login"]))
                                    frequencies)))))
      (println (str "\t\treview comments per author: "
                    (reverse
                      (sort-by last
                               (->> (filter pr-review-comment-evt? raw-events)
                                    (filter created?)
                                    (filter #(since? % "created_at" date))
                                    (map #(get-in % ["actor" "login"]))
                                    frequencies)))))
      (println (str "\t\tclosed per author: "
                    (reverse
                      (sort-by last
                               (->> (filter pr-event? raw-events)
                                    (filter #(since? % "created_at" date))
                                    (filter #(= "closed" (get-in % ["payload" "action"])))
                                    (map #(get-in % ["actor" "login"]))
                                    frequencies))))))))
