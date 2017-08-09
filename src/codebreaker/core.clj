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
  :args (s/and (s/cat :secret ::code :guess ::code)
          (fn [{:keys [secret guess]}]
            (= (count secret) (count guess)))))

(comment

  ;; Now when we generate args the secret and guess are of equal length
  (s/exercise (:args (s/get-spec 'codebreaker.core/score)) 2)
  => ([([:c :g :r :w] [:w :b :y :g]) {:secret [:c :g :r :w], :guess [:w :b :y :g]}]
       [([:g :g :c :c :y :w] [:b :b :y :b :y :g]) {:secret [:g :g :c :c :y :w], :guess [:b :b :y :b :y :g]}])
  )