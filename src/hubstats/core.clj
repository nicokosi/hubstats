(ns hubstats.core
  (:require
    [clojure.data.json :as json]
    [clj-time.core :as time]
    [clj-time.format :as time-format])
  (:import (java.io IOException)))

(def date-format (time-format/formatter "yyyy-MM-dd'T'HH:mm:ssZ"))

(defn events
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

(defn this-week? [map key]
  (time/after?
    (time-format/parse date-format (get map key))
    (time/ago (time/weeks 1))))

(defn created-this-week? [event]
  (this-week? event "created_at"))

(defn action [event] (get-in event ["payload" "action"]))

(defn pr-closed? [event] (= (action event) "closed"))

(defn pr-opened? [event] (= (action event) "opened"))

(defn pr-review-comment-evt? [event] (= "PullRequestReviewCommentEvent" (get event "type")))
(defn pr-event? [event] (= "PullRequestEvent" (get event "type")))
(defn created? [review-comment] (= (get-in review-comment ["payload" "action"]) "created"))

(defn -main [& args]
  (let
    [org (first args)
     repo (second args)
     token (nth args 2 "")

     raw-events (events org repo token)
     raw-events-this-week (filter created-this-week? raw-events)

     pr-opened-this-week (filter pr-opened? raw-events-this-week)
     pr-closed-this-week (filter pr-closed? raw-events-this-week)]

    (println (str org "/" repo " pull requests:"))

    (println (str "last week: " (count pr-opened-this-week) " opened, " (count raw-events-this-week) " updated, " (count pr-closed-this-week) " closed"))

    (println (str "PR opened per author for last week: "
                  (reverse
                    (sort-by last
                             (->> (filter pr-event? raw-events)
                                  (filter #(this-week? % "created_at"))
                                  (filter #(= "opened" (get-in % ["payload" "action"])))
                                  (map #(get-in % ["actor" "login"]))
                                  frequencies)))))

    (println (str "PR closed per author for last week: "
                  (reverse
                    (sort-by last
                             (->> (filter pr-event? raw-events)
                                  (filter #(this-week? % "created_at"))
                                  (filter #(= "closed" (get-in % ["payload" "action"])))
                                  (map #(get-in % ["actor" "login"]))
                                  frequencies)))))

    (println (str "review comments per author for last week: "
                  (reverse
                    (sort-by last
                             (->> (filter pr-review-comment-evt? raw-events)
                                  (filter created?)
                                  (filter #(this-week? % "created_at"))
                                  (map #(get-in % ["actor" "login"]))
                                  frequencies)))))

    ))
