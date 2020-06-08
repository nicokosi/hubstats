(ns hubstats.options-test
  (:require [clojure.test :refer [deftest, is, testing]]
            [hubstats.options :refer [options]]))

(deftest ^:unit-test options-tests
  (testing "Options with long names"
    (is (=
         {:org "o" :all-repos ["r"] :token "t" :weeks "4"}
         (options "--organization o --repository r --token t --since-weeks 4")))
    (is (=
         {:org "o" :all-repos ["r"] :token "t" :days "4"}
         (options "--organization o --repository r --token t --since-days 4")))
    (is (=
         {:org "o" :all-repos ["r"] :token "t" :since "2014"}
         (options "--organization o --repository r --token t --since 2014")))
    (is (=
         {:org "o" :all-repos ["r"] :token "t"}
         (options "--repository r --organization o --token t")))
    (is (=
         {:org "o" :all-repos ["r"] :token "t"}
         (options "--organization o --token t --repository r")))
    (is (=
         {:org "o" :all-repos ["r"]}
         (options "--organization o --repository r")))
    (is (=
         {:org "o" :all-repos ["r1" "r2"]}
         (options "--organization o --repositories r1,r2")))
    (is (=
         {:org "o" :all-repos ["r"]}
         (options "--organization o --repositories r"))))
  (testing "Options with short names"
    (is (=
         {:org "o" :all-repos ["r"] :token "t" :weeks "4"}
         (options "-o o -r r -t t -w 4")))
    (is (=
         {:org "o" :all-repos ["r"] :token "t" :days "4"}
         (options "-o o -r r -t t -d 4")))
    (is (=
         {:org "o" :all-repos ["r"] :token "t" :since "2014"}
         (options "-o o -r r -t t -s 2014")))
    (is (=
         {:org "o" :all-repos ["r"] :token "t"}
         (options "-r r -o o -t t")))
    (is (=
         {:org "o" :all-repos ["r"] :token "t"}
         (options "-o o -t t -r r")))
    (is (=
         {:org "o" :all-repos ["r"]}
         (options "-o o -r r")))
    (is (=
         {:org "o" :all-repos ["r"]}
         (options "-o o -r r"))))
  (testing "Invalid options"
    (is (=
         {:errors [:missing-org :missing-repo]}
         (options "")))
    (is (=
         {:errors [:several-since]}
         (options "--repository r --organization o --token t --since 2016-11-01T00:00:00Z --since-days 4 --since-weeks 5")))))