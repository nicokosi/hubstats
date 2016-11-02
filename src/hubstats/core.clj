(ns hubstats.core
  (:require
    [clojure.data.json :as json]
    [clj-time.core :as time]
    [clj-time.format :as time-format]))

(defn pulls [org repo token]
  (json/read-str
    (slurp
      (str "https://api.github.com/repos/" org "/" repo "/pulls?state=all&access_token=" token))))

(defn events [user org token]
  (json/read-str
    (slurp
      (str "https://api.github.com/users/" user "/events/orgs/" org "?access_token=" token))))

(def date-format (time-format/formatter "yyyy-MM-dd'T'HH:mm:ssZ"))

(defn this-week? [pr]
  (time/after?
    (time-format/parse date-format (get pr "updated_at"))
    (time/ago (time/weeks 1))))

(defn open? [pr]
  (= "open" (.get pr "state")))

(defn closed? [pr]
  (= "closed" (.get pr "state")))

(defn -main [& args]
  (let
    [org (first args)
     repo (second args)
     token (nth args 2 "")
     raw-prs (pulls org repo token)
     open-prs (filter open? raw-prs)
     closed-prs (filter closed? raw-prs)
     updated-this-week (filter this-week? raw-prs)]
    (println "GitHub repository " org "/" repo ":")
    (println (count open-prs) "open pull requests")
    (println (count closed-prs) "closed pull requests")
    (println (count updated-this-week) "pull requests updated this week")))