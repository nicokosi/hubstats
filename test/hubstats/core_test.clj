(ns hubstats.core-test
  (:require
    [clojure.java.io :refer [resource]]
    [clojure.string :refer [starts-with?]]
    [clojure.test :refer [deftest is testing]]
    [hubstats.core :refer [-main]]
    [hubstats.github :refer [github-api-events]]
    [clojure.data.json :as json])
  (:import (java.io FileNotFoundException StringWriter)))

(deftest ^:integration-test core-tests

  (letfn [(read-json [_ _ _ page]
            (when (= page 1)
              (json/read-str
                (slurp (resource "hubstats/events.json")))))]

    (testing "Display summary of pull requests"

      (binding [*out* (StringWriter.)]
        (with-redefs
          [github-api-events read-json]

          (-main "-o org -r repo")

          (is (starts-with? *out* "pull requests for org/repo")))))))