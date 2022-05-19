(ns coerce.utils
  (:require [clojure.string :as s]))

(defn remove-junk-from-integer
  [s]
  (s/replace s #"[^0-9]" ""))

(defn collapse-spaces
  [s]
  (s/replace s #"\s+" " "))

(defn clean-string
  [v & [{:keys [clean? collapse? upper? lower?]
         :or {clean?    true
              collapse? true}
         :as options}]]
  (when-not (s/blank? v)
    (cond-> v
      clean?    s/trim
      collapse? collapse-spaces
      upper?    s/upper-case
      lower?    s/lower-case)))

(defn join-cleanly
  ([coll]
   (join-cleanly coll ""))
  ([coll sep]
   (->> coll
        (map clean-string)
        (remove nil?)
        (s/join sep))))

(defn keywordify
  ([s] (keywordify nil s))
  ([ns s]
   (when-let [s (some-> s
                        (s/replace #"\W" " ")
                        s/lower-case
                        clean-string
                        (s/replace #"_| " "-"))]
     (keyword ns s))))

(defn guess-sign
  [s]
  ;; We look for the first - sign from the beginning followed by a
  ;; number, if we find it we are negative otherwise we aren't.
  ;;
  ;; We ignore whitespace between the - and the digit.
  (if (re-find #"^[^0-9]*-[\s]*[0-9]" s)
    "-" ""))

(defn extract-leading-integer
  [s]
  (some->> (s/split s #"\.")
           first
           remove-junk-from-integer
           clean-string
           (str (guess-sign s))))

(defn extract-leading-decimal
  [s]
  (let [[i1 i2] (some->> (s/split s #"\.")
                         (take 2)
                         (map remove-junk-from-integer)
                         (map clean-string))]
    (when i1 (str (guess-sign s) i1 (when i2 ".") i2))))

(defn uuid-string?
  [s]
  (re-find #"(?i)^[\da-f]{8}-[\da-f]{4}-[\da-f]{4}-[\da-f]{4}-[\da-f]{12}$" s))
