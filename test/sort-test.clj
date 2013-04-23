(ns hello-clj-test
  (:use 
    clojure.test
    hello-clj
  ))

(defn sort-test [xsort] (doseq
  [x [
    []
    [1]
    [2 1]
    [3 1 2]
    (take 10 (repeatedly #(rand-int 100)))
    ]]
  (println (bubble-sort x))
  (is (= (bubble-sort x) (sort x)))
 ))

(deftest test-bubble-sort
  (testing "bubble-sort" (sort-test bubble-sort)))
