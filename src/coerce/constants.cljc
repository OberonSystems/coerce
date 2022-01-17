(ns coerce.constants)

(def +default-date-pattern+ :yyyy-mm-dd)

(def +date-patterns+
  {:yyyy-mm-dd            #?(:clj "yyyy-MM-dd"               :cljs "")
   :yyyy-mm-dd-HHmm       #?(:clj "yyyy-MM-dd HH:mm"         :cljs "")

   :yyccmmdd              #?(:clj "yyyyMMdd"                 :cljs "")

   :dd-mm-yyyy            #?(:clj "dd-MM-yyyy"               :cljs "")
   :dd-mm-yyyy-hhmma      #?(:clj "dd-MM-yyyy hh:mma"        :cljs "")

   :dd|mm|yy              #?(:clj "dd/MM/yy"                 :cljs "")
   :dd|mm|yyyy            #?(:clj "dd/MM/yyyy"               :cljs "")
   :dd|mm|yyyy-hhmma      #?(:clj "dd/MM/yyyy hh:mma"        :cljs "")

   :ddmmyyyy              #?(:clj  "ddMMyyyy"                :cljs "")

   :day-dd|mm|yyyy        #?(:clj "EEEE, dd/MM/yyyy"         :cljs "")

   :mm-dd-yyyy            #?(:clj "MM-dd-yyyy"               :cljs "")
   :mm|dd|yyyy            #?(:clj "MM/dd/yyyy"               :cljs "")

   :day-mm|dd|yyyy        #?(:clj "EEEE, MM/dd/yyyy"         :cljs "")

   :d-month-yyyy          #?(:clj "d MMMM yyyy"              :cljs "")
   :day-dd-month-yyyy     #?(:clj "EEEE, dd MMMM yyyy"       :cljs "")

   :rfc-3999              #?(:clj "yyyy-MM-dd'T'HH:mm:ssXXX" :cljs "")
   :iso-8601              #?(:clj "yyyy-MM-dd'T'kk:mm:ssZ"   :cljs "")
   :yyyy-mm-dd-hh-mm-ss   #?(:clj "yyyy-MM-dd kk:mm:ss"      :cljs "")
   :yyyy-mm-dd.hh-mm-ss-z #?(:clj "yyyy-MM-dd HH:mm:ss z"    :cljs "")
   :week                  #?(:clj "ww"                       :cljs "")
   :day                   #?(:clj "EEEE"                     :cljs "")
   :24h-mm-ss             #?(:clj "HH:mm:ss"                 :cljs "")})

(defn add-date-patterns!
  [patterns]
  (alter-var-root (var +date-patterns+)
                  (constantly (merge +date-patterns+ patterns))))
