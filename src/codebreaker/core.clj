(ns codebreaker.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))

;; clojure.spec requires clojure >= 1.9.0 (currently alpha)
;;  and clojure.spec.test depends upon test.check for various methods including exercise.
;; For leiningen your project.clj must include these dependencies
;; :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
;;  [org.clojure/test.check "0.9.0"]]

;; def a simple set with keywords for use as a spec validator
(def peg? #{:y :g :r :c :w :b})

;; use spec/def to create a spec that defines a collection
;; of keywords that belong to the set defined in peg?
(s/def ::code (s/coll-of peg? :min-count 4 :max-count 6))

;; use spec fdef to create a spec that defines a function "score"
;; that accepts a vector of two arguments of type defined in ::code.
;; Give the two args identifiers :secret and :guess. Input is "conformed" to these
;; keywords so you can refer to them in further spec definitions
;; and in invalid exception explanations.
(s/fdef score
  :args (s/cat :secret ::code :guess ::code))

(comment

  ;; You can retrieve the spec object using s/get-spec for the function score
  ;; and then get the args spec from it using the keyword :args
  ;; If you apply a sample input [[:y :y :g :r] [:b :w :c :g]] to this :args spec
  ;; if will be "conformed" to the map below using the keywords :secret and :guess.
  (s/conform (:args (s/get-spec 'codebreaker.core/score)) [[:y :y :g :r] [:b :w :c :g]])
  => {:secret [:y :y :g :r], :guess [:b :w :c :g]}

  ;; Generate some sample input and view the output from the spec using s/exercise
  (s/exercise (:args (s/get-spec 'codebreaker.core/score)) 2)
  => ([([:c :g :c :c :c :c] [:b :w :c :g]) {:secret [:c :g :c :c :c :c], :guess [:b :w :c :g]}]
    [([:g :w :g :w] [:r :g :w :c :c :r]) {:secret [:g :w :g :w], :guess [:r :g :w :c :c :r]}])

  )