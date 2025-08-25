(defproject hubstats "0.1.0-SNAPSHOT"
  :description "Command line tool to compute statistics for GitHub pull requests"
  :url "https://github.com/nicokosi/hubstats"
  :license {
            :name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.12.2"],
                 [org.clojure/data.json "2.5.1"],
                 [hato "1.0.0"],
                 [slingshot "0.12.2"],
                 [clj-time "0.15.2"],
                 [org.clojure/core.async "1.8.741"]]
  :main ^:skip-aot hubstats.core
  :plugins [[lein-ancient "0.7.0"]
            [lein-cljfmt "0.9.2"]]
  :profiles {:uberjar {:aot :all}})
