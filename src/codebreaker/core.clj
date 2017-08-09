(ns codebreaker.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))

(comment
  ;Problem
  ;
  ;We want a function that accepts a secret code and a guess, and returns a score for that
  ;guess. Codes are made of 4 to 6 colored pegs, selected from six
  ;colors: [r]ed, [y]ellow, [g]reen, [c]yan, [b]lack, and [w]hite.
  ;The score is based on the number of pegs in the guess that match the secret code.
  ;A peg in the guess that matches the color of the peg in the same position in the secret
  ;code is considered an exact match, and a peg that matches a peg in a different position
  ;in the secret code is considered a loose match.
  ;
  ;For example, if the secret code is [:r :y :g :c] and the guess is [:c :y :g :b], the
  ;score would be {:codebreaker/exact-matches 2 :codebreaker/loose-matches 1}
  ;because :y and :g appear in the same positions and :c appears in a different position.
  ;
  ;We want to invoke this fn with two codes and get back a map like the one above, e.g.
  ;
  ;(score [:r :y :g :c] [:c :y :g :r])
  ;;; {:codebreaker/exact-matches 2
  ;;;  :codebreaker/loose-matches 2}
  )

(def peg? #{:y :g :r :c :w :b})

(s/def ::code (s/coll-of peg? :min-count 4 :max-count 6))

;; Define spec definitions for the desired return values
(s/def ::exact-matches nat-int?)
(s/def ::loose-matches nat-int?)

;; Update the score spec to include a fn key that counts the input
;; arguments from secret and then verifies that output total is less than
;; or equal to that
(s/fdef score
  :args (s/and (s/cat :secret ::code :guess ::code)
          (fn [{:keys [secret guess]}]
            (= (count secret) (count guess))))
  :ret (s/keys :req [::exact-matches ::loose-matches])
  :fn (fn [{{secret :secret} :args ret :ret}]
        (<= (apply + (vals ret)) (count secret))))

;; Define a basic score function that we can test against
(defn score [secret guess]
  {::exact-matches (count (filter true? (map = secret guess)))
   ::loose-matches 0})

(comment

  ;; Now lets exercise the function again to see if exact match calculation works...
  (s/exercise-fn 'codebreaker.core/score 2)
  => ([[([:b :w :y :c :g :r] [:r :b :y :b :w :b]) #:codebreaker.core{:exact-matches 1, :loose-matches 0}]
       [([:y :g :c :y :c :b] [:b :c :c :g :c :b]) #:codebreaker.core{:exact-matches 3, :loose-matches 0}]])
  )