(ns coerce.core-test
  (:require [clojure.test :refer [deftest testing is run-tests]]
            [coerce.core :refer [coerce]]
            [coerce.constants :refer [add-date-patterns!]]))

(deftest coercions-to-nil
  (testing "Blank Strings"

    (is (-> (coerce "\t \t  " :string) nil?))

    (is (-> (coerce "" :string)      nil?))
    (is (-> (coerce "" :boolean)     nil?))
    (is (-> (coerce "" :integer)     nil?))
    (is (-> (coerce "" :big-integer) nil?))
    (is (-> (coerce "" :date)        nil?))
    (is (-> (coerce "" :uuid)        nil?))
    ;;
    (is (-> (coerce "  " :string)      nil?))
    (is (-> (coerce "  " :boolean)     nil?))
    (is (-> (coerce "  " :integer)     nil?))
    (is (-> (coerce "  " :big-integer) nil?))
    (is (-> (coerce "  " :date)        nil?))))

(deftest boolean-coercions
  (testing "Strings to Booleans"
    (is (-> (coerce "t" :boolean) true?))
    (is (-> (coerce "f" :boolean) false?))
    ;;
    (is (-> (coerce "y"   :boolean) true?))
    (is (-> (coerce "ye"  :boolean) true?))
    (is (-> (coerce "yes" :boolean) true?))
    ;;
    (is (-> (coerce "n"  :boolean) false?))
    (is (-> (coerce "no" :boolean) false?))

    (is (-> (coerce "T" :boolean) true?))
    (is (-> (coerce "F" :boolean) false?))
    ;;
    (is (-> (coerce "Y"   :boolean) true?))
    (is (-> (coerce "YE"  :boolean) true?))
    (is (-> (coerce "Yes" :boolean) true?))
    ;;
    (is (-> (coerce "N"  :boolean) false?))
    (is (-> (coerce "No" :boolean) false?))
    ;;
    ;; Watch out this returns TRUE
    (is (-> (coerce "not" :boolean) true?)))

  (testing "Booleans to Strings"
    (is (-> (coerce true  :string) (= "true")))
    (is (-> (coerce false :string) (= "false")))
    ;;
    (is (-> (coerce true  :string :t "YES") (= "YES")))
    (is (-> (coerce false :string :f "NO")  (= "NO"))))

  (testing "Booleans to Booleans"
    (is (-> (coerce true  :boolean) true?))
    (is (-> (coerce false :boolean) false?)))

  (testing "Nil to Boolean"
    ;;
    ;; Watch out, don't know what nil should be so we just return it.
    (is (-> (coerce nil :boolean) nil?))))

(deftest number-coercions
  (testing "Numbers to Strings"
    (is (-> (coerce (int  42) :string) (= "42")))
    (is (-> (coerce (long 42) :string) (= "42"))))

  (testing "Strings To Number values"

    (is (-> (coerce "42" :integer)     (= 42)))
    (is (-> (coerce "42" :big-integer) (= 42)))
    ;;
    (is (-> (coerce "42.42" :integer)     (= 42)))
    (is (-> (coerce "42.42" :big-integer) (= 42)))
    ;;
    #?(:clj (do
              (is (-> (coerce "42.42" :integer     :strict? true) nil?))
              (is (-> (coerce "42.42" :big-integer :strict? true) nil?))
              ;;
              (is (thrown? NumberFormatException (coerce "42.42" :integer     :strict? true :throw? true)))
              (is (thrown? NumberFormatException (coerce "42.42" :big-integer :strict? true :throw? true))))
       :cljs (do
               (is (-> (coerce "42.42" :integer     :strict? true) (= 42)))
               (is (-> (coerce "42.42" :big-integer :strict? true) (= 42)))
               ;;
               ;; FIXME: How does exception handling work in javascript?
               #_(is (thrown? NumberFormatException (coerce "42.42" :integer     :strict? true :throw? true)))
               #_(is (thrown? NumberFormatException (coerce "42.42" :big-integer :strict? true :throw? true)))))))



#?(:clj
   (deftest date-coercions
     (testing "Dates to Strings"
       (is (= (coerce #inst "2016-10-10" :string) "2016-10-10"))
       (is (= (coerce #inst "2016-10-10" :string :pattern :dd-mm-yyyy) "10-10-2016"))
       (is (= (coerce #inst "2016-10-10" :string :pattern :dd|mm|yyyy) "10/10/2016"))
       ;;
       (is (= (coerce "2016-10-10" :date)                      #inst "2016-10-10"))
       (is (= (coerce "10-10-2016" :date :pattern :dd-mm-yyyy) #inst "2016-10-10"))
       (is (= (coerce "10/10/2016" :date :pattern :dd|mm|yyyy) #inst "2016-10-10")))

     (add-date-patterns! {:24HMM "HHmm"
                          :dy-dd "E dd"})
     (is (= (coerce #inst "2016-10-10T11:11:00.000-00:00" :string :pattern :24HMM) "1111"))
     (is (= (coerce #inst "2016-10-10"                    :string :pattern :dy-dd) "Mon. 10"))))

(deftest keyword-coercions
  (testing "Strings to keywords"
    (is (= (coerce "this-is-a-test" :keyword) :this-is-a-test))
    (is (= (coerce "This-Is-A-Test" :keyword :strict? true) :This-Is-A-Test))
    ;;
    (is (= (coerce "This is a Test"          :keyword) :this-is-a-test))
    (is (= (coerce "This   \tIs A  \t  Test" :keyword) :this-is-a-test)))

  (testing "Keywords to Strings"
    (is (= (coerce :this :string) "this"))
    (is (= (coerce :whatever/this :string) "whatever/this")))

  (testing "Keywords to Booleans"
    (is (= (coerce :this :boolean) true))))

(deftest symbol-coercions
  (testing "Symbols to Strings"
    (is (= (coerce 'this-is-a-test :string) "this-is-a-test"))))

(deftest uuid-coercions
  (testing "Strings to UUIDs"
    (is (= (coerce "cf20e1dc-861a-11e6-ae22-56b6b6499611" :uuid) #uuid "cf20e1dc-861a-11e6-ae22-56b6b6499611"))
    (is (= (coerce "CF20E1DC-861A-11E6-AE22-56B6B6499611" :uuid) #uuid "cf20e1dc-861a-11e6-ae22-56b6b6499611"))
    ;;
    (is (nil? (coerce "cf20e1dc861a11e6ae2256b6b6499611" :uuid)))
    (is (nil? (coerce "cf20e1dc--56b6b6499611"           :uuid)))

    (is (= (coerce #uuid "cf20e1dc-861a-11e6-ae22-56b6b6499611" :string) "cf20e1dc-861a-11e6-ae22-56b6b6499611"))
    (is (= (coerce #uuid "CF20E1DC-861A-11E6-AE22-56B6B6499611" :string) "cf20e1dc-861a-11e6-ae22-56b6b6499611"))

    ;;
    #?(:clj (do
              (is (thrown? IllegalArgumentException (coerce "cf20e1dc861a11e6ae2256b6b6499611" :uuid :throw? true)))
              (is (thrown? IllegalArgumentException (coerce "cf20e1dc--56b6b6499611"           :uuid :throw? true)))))))

(deftest date-coercions
  (testing "Strings to Dates"
    (is (= (coerce "2020-10-10" :date) #inst "2020-10-10")))

  (testing "Dates to Strings"
    (is (= (coerce #inst "2020-10-10" :string) "2020-10-10"))
    (is (= (coerce #inst "2020-10-10" :string :pattern :yyyy-mm-dd)  "2020-10-10"))
    (is (= (coerce #inst "2020-10-10" :string :pattern "yyyy-MM-dd") "2020-10-10"))
    ;;
    (is (= (coerce "10/10/2020"       :date   :pattern :dd|mm|yyyy) #inst "2020-10-10"))
    (is (= (coerce #inst "2020-10-10" :string :pattern :dd|mm|yyyy) "10/10/2020"))))
