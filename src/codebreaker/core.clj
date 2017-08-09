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

(s/def ::secret-and-guess
  (s/and (s/cat :secret ::code :guess ::code)
    (fn [{:keys [secret guess]}]
      (= (count secret) (count guess)))))

;; Update the score spec to include a fn key that counts the input
;; arguments from secret and then verifies that output total is less than
;; or equal to that
(s/fdef score
  :args ::secret-and-guess
  :ret (s/keys :req [::exact-matches ::loose-matches])
  :fn (fn [{{secret :secret} :args ret :ret}]
        (<= (apply + (vals ret)) (count secret))))

;; re-use this spec for both exact-matches and all-matches functions
(s/fdef matches-count
  :args ::secret-and-guess
  :ret nat-int?
  :fn (fn [{{secret :secret} :args ret :ret}]
        (<= ret (count secret)))
  )


;; ------------ Public functions ------------

(defn exact-matches [secret guess]
  (count (filter true? (map = secret guess)))
  )

(defn all-matches [secret guess]
  (apply + (vals (merge-with min (select-keys (frequencies secret) guess)
                                 (select-keys (frequencies guess) secret))))
  )

(defn score [secret guess]
  (let [exact (exact-matches secret guess)
        all   (all-matches secret guess)]
    {::exact-matches exact
     ::loose-matches (- all exact)})
  )

(defn foo [secret guess]
  {::exact-matches 0
   ::loose-matches 0})

(comment

  ;; If we are sharing the matches-count spec between two functions, you must
  ;; explicitly pass in the spec to the exercise function as it won't have
  ;; an exact name match between defn and s/fdef.
  ; (s/exercise-fn 'codebreaker.core/exact-matches 2 (s/get-spec 'codebreaker.core/matches-count))
  ; => ([([:b :w :y :b :b] [:b :g :w :g :y]) 1] [([:g :r :y :y :w] [:r :r :c :y :c]) 2])

  ;; You can instrument a method, then exercise it to reveal errors
  ;(stest/instrument 'codebreaker.core/exact-matches {:spec {'codebreaker.core/exact-matches (s/get-spec 'codebreaker.core/matches-count)}})
  ;=> [codebreaker.core/exact-matches]
  ;(s/exercise-fn 'codebreaker.core/score)

  ;; We can summarize the auto-test results for our score function
  ;(stest/summarize-results (stest/check 'codebreaker.core/score))
  ;{:sym codebreaker.core/score}
  ;=> {:total 1, :check-passed 1}

  ;; If you include these summary results in a traditional clojure.test files like this:
  ;#(= (:total %) (:check-passed %))
  ;;; or
  ;#(not (contains? % :check-failed))

  )