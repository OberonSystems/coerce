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
                                      #_Coerce->BigDecimal]]
            #_[coerce.constants :refer [+date-patterns+]]
            [coerce.utils :refer [clean-string join-cleanly extract-leading-integer extract-leading-decimal uuid-string? keywordify]]

            ;;
            #_[goog.i18n.DateTimeFormat :as dtf]
            #_[goog.i18n.DateTimeParse  :as dtp]))

;;; --------------------------------------------------------------------------------

(defn url-encode
  [s]
  (js/encodeURIComponent s))

;;; --------------------------------------------------------------------------------

(defn- read-number-string
  [s strict? throw? constructor cleaner]
  (when-let [v (some-> s clean-string constructor)]
    (if (and (number? v) (not (js/isNaN v)))
      v
      (if strict?
        (when throw? (throw (js/Exception. (str s " is not a number."))))
        (some-> s cleaner constructor)))))

;;; --------------------------------------------------------------------------------

(extend-type string
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
    (if-let [v (clean-string v)]
      (-> (re-find re-false? v) not)
      false))
  ;;
  ;; Coerce->Date
  ;; (coerce->date
  ;;   [v {:keys [pattern timezone]
  ;;       :or   {pattern  :yyyy-mm-dd
  ;;              timezone +default-timezone+}
  ;;       :as   options}]
  ;;   (some-> v
  ;;           clean-string
  ;;           (string->date pattern timezone)))
  ;;

  Coerce->Integer
  (coerce->integer [v {:keys [strict? throw? constructor]
                       :or   {constructor js/parseInt}}]
    (read-number-string v strict? throw?
                        constructor
                        extract-leading-integer))
  ;;
  Coerce->BigInteger
  (coerce->big-integer [v {:keys [strict? throw? constructor]
                           :or   {constructor js/parseInt}}]
    (read-number-string v strict? throw?
                        constructor
                        extract-leading-integer))
  ;;
  Coerce->Decimal
  (coerce->decimal [v {:keys [strict? throw? constructor]
                       :or   {constructor js/parseFloat}}]
    (read-number-string v strict? throw?
                        constructor
                        extract-leading-decimal))
  ;;
  Coerce->Keyword
  (coerce->keyword [v {:keys [strict? ns] :as options}]
    (if strict?
      (keyword    ns v)
      (keywordify ns v)))
  ;;
  Coerce->UUID
  (coerce->uuid
    [v {:keys [throw?] :as options}]
    (when-let [v (clean-string v)]
      (if (uuid-string? v)
        (uuid v)
        (when throw? (throw (js/Exception. (str v " is not a UUID."))))))))

;;; --------------------------------------------------------------------------------

(extend-type boolean
  Coerce->String     (coerce->string      [v {:keys [t f] :or {t "true" f "false"}}] (if (true? v) t f))
  Coerce->Boolean    (coerce->boolean     [v _] v)
  #_Coerce->Keyword
  #_Coerce->UUID
  Coerce->Integer    (coerce->integer     [v _] (-> v (if 1 0)))
  Coerce->BigInteger (coerce->big-integer [v _] (-> v (if 1 0)))
  Coerce->Decimal    (coerce->decimal     [v _] (-> v (if 1 0)))
  #_Coerce->BigDecimal)

(extend-type cljs.core/Keyword
  Coerce->String     (coerce->string      [v {:keys [sep]}] (join-cleanly [(namespace v)
                                                                           (name      v)]
                                                                          (or sep "/")))
  Coerce->Boolean    (coerce->boolean     [_ _] true)
  Coerce->Keyword    (coerce->keyword     [v _] v)
  #_Coerce->UUID
  #_Coerce->Integer
  #_Coerce->BigInteger
  #_Coerce->Decimal
  #_Coerce->BigDecimal)

(extend-type cljs.core/Symbol
  Coerce->String     (coerce->string      [v {:keys [sep]}] (join-cleanly [(namespace v)
                                                                           (name      v)]
                                                                          (or sep "/")))
  Coerce->Boolean    (coerce->boolean     [_ _] true)
  Coerce->Keyword    (coerce->keyword     [v _] (keyword v))
  #_Coerce->UUID
  #_Coerce->Integer
  #_Coerce->BigInteger
  #_Coerce->Decimal
  #_Coerce->BigDecimal)

(extend-type cljs.core/UUID
  Coerce->String     (coerce->string      [v _] (str v))
  Coerce->Boolean    (coerce->boolean     [_ _] true)
  #_Coerce->Keyword
  Coerce->UUID       (coerce->uuid        [v _] v)
  #_Coerce->Integer
  #_Coerce->BigInteger
  #_Coerce->Decimal
  #_Coerce->BigDecimal)

(extend-type number
  Coerce->String     (coerce->string      [v _] (str v))
  Coerce->Boolean    (coerce->boolean     [v _] (-> v zero? not))
  #_Coerce->Keyword
  #_Coerce->UUID
  Coerce->Integer    (coerce->integer     [v _] (-> v js/Math.round))
  Coerce->BigInteger (coerce->big-integer [v _] (-> v js/BigInt))
  Coerce->Decimal    (coerce->decimal     [v _] (-> v))
  #_Coerce->BigDecimal)
