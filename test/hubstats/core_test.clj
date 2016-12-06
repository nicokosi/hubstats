(ns hubstats.core-test
  (:require
    [clojure.java.io :as io]
    [clojure.test :refer :all]
    [hubstats.core :refer :all]
    [clojure.data.json :as json])
  (:import (java.io FileNotFoundException StringWriter)))

(deftest ^:integration-test core-tests

  (letfn [(read-json [_ _ _ page]
            (when (= page 1)
              (json/read-str
                (slurp (io/resource "hubstats/events.json")))))]

    (testing "Display summary of pull requests"

      (binding [*out* (StringWriter.)]
        (with-redefs
          [github-api-events read-json]

          (-main "-o org -r repo")

          (is (clojure.string/starts-with? *out* "pull requests for org/repo")))))

    (testing "Display summary of pull requests with correct counters"

      (binding [*out* (StringWriter.)]
        (with-redefs
          [github-api-events read-json]

          (-main "-o softwarevidal -r arthur -s 2016-12-04T00:00:00Z")

          (is (clojure.string/starts-with? *out* "pull requests for softwarevidal/arthur"))
          (is (clojure.string/includes? *out* "1 opened / 0 closed")))))

    (testing "No errors if GitHub returns no events"

      (binding [*out* (StringWriter.)]
        (with-redefs
          [github-api-events (fn [_ _ _ _] (json/read-str ""))]

          (-main "-o org -r repo")

          (is (clojure.string/starts-with? *out* "pull requests for org/repo")))))

    (testing "No errors if GitHub is unreachable"

      (binding [*out* (StringWriter.)]
        (with-redefs
          [github-api-events (fn [_ _ _ _] (throw (FileNotFoundException. "not found")))]

          (-main "-o org -r repo")

          (is (clojure.string/starts-with? *out* "pull requests for org/repo")))))))