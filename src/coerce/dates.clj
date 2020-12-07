(ns coerce.dates
  (:require [clojure.string :as s]
            [coerce.constants :refer [+date-patterns+]])
  (:import  [java.util TimeZone Date]
            [java.text SimpleDateFormat]))

;;; --------------------------------------------------------------------------------

(defn- pattern-dispatcher
  [v pattern]
  (cond
    (contains? +date-patterns+ pattern) ::pattern
    (string? pattern)                   ::string
    :else pattern))

(defmulti date->string pattern-dispatcher)
(defmulti string->date pattern-dispatcher)

;;;

(defn- make-date-format
  [pattern]
  (doto (SimpleDateFormat. pattern)
    (.setTimeZone (TimeZone/getTimeZone "UTC"))
    (.setLenient false)))

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
