(ns hubstats.options-test
  (:require [clojure.test :refer :all]
            [hubstats.options :refer :all]))

(deftest options-tests
  (testing "Options with long names"
    (is (=
          {:repo "1" :org "2" :token "3" :weeks "4"}
          (options "--repository 1 --organization 2 --token 3 --since-weeks 4")))
    (is (=
          {:repo "1" :org "2" :token "3" :days "4"}
          (options "--repository 1 --organization 2 --token 3 --since-days 4")))
    (is (=
          {:repo "1" :org "2" :token "3" :since "2014"}
          (options "--repository 1 --organization 2 --token 3 --since 2014")))
    (is (=
          {:repo "1" :org "2" :token "3"}
          (options "--repository 1 --organization 2 --token 3")))
    (is (=
          {:repo "1" :org "2" :token "3"}
          (options "--organization 2 --token 3 --repository 1")))
    (is (=
          {:repo "1" :org "2"}
          (options "--organization 2 --repository 1")))
    (is (=
          {:repo "1" :org "2"}
          (options "--repository 1 --organization 2"))))
  (testing "Options with short names"
    (is (=
          {:repo "1" :org "2" :token "3" :since "4"}
          (options "-r 1 -o 2 -t 3 -s 4")))
    (is (=
          {:repo "1" :org "2" :token "3" :days "4"}
          (options "-r 1 -o 2 -t 3 -d 4")))
    (is (=
          {:repo "1" :org "2" :token "3" :weeks "4"}
          (options "-r 1 -o 2 -t 3 -w 4")))
    (is (=
          {:repo "1" :org "2" :token "3"}
          (options "-r 1 -o 2 -t 3")))
    (is (=
          {:repo "1" :org "2" :token "3"}
          (options "-o 2 -t 3 -r 1")))
    (is (=
          {:repo "1" :org "2"}
          (options "-o 2 -r 1")))
    (is (=
          {:repo "1" :org "2"}
          (options "-r 1 -o 2"))))
  (testing "Invalid options"
    (is (=
          {:errors [:missing-org :missing-repo]}
          (options "")))
    (is (=
          {:errors [:several-since]}
          (options "--repository 1 --organization 2 --token 3 --since 2016-11-01T00:00:00Z --since-days 4 --since-weeks 5")))))