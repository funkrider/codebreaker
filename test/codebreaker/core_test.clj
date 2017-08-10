(ns codebreaker.core-test
  (:require [clojure.test :refer :all]
            [codebreaker.core :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))

(deftest a-test
  (let [result (stest/summarize-results (stest/check 'codebreaker.core/score))]
    (testing "Test - Spec Generative test of score function."
      (is (= (:total result) (:check-passed result))))))


(comment

  ;(run-tests 'codebreaker.core-test)
  ;=> {:test 1, :pass 1, :fail 0, :error 0, :type :summary}

  )