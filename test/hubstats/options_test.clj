(ns hubstats.options-test
  (:require [clojure.test :refer :all]
            [hubstats.options :refer :all]))

(deftest options-tests
  (testing "Options with long names"
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
    (is (= {:errors [:missing-org :missing-repo]} (options ""))))
  )