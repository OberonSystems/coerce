(ns coerce.impl
  (:require [clojure.string :as s]
            ;;
            [coerce.protocols :refer [Coerce->String
                                      Coerce->Boolean
                                      Coerce->Keyword
                                      Coerce->UUID
                                      ;;
                                      Coerce->Integer
                                      Coerce->BigInteger
                                      Coerce->Decimal
                                      Coerce->BigDecimal
                                      ;;
                                      Coerce->Date]]
            [coerce.constants :refer [+default-date-pattern+]]
            [coerce.utils :refer [clean-string join-cleanly extract-leading-integer extract-leading-decimal keywordify]]
            [coerce.dates :as dates])
  (:import  [java.util UUID]
            ;;
            [java.math BigInteger BigDecimal]
            [java.net URLEncoder URLDecoder]))

;;; --------------------------------------------------------------------------------

(defn url-encode
  [s]
  (URLEncoder/encode s))

;;; --------------------------------------------------------------------------------

(defn- read-number-string
  [s strict? throw? constructor cleaner]
  (when-let [s (clean-string s)]
    (try
      (constructor s)
      (catch Exception e
        (if strict?
          (when throw? (throw e))
          (some-> s cleaner constructor))))))

;;; --------------------------------------------------------------------------------

(extend-type java.lang.String
  ;;
  Coerce->String
  (coerce->string [v {:keys [clean?] :or {clean? true}}]
    (if clean?
      (clean-string v)
      v))
  ;;
  Coerce->Boolean
  (coerce->boolean [v {:keys [re-false?]
                       :or   {re-false? #"(?i)^(f|false|n|no)$"}}]
    ;; By default tried to get semantics close to NIL, in other words
    ;; blank strings, 'f' or 'false', 'n' or 'no' are false,
    ;; everything else is true.
    (if-let [v (clean-string v)]
      (-> (re-find re-false? v) not)
      false))
  ;;
  Coerce->Keyword
  (coerce->keyword [v {:keys [strict? ns] :as options}]
    (if strict?
      (keyword    ns v)
      (keywordify ns v)))

  Coerce->UUID
  (coerce->uuid
    [v {:keys [throw?] :as options}]
    (when-let [v (clean-string v)]
      (try
        (UUID/fromString v)
        (catch Exception e
          (when throw? (throw e))))))

  Coerce->Integer
  (coerce->integer [v {:keys [strict? throw? constructor]
                       :or   {constructor #(Long/parseLong %)}}]
    ;; Can pass #(Integer/parseInt %) if you really need it, or cast
    ;; after the fact.
    (read-number-string v strict? throw?
                        constructor
                        extract-leading-integer))
  ;;
  Coerce->BigInteger
  (coerce->big-integer [v {:keys [strict? throw?]}]
    (read-number-string v strict? throw?
                        #(BigInteger. %)
                        extract-leading-integer))
  ;;
  Coerce->Decimal
  (coerce->decimal [v {:keys [strict? throw? constructor]
                       :or   {constructor #(Float/parseFloat %)}
                       :as options}]
    ;; Can pass #(Double/parseDouble %) if you really need it, or cast
    ;; after the fact.
    (read-number-string v strict? throw?
                        constructor
                        extract-leading-decimal))
  ;;
  Coerce->BigDecimal
  (coerce->big-decimal [v {:keys [strict? throw?]}]
    (read-number-string v strict? throw?
                        #(BigDecimal. %)
                        extract-leading-decimal))

  Coerce->Date
  (coerce->date [v {:keys [pattern]}]
    (some-> v clean-string (dates/string->date (or pattern +default-date-pattern+)))))

;;; --------------------------------------------------------------------------------

(extend-type java.lang.Boolean
  Coerce->String     (coerce->string      [v {:keys [t f] :or {t "true" f "false"}}] (if v t f))
  Coerce->Boolean    (coerce->boolean     [v _] v)
  #_Coerce->Keyword
  #_Coerce->UUID
  Coerce->Integer    (coerce->integer     [v _] (-> v (if 1 0) long))
  Coerce->BigInteger (coerce->big-integer [v _] (-> v (if 1 0) bigint))
  Coerce->Decimal    (coerce->decimal     [v _] (-> v (if 1 0) double))
  Coerce->BigDecimal (coerce->big-decimal [v _] (-> v (if 1 0) bigdec))
  #_Coerce->Date)

;;; --------------------------------------------------------------------------------

(extend-type clojure.lang.Keyword
  Coerce->String     (coerce->string      [v {:keys [sep]}] (join-cleanly [(namespace v)
                                                                           (name      v)]
                                                                          (or sep "/")))
  Coerce->Boolean    (coerce->boolean     [_ _] true)
  Coerce->Keyword    (coerce->keyword     [v _] v)
  #_Coerce->UUID
  #_Coerce->Integer
  #_Coerce->BigInteger
  #_Coerce->Decimal
  #_Coerce->BigDecimal
  #_Coerce->Date)

(extend-type clojure.lang.Symbol
  Coerce->String     (coerce->string      [v {:keys [sep]}] (join-cleanly [(namespace v)
                                                                           (name      v)]
                                                                          (or sep "/")))
  Coerce->Boolean    (coerce->boolean     [_ _] true)
  Coerce->Keyword    (coerce->keyword     [v _] (keyword v))
  #_Coerce->UUID
  #_Coerce->Integer
  #_Coerce->BigInteger
  #_Coerce->Decimal
  #_Coerce->BigDecimal
  #_Coerce->Date)

(extend-type java.util.UUID
  Coerce->String     (coerce->string      [v _] (str v))
  Coerce->Boolean    (coerce->boolean     [_ _] true)
  #_Coerce->Keyword
  #_Coerce->UUID
  #_Coerce->Integer
  #_Coerce->BigInteger
  #_Coerce->Decimal
  #_Coerce->BigDecimal
  #_Coerce->Date)

;;; --------------------------------------------------------------------------------
;;; Numbers

(extend-type java.lang.Integer
  Coerce->String     (coerce->string      [v _] (str v))
  Coerce->Boolean    (coerce->boolean     [v _] (-> v zero? not))
  #_Coerce->Keyword
  #_Coerce->UUID
  Coerce->Integer    (coerce->integer     [v _] (-> v long))
  Coerce->BigInteger (coerce->big-integer [v _] (-> v bigint))
  Coerce->Decimal    (coerce->decimal     [v _] (-> v double))
  Coerce->BigDecimal (coerce->big-decimal [v _] (-> v bigdec))
  ;;
  Coerce->Date       (coerce->date        [v _] (-> v dates/integer->date)))

(extend-type java.lang.Long
  Coerce->String     (coerce->string      [v _] (str v))
  Coerce->Boolean    (coerce->boolean     [v _] (-> v zero? not))
  #_Coerce->Keyword
  #_Coerce->UUID
  Coerce->Integer    (coerce->integer     [v _] (-> v long))
  Coerce->BigInteger (coerce->big-integer [v _] (-> v bigint))
  Coerce->Decimal    (coerce->decimal     [v _] (-> v double))
  Coerce->BigDecimal (coerce->big-decimal [v _] (-> v bigdec))
  ;;
  Coerce->Date       (coerce->date        [v _] (-> v dates/integer->date)))

(extend-type java.math.BigInteger
  Coerce->String     (coerce->string      [v _] (str v))
  Coerce->Boolean    (coerce->boolean     [v _] (-> v zero? not))
  #_Coerce->Keyword
  #_Coerce->UUID
  Coerce->Integer    (coerce->integer     [v _] (-> v long))
  Coerce->BigInteger (coerce->big-integer [v _] (-> v bigint))
  Coerce->Decimal    (coerce->decimal     [v _] (-> v double))
  Coerce->BigDecimal (coerce->big-decimal [v _] (-> v bigdec))
  ;;
  Coerce->Date       (coerce->date        [v _] (-> v dates/integer->date)))

;; --

(extend-type java.lang.Float
  Coerce->String     (coerce->string      [v _] (str v))
  Coerce->Boolean    (coerce->boolean     [v _] (-> v zero? not))
  #_Coerce->Keyword
  #_Coerce->UUID
  Coerce->Integer    (coerce->integer     [v _] (-> v long))
  Coerce->BigInteger (coerce->big-integer [v _] (-> v bigint))
  Coerce->Decimal    (coerce->decimal     [v _] (-> v double))
  Coerce->BigDecimal (coerce->big-decimal [v _] (-> v bigdec))
  #_Coerce->Date)

(extend-type java.lang.Double
  Coerce->String     (coerce->string      [v _] (str v))
  Coerce->Boolean    (coerce->boolean     [v _] (-> v zero? not))
  #_Coerce->Keyword
  #_Coerce->UUID
  Coerce->Integer    (coerce->integer     [v _] (-> v long))
  Coerce->BigInteger (coerce->big-integer [v _] (-> v bigint))
  Coerce->Decimal    (coerce->decimal     [v _] (-> v double))
  Coerce->BigDecimal (coerce->big-decimal [v _] (-> v bigdec))
  #_Coerce->Date)

(extend-type java.math.BigDecimal
  Coerce->String     (coerce->string      [v _] (str v))
  Coerce->Boolean    (coerce->boolean     [v _] (-> v zero? not))
  #_Coerce->Keyword
  #_Coerce->UUID
  Coerce->Integer    (coerce->integer     [v _] (-> v long))
  Coerce->BigInteger (coerce->big-integer [v _] (-> v bigint))
  Coerce->Decimal    (coerce->decimal     [v _] (-> v double))
  Coerce->BigDecimal (coerce->big-decimal [v _] (-> v bigdec))
  #_Coerce->Date)


;;; --------------------------------------------------------------------------------
;;; Date

(extend-type java.util.Date
  Coerce->String     (coerce->string      [v {:keys [pattern]}]
                       (dates/date->string v (or pattern +default-date-pattern+)))
  #_Coerce->Boolean
  #_Coerce->Keyword
  #_Coerce->UUID
  Coerce->Integer    (coerce->integer     [v _] (dates/date->integer v))
  Coerce->BigInteger (coerce->big-integer [v _] (dates/date->integer v))
  Coerce->Decimal    (coerce->decimal     [v _] (dates/date->integer v))
  Coerce->BigDecimal (coerce->big-decimal [v _] (dates/date->integer v))
  ;;
  Coerce->Date       (coerce->date        [v _] v))
