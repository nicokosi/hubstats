(defproject hubstats "0.1.0-SNAPSHOT"
  :description "Command line tool to compute statistics for GitHub pull requests"
  :url "https://github.com/nicokosi/hubstats"
  :license {
            :name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"],
                 [org.clojure/data.json "1.0.0"],
                 [clj-http "3.10.2"],
                 [slingshot "0.12.2"],
                 [clj-time "0.15.2"],
                 [org.clojure/core.async "1.3.610"]]
  :main ^:skip-aot hubstats.core
  :plugins [[lein-ancient "0.6.15"]
            [lein-cljfmt "0.7.0"]]
  :profiles {:uberjar {:aot :all}})
