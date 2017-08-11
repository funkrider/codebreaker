(ns codebreaker.core-test
  (:require [clojure.test :refer :all]
            [codebreaker.core :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))

(deftest a-test
  (let [result (stest/summarize-results (stest/check 'codebreaker.core/score))]
    (testing "Test - Spec Generative test of score function."
      (println "hiya!" (clojure.core/pr result))
      (is (= (:total result) (:check-passed result))))))

(run-tests 'codebreaker.core-test)

(comment

  ; There is a bug in Leiningen test runner that throws an error when you try
  ; to summarise generated results. Fix is to run the tests in code first... or add
  ; :monkeypatch-clojure-test false to project.clj
  ; https://github.com/technomancy/leiningen/issues/2173

  ;(run-tests 'codebreaker.core-test)
  ;=> {:test 1, :pass 1, :fail 0, :error 0, :type :summary}

  )