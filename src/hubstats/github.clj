(ns hubstats.github
  (:require
    [clojure.data.json :as json]
    [clj-time.core :as time]
    [clj-time.format :as time-format]
    [clj-http.client :as http-client]
    [slingshot.slingshot :refer [try+]])
  (:import
    (java.net UnknownHostException)
    (java.io FileNotFoundException IOException)))

(def date-format (time-format/formatter "yyyy-MM-dd'T'HH:mm:ssZ"))

(defn github-api-events [org repo token page]
  (let [url (str "https://api.github.com/repos/" org "/" repo "/events?access_token=" token "&page=" page)]
    (json/read-str
      ((http-client/get url {"Accept" "application/vnd.github.v3+json"}) :body))))

(defn events
  ([org repo token page]
   (when (< page 100)
     (try+
       (github-api-events org repo token page)
       (catch [:status 422] {} nil)
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

(defn- sort-map-by-value [m]
  (into (sorted-map-by (fn [key1 key2]
                         (compare [(get m key2) key2]
                                  [(get m key1) key1])))
        m))

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
        new-raw-events (filter #(created-since? % date) (events org repo token))]
    (assoc {}
      :request {:org   org
                :repo  repo
                :since (time-format/unparse (time-format/formatters :date-time-no-ms) date)}
      :opened {
               :count (->> (filter pr-opened? new-raw-events)
                           (filter #(= "opened" (get-in % ["payload" "action"])))
                           count)
               :count-by-author
                      (sort-map-by-value
                        (->> (filter pr-opened? new-raw-events)
                             (filter #(= "opened" (get-in % ["payload" "action"])))
                             (map #(get-in % ["actor" "login"]))
                             frequencies))
               }
      :commented {
                  :count (->> (filter pr-review-comment-evt? new-raw-events)
                              (filter created?)
                              count)
                  :count-by-author
                         (sort-map-by-value
                           (->> (filter pr-review-comment-evt? new-raw-events)
                                (filter created?)
                                (map #(get-in % ["actor" "login"]))
                                frequencies))
                  }
      :closed {:count (count (filter pr-closed? new-raw-events))
               :count-by-author
                      (sort-map-by-value
                        (->> (filter pr-event? new-raw-events)
                             (filter #(= "closed" (get-in % ["payload" "action"])))
                             (map #(get-in % ["actor" "login"]))
                             frequencies))}
      )))