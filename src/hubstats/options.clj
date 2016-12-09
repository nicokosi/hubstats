(ns hubstats.options
  (:require [clojure.set]))


(defn options [args]
  (if (empty? args)
    {:errors [:missing-org :missing-repo]}
    (let [option-map (apply array-map (clojure.string/split args #" "))
          filtered (select-keys
                     option-map
                     ["--repository" "--organization" "--token" "--since-days" "--since-weeks" "--since"
                      "-o" "-r" "-t" "-d" "-w" "-s"])
          with-keywords (clojure.set/rename-keys
                          filtered
                          {"--repository"   :repo
                           "-r"             :repo
                           "--organization" :org
                           "-o"             :org
                           "--token"        :token
                           "-t"             :token
                           "--since-days"   :days
                           "-d"             :days
                           "--since-weeks"  :weeks
                           "-w"             :weeks
                           "--since"        :since
                           "-s"             :since})]
      (if (contains? with-keywords :org)
        (if (contains? with-keywords :repo)
          (if (> 2 (count (select-keys with-keywords [:since :weeks :days])))
            with-keywords
            {:errors [:several-since]})
          {:errors [:missing-repo]})
        {:errors [:missing-org]}))))
