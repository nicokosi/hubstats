(ns hubstats.options
  (:require [clojure.set]))


(defn options [args]
  (if (empty? args)
    {:errors [:missing-org :missing-repo]}
    (let [option-map (apply hash-map (clojure.string/split args #" "))
          filtered (select-keys option-map ["--repository" "--organization" "--token" "-o" "-r" "-t"])
          with-keywords (clojure.set/rename-keys filtered
                                                 {"--repository"   :repo
                                                  "-r"             :repo,
                                                  "--organization" :org
                                                  "-o"             :org
                                                  "--token"        :token,
                                                  "-t"             :token})
          errors []]
      (if (contains? with-keywords :org)
        (if (contains? with-keywords :repo)
          with-keywords
          {:errors [:missing-repo]})
        {:errors [:missing-org]}))

    ))
