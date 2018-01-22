(defproject hubstats "0.1.0-SNAPSHOT"
  :description "Command line tool to compute statistics for GitHub pull requests"
  :url "https://github.com/nicokosi/hubstats"
  :license {
            :name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"],
                 [org.clojure/data.json "0.2.6"],
                 [clj-http "3.7.0"],
                 [slingshot "0.12.2"],
                 [clj-time "0.14.2"],
                 [org.clojure/core.async "0.4.474"]]
  :main ^:skip-aot hubstats.core
  :profiles {:uberjar {:aot :all}})
