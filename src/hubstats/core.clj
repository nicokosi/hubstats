(ns hubstats.core
  (:require
    [clojure.data.json :as json]
    [clj-time.core :as time]
    [clj-time.format :as time-format])
  (:import (java.io IOException)))

(defn pulls [org repo token]
  (json/read-str
    (slurp
      (str "https://api.github.com/repos/" org "/" repo "/pulls?state=all&access_token=" token))))

(def date-format (time-format/formatter "yyyy-MM-dd'T'HH:mm:ssZ"))

(defn this-week? [x key]
  (time/after?
    (time-format/parse date-format (get x key))
    (time/ago (time/weeks 1))))

(defn updated-this-week? [x]
  (this-week? x "updated_at"))

(defn created-this-week? [x]
  (this-week? x "created_at"))

(defn open? [pr]
  (= "open" (get pr "state")))
(defn closed? [pr]
  (= "closed" (get pr "state")))

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

     raw-prs (pulls org repo token)
     open-prs (filter open? raw-prs)
     closed-prs (filter closed? raw-prs)
     pr-updated-this-week (filter updated-this-week? raw-prs)

     raw-events (events org repo token)
     raw-events-this-week (filter created-this-week? raw-events)
     pr-opened-this-week (filter pr-opened? raw-events-this-week)
     pr-closed-this-week (filter pr-closed? raw-events-this-week)]

    (println (str org "/" repo " pull requests:"))

    (println (str (count open-prs) " open, " (count closed-prs)) "closed")

    (println (str "last week: " (count pr-opened-this-week) " opened, " (count pr-updated-this-week) " updated, " (count pr-closed-this-week) " closed"))

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
