(ns hubstats.github
  (:require
    [clojure.data.json :as json]
    [clj-time.core :as time]
    [clj-time.format :as time-format])
  (:import
    (java.net UnknownHostException)
    (java.io FileNotFoundException IOException)))

(def date-format (time-format/formatter "yyyy-MM-dd'T'HH:mm:ssZ"))

(defn github-api-events [org repo token page]
  (json/read-str
    (slurp
      (str
        "https://api.github.com/repos/" org "/" repo "/events" "?access_token=" token "&page=" page))))

(defn events
  ([org repo token page]
   (when (< page 100)
     (try
       (github-api-events org repo token page)
       (catch IOException e nil)                            ;TODO Check for HTTP 422? Exception message contains "Server returned HTTP response code: 422 for URL"
       (catch UnknownHostException e (throw e))
       (catch FileNotFoundException e (throw e)))))
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

(defn- action [event]
  (get-in event ["payload" "action"]))

(defn- pr-closed? [event]
  (= (action event) "closed"))

(defn- pr-opened? [event]
  (= (action event) "opened"))

(defn- pr-review-comment-evt? [event]
  (= "PullRequestReviewCommentEvent" (get event "type")))

(defn- pr-event? [event]
  (= "PullRequestEvent" (get event "type")))

(defn- created? [review-comment]
  (= (get-in review-comment ["payload" "action"]) "created"))

(defn pr-stats [opts]
  (let [org (opts :org)
        repo (opts :repo)
        token (opts :token)
        since-date (get opts :since nil)
        days (Integer/parseInt (get opts :days "0"))
        weeks (Integer/parseInt (get opts :weeks "1"))
        date (if since-date
               (time-format/parse date-format since-date)
               (time/ago (if (> days 0) (time/days days) (time/weeks weeks))))
        raw-events (events org repo token)
        new-raw-events (filter #(created-since? % date) raw-events)]
    (assoc {}
      :request {:org   org
                :repo  repo
                :since date}
      :opened {
               :count (count (filter pr-opened? new-raw-events))
               :count-by-author
                      (reverse
                        (sort-by last
                                 (->> (filter pr-event? raw-events)
                                      (filter #(since? % "created_at" date))
                                      (filter #(= "opened" (get-in % ["payload" "action"])))
                                      (map #(get-in % ["actor" "login"]))
                                      frequencies)))
               }
      :reviewed {
                 :count-by-author
                 (reverse
                   (sort-by last
                            (->> (filter pr-review-comment-evt? raw-events)
                                 (filter created?)
                                 (filter #(since? % "created_at" date))
                                 (map #(get-in % ["actor" "login"]))
                                 frequencies)))
                 }
      :closed {:count           (count (filter pr-closed? new-raw-events))
               :count-by-author (reverse
                                  (sort-by last
                                           (->> (filter pr-event? raw-events)
                                                (filter #(since? % "created_at" date))
                                                (filter #(= "closed" (get-in % ["payload" "action"])))
                                                (map #(get-in % ["actor" "login"]))
                                                frequencies)))}
      )))