(ns coerce.constants)

(def +default-date-pattern+ :yyyy-mm-dd)

(def +date-patterns+
  {:yyyy-mm-dd             #?(:clj "yyyy-MM-dd"               :cljs "")
   :yyyy-mm-dd-HHmm        #?(:clj "yyyy-MM-dd HH:mm"         :cljs "")

   :yyyymmdd               #?(:clj "yyyyMMdd"                 :cljs "")

   :dd-mmm                 #?(:clj "dd MMM"                   :cljs "")
   :dd-mmm-yy              #?(:clj "d-MMMM-yy"                :cljs "")
   :dd-mmm-yyyy            #?(:clj "dd MMM yyyy"              :cljs "")

   :dd-mmmm                #?(:clj "dd MMMM"                  :cljs "")
   :dd-mmmm-yyyy           #?(:clj "dd MMMM yyyy"             :cljs "")

   :dd-mm-yyyy             #?(:clj "dd-MM-yyyy"               :cljs "")
   :dd-mm-yyyy-hhmma       #?(:clj "dd-MM-yyyy hh:mma"        :cljs "")

   :dd|mm|yy               #?(:clj "dd/MM/yy"                 :cljs "")
   :dd|mm|yyyy             #?(:clj "dd/MM/yyyy"               :cljs "")
   :dd|mm|yyyy-hhmma       #?(:clj "dd/MM/yyyy hh:mma"        :cljs "")

   :ddmmyyyy               #?(:clj  "ddMMyyyy"                :cljs "")

   :day-dd                 #?(:clj "E dd"                     :cljs "")
   :day-dd-mm              #?(:clj "E dd/MM"                  :cljs "")
   :day-dd|mm|yyyy         #?(:clj "EEEE, dd/MM/yyyy"         :cljs "")

   :mm-dd-yyyy             #?(:clj "MM-dd-yyyy"               :cljs "")
   :mm|dd|yyyy             #?(:clj "MM/dd/yyyy"               :cljs "")

   :day-mm|dd|yyyy         #?(:clj "EEEE, MM/dd/yyyy"         :cljs "")

   :d-month-yyyy           #?(:clj "d MMMM yyyy"              :cljs "")
   :day-dd-month-yyyy      #?(:clj "EEEE, dd MMMM yyyy"       :cljs "")

   :rfc-3999               #?(:clj "yyyy-MM-dd'T'HH:mm:ssXXX" :cljs "")
   :iso-8601               #?(:clj "yyyy-MM-dd'T'kk:mm:ssZ"   :cljs "")
   :yyyy-mm-dd-hh-mm-ss    #?(:clj "yyyy-MM-dd kk:mm:ss"      :cljs "")
   :yyyy-mm-dd.hh-mm-ss-z  #?(:clj "yyyy-MM-dd HH:mm:ss z"    :cljs "")
   :yyyy-mm-ddThh-mm-ss    #?(:clj "yyyy-MM-dd'T'HH-mm-ss"    :cljs "")
   :yyyymmddhhmmss         #?(:clj "yyyyMMddkkmmss"           :cljs "")

   :yyyy|mm|dd-directories #?(:clj  "yyyy/MM/dd"              :cljs "")

   :week-dd|mm|yyyy-day    #?(:clj "ww - dd/MM/yyyy - EEEE"   :cljs "")

   :week                   #?(:clj "ww"                       :cljs "")
   :day                    #?(:clj "EEEE"                     :cljs "")

   :24hmm                  #?(:clj "HHmm"                     :cljs "")
   :24h-mm-ss              #?(:clj "HH:mm:ss"                 :cljs "")})

(defn add-date-patterns!
  [patterns]
  (alter-var-root (var +date-patterns+)
                  (constantly (merge +date-patterns+ patterns))))
