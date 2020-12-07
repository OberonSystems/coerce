(ns coerce.core
  (:require [clojure.string :as s]
            ;;
            [coerce.protocols :refer [coerce->string
                                      coerce->boolean
                                      coerce->keyword
                                      coerce->uuid
                                      coerce->integer
                                      coerce->big-integer
                                      coerce->decimal
                                      #?(:clj coerce->big-decimal)
                                      #?(:clj coerce->date)]]
            [coerce.impl :as impl]))

(defmulti coerce (fn [value to & {:as options}]
                   (if (or (nil? value)
                           (and (string?  value) (s/blank? value))
                           (and (seqable? value) (empty?   value))
                           #?(:cljs (js/Number.isNaN value))
                           #?(:cljs (undefined?      value)))
                     ::nil
                     to)))

(defmethod coerce ::nil
  [& _]
  nil)

;;;

(defmethod coerce :string
  [value _ & {:as options}]
  (coerce->string value options))

;;;

(defmethod coerce :boolean
  [value _ & {:as options}]
  (coerce->boolean value options))

(defmethod coerce :keyword
  [value _ & {:as options}]
  (coerce->keyword value options))

(defmethod coerce :uuid
  [value _ & {:as options}]
  (coerce->uuid value options))

;;;

(defmethod coerce :integer
  [value _ & {:as options}]
  (coerce->integer value options))

(defmethod coerce :big-integer
  [value _ & {:as options}]
  (coerce->big-integer value options))

(defmethod coerce :decimal
  [value _ & {:as options}]
  (coerce->decimal value options))

#?(:clj (defmethod coerce :big-decimal
          [value _ & {:as options}]
          (coerce->big-decimal value options)))

;;;

#?(:clj (defmethod coerce :date
          [value _ & {:as options}]
          (coerce->date value options)))
