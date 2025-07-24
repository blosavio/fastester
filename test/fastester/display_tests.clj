(ns fastester.display-tests
  (:require
   [clojure.test :refer [are
                         deftest
                         is
                         run-test
                         run-tests
                         testing]]
   [fastester.display :refer :all]))


(def ε 1e-15)


(defn εqual?
  "Given numbers `n1` and `n2`, returns `true` if they are equal within [[ε]]."
  {:UUIDv4 #uuid "6d9c49a7-dcc5-41b8-b0d4-d12f1c016e78"
   :no-doc true}
  [n1 n2]
  (<= (Math/abs (- n1 n2)) ε))


;; Note: The following tests use `εqual?` defined above.

(deftest significant-digits-tests
  (testing "greater than zero, rounding towards zero"
    (are [x y] (εqual? y (significant-digits 444 x))
      0 444
      1 400
      2 440
      3 444
      4 444))
  (testing "greater than zero, rounding away from zero"
    (are [x y] (εqual? y (significant-digits 555 x))
      0 555
      1 600
      2 560
      3 555
      4 555))
  (testing "less than zero, rounding away from zero"
    (are [x y] (εqual? y (significant-digits 0.555 x))
      0 0.555
      1 0.6
      2 0.56
      3 0.555
      4 0.555))
  (testing "less than zero, rounding towards zero"
    (are [x y] (εqual? y (significant-digits 0.444 x))
      0 0.444
      1 0.4
      2 0.44
      3 0.444
      4 0.444)))


(deftest transpose-tests
  (testing "empty matrix"
    (is (= [] (transpose [[]]))))
  (testing "basic transpostion"
    (are [x y] (= x (transpose y))
      [[:a]] [[:a]]

      [[:a]
       [:b]
       [:c]] [[:a :b :c]]

      [[:a :b :c :d]] [[:a]
                       [:b]
                       [:c]
                       [:d]]

      [[:a :b :c :d]
       [:e :f :g :h]
       [:i :j :k :l]] [[:a :e :i]
                       [:b :f :j]
                       [:c :g :k]
                       [:d :h :l]]

      [[:a :b :c]
       [:d :e :f]] [[:a :d]
                    [:b :e]
                    [:c :f]]))
  (testing "round-tripping"
    (are [x] (= x (transpose (transpose x)))
      [[:a :b :c]
       [:d :e :f]])))


(run-tests)