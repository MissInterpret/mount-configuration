(ns missinterpret.mount-configuration.resource
  "Provides data by loading resource-config.edn from the resources of the runtime context"
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [mount.core :refer [defstate]]
            [missinterpret.anomalies.anomaly :as anom]
            [mount.core :refer [defstate] :as mount]
            [missinterpret.mount-configuration.env :refer [env-config]]))

;; Mount ----------------------------------------------------------
;;

(defn start [{:mount-configuration.resource/keys [path throw-if-missing] :as args}]
  (let [anomaly {:from     ::start
                 :category :anomaly.category/fault
                 :message  {:readable (str path " missing or failed to load")
                            :data     {:path path
                                       :throw-if-missing throw-if-missing}}}
        rsrc (when path
               (io/resource path))]
    (cond
      (and (nil? rsrc) throw-if-missing) (anom/throw+ :resource-missing anomaly)
      (nil? rsrc)                        {}

      :else
      (try
        (-> (slurp rsrc) edn/read-string)
        (catch java.lang.Exception _ (anom/throw+ :parse-exception anomaly))))))


(defstate resource-config
          :start (start (mount/args)))

