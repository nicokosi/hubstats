(ns hubstats.options
  (:require [clojure.set]))

(defn- repos [map]
  (if (nil? (:repo map))
    (clojure.string/split (:repos map) #",")
    [(:repo map)]))

(defn options [args]
  (if (empty? args)
    {:errors [:missing-org :missing-repo]}
    (let [option-map (apply array-map (clojure.string/split args #" "))
          filtered (select-keys
                     option-map
                     ["--repository" "--repositories" "--organization" "--token" "--since-days" "--since-weeks" "--since"
                      "-o" "-r" "-t" "-d" "-w" "-s"])
          with-keywords (clojure.set/rename-keys
                          filtered
                          {"--repository"   :repo
                           "-r"             :repo
                           "--repositories" :repos
                           "--organization" :org
                           "-o"             :org
                           "--token"        :token
                           "-t"             :token
                           "--since-days"   :days
                           "-d"             :days
                           "--since-weeks"  :weeks
                           "-w"             :weeks
                           "--since"        :since
                           "-s"             :since})
          with-repos (dissoc
                       (assoc with-keywords :all-repos (repos with-keywords))
                       :repo :repos)]

      (if (contains? with-repos :org)
        (if (contains? with-repos :all-repos)
          (if (> 2 (count (select-keys with-repos [:since :weeks :days])))
            with-repos
            {:errors [:several-since]})
          {:errors [:missing-repo]})
        {:errors [:missing-org]}))))

