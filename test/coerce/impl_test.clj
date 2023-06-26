(ns coerce.impl-test
  (:require [clojure.test :refer :all])
  (:require [coerce.core :refer [coerce]]
            [coerce.dates :refer [set-format-options!]]
            :reload-all)
  (:import  [java.util Locale]))

(defn reset-options!
  [f]
  (set-format-options!)
  (f))

(use-fixtures :each reset-options!)

(defn coerced?
  [x y]
  (cond
    (string? y) (= (coerce x :string) y)
    (vector? y) (let [[y pattern] y]
                  (= (coerce x :string :pattern pattern) y))
    :else (throw (ex-info "Can't check date parsing" {:x x :y y}))))

(deftest date-parsing
  (testing "Basic parsing"
    (set-format-options!)
    (are [x y] (coerced? x y)
      #inst "2000-01-01" "2000-01-01"
      #inst "2000-01-02" "2000-01-02"
      ;;
      ;; Default Locale/US
      #inst "2000-03-01" ["01 Mar"        :dd-mmm]
      #inst "2000-03-01" ["01 Mar 2000"   :dd-mmm-yyyy]
      #inst "2000-03-01" ["01 March"      :dd-mmmm]
      #inst "2000-03-01" ["01 March 2000" :dd-mmmm-yyyy]))

  (testing "Testing with Locales."
    (set-format-options! "en_AU")
    (are [x y] (coerced? x y)
      #inst "2000-03-01" "2000-03-01"
      #inst "2000-03-01" ["01 Mar."       :dd-mmm]
      #inst "2000-03-01" ["01 Mar. 2000"  :dd-mmm-yyyy]
      #inst "2000-03-01" ["01 March"      :dd-mmmm]
      #inst "2000-03-01" ["01 March 2000" :dd-mmmm-yyyy])))

(deftest test-coerce-string
  (are [x y] (= x (coerce y
                          :string
                          :upper?   true
                          :alpha?   true
                          :numeric? true))
    nil      "  ~ $ . %"
    "ABC123" " a b   c 1  ~ $ 2  . % 3"
    "ABC123" "abc123"
    "ABC123" "ABC123"
    "ABC123" "ABC-123"
    "ABC123" " ABC123+"))
