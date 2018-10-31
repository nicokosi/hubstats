(ns hubstats.github
)

#_(def date-format (time-format/formatter "yyyy-MM-dd'T'HH:mm:ssZ"))

#_(defn github-api-events [org repo token page]
  (let [url (str "https://api.github.com/repos/" org "/" repo "/events?access_token=" token "&page=" page)]
    (json/read-str
      ((http-client/get url {"Accept" "application/vnd.github.v3+json"}) :body))))

#_(defn events
  ([org repo token page]
   (when (< page 100)
     (try+
       (github-api-events org repo token page)
       (catch [:status 422] {} nil))))
  ([org repo token page acc]
   (let [events (events org repo token page)]
     (if (nil? events)
       acc
       (recur org repo token (inc page) (concat acc events)))))
  ([org repo token]
   (events org repo token 1 [])))

#_(defn- since? [map key date]
  (time/after?
    (time-format/parse date-format (get map key))
    date))

#_(defn- created-since? [event date]
  (since? event "created_at" date))

#_(defn- action [event]
  (get-in event ["payload" "action"]))

#_(defn- pr-review-comment-evt? [event]
  (= "PullRequestReviewCommentEvent" (get event "type")))

#_(defn- pr-event? [event]
  (= "PullRequestEvent" (get event "type")))

#_(defn- closed? [event]
  (= (action event) "closed"))

#_(defn- opened? [event]
  (= (action event) "opened"))

#_(defn- created? [review-comment]
  (= (get-in review-comment ["payload" "action"]) "created"))

#_(defn- sort-map-by-value [m]
  (into (sorted-map-by (fn [key1 key2]
                         (compare [(get m key2) key2]
                                  [(get m key1) key1])))
        m))

#_(defn pr-stats [opts repo]
  (let [org (opts :org)
        token (opts :token)
        since-date (get opts :since nil)
        days (Integer/parseInt (get opts :days "0"))
        weeks (Integer/parseInt (get opts :weeks "1"))
        date (if since-date
               (time-format/parse date-format since-date)
               (time/ago (if (> days 0) (time/days days) (time/weeks weeks))))
        new-raw-events (filter #(created-since? % date) (events org repo token))
        pr-events (filter pr-event? new-raw-events)
        pr-review-comment-events (filter pr-review-comment-evt? new-raw-events)]
    (assoc {}
      :request {:org   org
                :repo  repo
                :since (time-format/unparse (time-format/formatters :date-time-no-ms) date)}
      :opened {
               :count (->> (filter opened? pr-events)
                           count)
               :count-by-author
                      (sort-map-by-value
                        (->> (filter opened? pr-events)
                             (map #(get-in % ["actor" "login"]))
                             frequencies))}
      :comments {
                 :count (->> pr-review-comment-events
                             (filter created?)
                             count)
                 :count-by-author
                        (sort-map-by-value
                          (->> pr-review-comment-events
                               (filter created?)
                               (map #(get-in % ["actor" "login"]))
                               frequencies))}
      :commented {
                  :count
                  (->> pr-review-comment-events
                       (filter created?)
                       (map #(get-in % ["payload" "pull_request" "id"]))
                       set
                       count)}
      :closed {:count (count (filter closed? pr-events))
               :count-by-author
                      (sort-map-by-value
                        (->> (filter closed? pr-events)
                             (map #(get-in % ["actor" "login"]))
                             frequencies))})))