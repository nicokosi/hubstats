(defproject hubstats "0.1.0-SNAPSHOT"
  :description "Command line tool to compute statistics for GitHub pull requests"
  :url "https://github.com/nicokosi/hubstats"
  :license {
            :name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.3"],
                 [org.clojure/data.json "2.4.0"],
                 [hato "0.8.2"],
                 [slingshot "0.12.2"],
                 [clj-time "0.15.2"],
                 [org.clojure/core.async "1.3.622"]]
  :main ^:skip-aot hubstats.core
  :plugins [[lein-ancient "0.7.0"]
            [lein-cljfmt "0.8.0"]]
  :profiles {:uberjar {:aot :all}})
