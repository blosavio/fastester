(ns fastester.display-tests
  (:require
   [clojure.test :refer [are
                         deftest
                         is
                         run-test
                         run-tests
                         testing]]
   [fastester.display :refer :all]))


(deftest link-to-original-data-tests
  (is (=
       (link-to-original-data {:fastester/metadata {:index 99
                                                    :version 101}}
                              {:results-url "http://example.com/"
                               :results-directory "foo/"}
                              "Hooray!")
       [:a
        {:href "http://example.com/foo/version 101/test-99.edn"}
        "Hooray!"])))


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


(deftest col-tests
  (is (= (let [datum {:mean [99E0 1 2]
                      :variance [101E0 3 4]
                      :fastester/metadata {:arg 1000
                                           :version "12"}}
               opt {:results-url "https://example.com/"
                    :results-directory "baz/"}]
           (col datum opt))
         [:td
          [:a
           {:href "https://example.com/baz/version 12/test-.edn"}
           "9.9e+01±1.0e+01"]])))


(deftest version-sort-by-comparator-and-sort-table-rows-tests
  (is (=(let [opt {:sort-comparator #(> (Integer/parseInt %1)
                                        (Integer/parseInt %2))}
             c (version-sort-by-comparator opt)]
         (sort-table-rows-by-version [[[:td "2"] [:td "bar"]]
                                      [[:td "3"] [:td "baz"]]
                                      [[:td "1"] [:td "foo"]]]
                                     opt))
         [[[:td "3"] [:td "baz"]]
          [[:td "2"] [:td "bar"]]
          [[:td "1"] [:td "foo"]]])))


(deftest img-filepath-tests
  (is (= (img-filepath 99 101 {:html-directory "foo/"
                               :img-subdirectory "baz/"})
         "foo/baz/group-99-fexpr-101.svg")))


(deftest integer-keyed-map->vector-tests
  (testing "valid, empty map"
    (is (= [] (integer-keyed-map->vector {}))))
  (testing "valid, in-order"
    (is (= ["foo" "bar" "baz"]
           (integer-keyed-map->vector {0 "foo"
                                       1 "bar"
                                       2 "baz"}))))
  (testing "valid, out-of-order"
    (is (= ["foo" "bar" "baz"]
           (integer-keyed-map->vector {1 "bar"
                                       2 "baz"
                                       0 "foo"}))))
  (testing "invalid, no zero"
    (is (thrown? Exception
                 (integer-keyed-map->vector {1 "bar"
                                             2 "baz"}))))
  (testing "invalid, keys not all integers"
    (is (thrown? Exception
                 (integer-keyed-map->vector {0 "foo"
                                             1 "bar"
                                             "two" "baz"}))))
  (testing "invalide, keys not incremental"
    (is (thrown? Exception
                 (integer-keyed-map->vector {0 "foo"
                                             1 "bar"
                                             3 "baz"})))))


(deftest nested-maps->vecs-tests
  (is (= (nested-maps->vecs {99 {:x {0 :datum-1
                                     1 :datum-2
                                     2 :datum-3}
                                 :y {0 :datum-4
                                     1 :datum-5
                                     2 :datum-6}}})
         {99 {:x [:datum-1
                  :datum-2
                  :datum-3]
              :y [:datum-4
                  :datum-5
                  :datum-6]}})))


(deftest toc-tests
  (is (= (toc {"group-1" nil
               "group-2" nil
               "group-3" nil})
         [:div
          [:a {:href "#group-0"} "group-1"]
          [:br]
          [:a {:href "#group-1"} "group-2"]
          [:br]
          [:a {:href "#group-2"} "group-3"]])))


(deftest inject-js-tests
  (is (= (let [html "<head><body>..."
               js-filepath "https://example.com/foo.js"]
           (inject-js html js-filepath))
         "<head><script src=\"https://example.com/foo.js\" type=\"text/javascript\"></script><body>...")))


#_(run-tests)

