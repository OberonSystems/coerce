(ns coerce.protocols)

(defprotocol Coerce->String
  (coerce->string [v o]))

;;;

(defprotocol Coerce->Boolean
  (coerce->boolean [v o]))

(defprotocol Coerce->Keyword
  (coerce->keyword [v o]))

(defprotocol Coerce->UUID
  (coerce->uuid [v o]))

;;;

(defprotocol Coerce->Integer
  (coerce->integer [v o]))

(defprotocol Coerce->BigInteger
   (coerce->big-integer [v o]))

(defprotocol Coerce->Decimal
  (coerce->decimal [v o]))

#?(:clj (defprotocol Coerce->BigDecimal
          (coerce->big-decimal [v o])))

;;; Java Util Date

#?(:clj (defprotocol Coerce->Date
          "Coercion to java.util.Date"
          (coerce->date [v o])))

;;; Java Time

#?(:clj (defprotocol Coerce->LocalDate
          "Coercion to java.time.LocalDate"
          (coerce->local-date [v o])))

#?(:clj (defprotocol Coerce->LocalTime
          "Coercion to java.time.LocalTime"
          (coerce->local-time [v o])))

#?(:clj (defprotocol Coerce->LocalDateTime
          "Coercion to java.time.LocalDateTime"
          (coerce->local-date-time [v o])))

#?(:clj (defprotocol Coerce->ZonedDateTime
          "Coercion to java.time.ZonedDateTime"
          (coerce->zoned-date-time [v o])))

#?(:clj (defprotocol Coerce->Instant
          "Coercion to java.time.Instant"
          (coerce->instant [v o])))
