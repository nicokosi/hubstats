(defproject hubstats "0.1.0-SNAPSHOT"
  :description "Command line tool to compute statistics for GitHub pull requests"
  :url "https://github.com/nicokosi/hubstats"
  :license {
            :name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.4"],
                 [org.clojure/data.json "2.5.0"],
                 [hato "0.9.0"],
                 [slingshot "0.12.2"],
                 [clj-time "0.15.2"],
                 [org.clojure/core.async "1.6.681"]]
  :main ^:skip-aot hubstats.core
  :plugins [[lein-ancient "0.7.0"]
            [lein-cljfmt "0.9.2"]]
  :profiles {:uberjar {:aot :all}})
