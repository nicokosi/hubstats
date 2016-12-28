(ns hubstats.github-test
  (:require
    [clojure.java.io :as io]
    [clojure.test :refer :all]
    [hubstats.github :refer [github-api-events pr-stats]]
    [clojure.data.json :as json]))

(deftest github-tests

  (letfn [(read-json [_ _ _ page]
            (when (= page 1)
              (json/read-str
                (slurp (io/resource "hubstats/events.json")))))]

    (testing "Summary of pull requests when some GitHub events"
      (with-redefs
        [github-api-events read-json]
        (is (=
              (pr-stats {:org "myorg" :repo "myrepo" :since "2016-12-04T00:00:00Z"})
              {
               :request   {:org   "myorg"
                           :repo  "myrepo"
                           :since "2016-12-04T00:00:00Z"}
               :opened    {
                           :count           3
                           :count-by-author (array-map "bob" 2 "carol" 1)
                           }
               :commented {
                           :count           1
                           :count-by-author {"carol" 1}
                           }
               :closed    {
                           :count           1
                           :count-by-author {"eve" 1}
                           }
               })))))

  (testing "Summary of pull requests when no GitHub events"
    (with-redefs
      [github-api-events (fn [_ _ _ _] (json/read-str "{}"))]
      (is (=
            (pr-stats {:org "myorg" :repo "myrepo" :since "2016-12-04T00:00:00Z"})
            {
             :request   {:org   "myorg"
                         :repo  "myrepo"
                         :since "2016-12-04T00:00:00Z"}
             :opened    {
                         :count           0
                         :count-by-author {}
                         }
             :commented {
                         :count           0
                         :count-by-author {}
                         }
             :closed    {
                         :count           0
                         :count-by-author {}
                         }
             })))))
