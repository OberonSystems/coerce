(ns coerce.dates
  (:require [clojure.string :as s]
            [coerce.constants :refer [+date-patterns+]])
  (:import  [java.util TimeZone Date Locale]
            [java.text SimpleDateFormat DateFormatSymbols]))

;;; --------------------------------------------------------------------------------

(defn- pattern-dispatcher
  [v pattern]
  (cond
    (contains? +date-patterns+ pattern) ::pattern
    (string? pattern)                   ::string
    :else pattern))

(defmulti date->string pattern-dispatcher)
(defmulti string->date pattern-dispatcher)

;;; --------------------------------------------------------------------------------
;; By using Locale/US, we get Jan, Feb, etc, rather than
;; Jan. Feb. (with a dot) that we'd get with Locale/AUS (and maybe
;; others).
;;
;; We have to set it to something so I think this is 'sane' default.

(defonce +date-symbols+ (DateFormatSymbols. Locale/US))
(defonce +lenient?+     false)
(defonce +timezone+     "UTC")

(defn- make-locale
  [loc]
  (if (string? loc)
    (let [[lang country] (s/split loc #"_")]
      (if country
        (Locale. lang country)
        (Locale. loc)))
    loc))

(defn set-format-options!
  ([]       (set-format-options! nil    nil nil))
  ([locale] (set-format-options! locale nil nil))
  ;;
  ([locale lenient? timezone]
   (alter-var-root (var +date-symbols+) (constantly (DateFormatSymbols. (or (make-locale locale)
                                                                            Locale/US))))
   (alter-var-root (var +lenient?+)     (constantly (boolean lenient?)))
   (alter-var-root (var +timezone+)     (constantly (or timezone "UTC")))
   nil))

(defn- make-date-format
  [pattern]
  (doto (SimpleDateFormat. pattern +date-symbols+)
    (.setTimeZone (TimeZone/getTimeZone +timezone+))
    (.setLenient +lenient?+)))

(defmethod date->string ::pattern
  [v pattern]
  (-> (get +date-patterns+ pattern)
      make-date-format
      (.format v)))

(defmethod string->date ::pattern
  [v pattern]
  (-> (get +date-patterns+ pattern)
      make-date-format
      (.parse v)))

;;; Same as above but assuming that pattern is a valid string pattern.

(defmethod date->string ::string
  [v pattern]
  (-> pattern
      make-date-format
      (.format v)))

(defmethod string->date ::string
  [v pattern]
  (-> pattern
      make-date-format
      (.parse v)))

;;; --------------------------------------------------------------------------------
;;  For integers

(defn integer->date
  [v]
  (Date. v))

(defn date->integer
  [v]
  (.getTime v))
