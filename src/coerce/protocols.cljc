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

;;;

#?(:clj (defprotocol Coerce->Date
          "Coercion to java.util.Date"
          (coerce->date [v o])))

#_
(defprotocol Coerce->Instant
  "Coercion to java.time.Instant"
  (coerce->instant [v o]))

#_(
   (defprotocol Coerce->Date
     (coerce->date [v o]))

   (defprotocol Coerce->DateTime
     (coerce->datetime [v o]))

   (defprotocol Coerce->Timestamp
     (coerce->timestamp [v o])))
