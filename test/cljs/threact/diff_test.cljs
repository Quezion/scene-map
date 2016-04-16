(ns threact.diff-test
  (:require-macros [swiss.arrows :refer [-<>]]
                   [cljs.core.async.macros :refer [go go-loop]]
                   [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var done)])
  (:require [cemerick.cljs.test :as t]
            [threact.diff :as diff]))

(deftest empty-map
         (let [state     {}
               new-state {}
               result    (diff/alterations state new-state)
               expected  {:additions {}
                          :removals  {}
                          :updates   {}}]
               (testing "Returns correct input for an empty map"
                        (is (= result expected)))))

(deftest empty-vector
         (let [state     []
               new-state []
               result    (diff/alterations state new-state)
               expected  {:additions []
                          :removals  []
                          :updates   []}]
           (testing "Returns correct input for an empty vector"
                    (is (= result expected)))))

(deftest single-addition
         (let [state     {}
               new-state {:a 1}
               result    (diff/alterations state new-state)
               expected  {:additions {:a 1}
                          :removals  {}
                          :updates   {}}]
           (testing "Returns correct input when single element is added to empty map"
                    (is (= result expected)))))

(deftest additions-flat
         (let [state     {:a 1}
               new-state {:a 1
                          :b 2}
               result    (diff/alterations state new-state)
               expected  {:additions {:b 2}
                          :removals  {}
                          :updates   {}}]
           (testing "Returns correct additions update when flat kv is added from the top level map"
                    (is (= result expected)))))

(deftest removals-flat
         (let [state     {:a 1
                          :b 2}
               new-state {:b 2}
               result    (diff/alterations state new-state)
               expected  {:additions {}
                          :removals  {:a 1}
                          :updates   {}}]
           (testing "Returns correct additions update when flat kv is removed from top level map"
                    (is (= result expected)))))

(deftest updates-flat
         (let [state     {:a 1
                          :b 2}
               new-state {:a 1
                          :b 3}
               result    (diff/alterations state new-state)
               expected  {:additions {}
                          :removals  {}
                          :updates   {:b 3}}]
           (testing "Returns correct additions update when flat kv is updated in top level map"
                    (is (= result expected)))))

(deftest multiple-flat
         (let [state     {:a 1
                          :b 2
                          :c 3}
               new-state {:a 0
                          :c 3
                          :d 4}
               result    (diff/alterations state new-state)
               expected  {:additions {:d 4}
                          :removals  {:b 2}
                          :updates   {:a 0}}]
           (testing "Returns correct results for multiple alterations to a flat map"
                    (is (= result expected)))))

(deftest additions-nested
         (let [state     {}
               new-state {:a {:aa [4 5 6]}}
               result    (diff/alterations state new-state)
               expected  {:additions {:a {:aa [4 5 6]}}
                          :removals  {}
                          :updates   {}}]
           (testing "Returns correct additions update when nested kv is added to top level map"
                    (is (= result expected)))))

(deftest removals-nested
         (let [state     {:a {:aa [4 5 6]}
                          :b 1}
               new-state {:b 1}
               result    (diff/alterations state new-state)
               expected  {:additions {}
                          :removals  {:a {:aa [4 5 6]}}
                          :updates   {}}]
           (testing "Returns correct additions update when nested kv is removed from top level map"
                    (is (= result expected)))))

(deftest updates-nested
         (let [state     {:a {:aa [1 2 3]}}
               new-state {:a {:aa [4 5 6]}}
               result    (diff/alterations state new-state)
               expected  {:additions {}
                          :removals  {}
                          :updates   {:a {:aa [4 5 6]}}}]
           (testing "Returns correct updates when vector in nested kv is changed"
                    (is (= result expected)))))

(deftest multiple-nested
         (let [state     {:a {:aa [1 2 3]}
                          :b {:bb [3 4 5]}
                          :c {:cc [6 7 8]}}
               new-state {:a {:aa [4 5 6]}
                          :c {:cc [6 7 8]}
                          :d {:dd [9 10 11]}}
               result    (diff/alterations state new-state)
               expected  {:additions {:d {:dd [9 10 11]}}
                          :removals  {:b {:bb [3 4 5]}}
                          :updates   {:a {:aa [4 5 6]}}}]
           (testing "Returns correct results with multiple alterations performed on nested maps"
                    (is (= result expected)))))

(deftest map-in-vector
         (let [state     [1 2 {:a {:aa [1 2 3]
                                   :bb [4 5 6]}}]
               new-state [1 2  {:a {:aa [2 3 4]}
                                :c {:cc [7 8 9]}}]
               result    (diff/alterations state new-state)
               expected  {:additions []
                          :removals  []
                          :updates   [1 2 {:a {:aa [2 3 4]}
                                           :c {:cc [7 8 9]}}]}]
           (testing "Returns correct results when nested map is altered inside of a vector"
                    (is (= result expected)))))

