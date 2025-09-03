(ns missinterpret.mount-configuration.env
  "Provides data from environment variables by iterating over the argument map:
   {:archivist.config.env/vars #{:ENV-VAR}} populating a map of the key with
   its environment variable value.

   Note: values from env variables are parsed as edn"
  (:require [missinterpret.anomalies.anomaly :as anom]
            [missinterpret.edn-io.edn :as edn]
            [mount.core :refer [defstate] :as mount]
            [clojure.pprint :refer [pprint]])
  (:import (clojure.lang Symbol)))

;; Mount ----------------------------------------------------------
;;
(defn start [{:mount-configuration.env/keys [vars throw-if-missing throw-parse-failed] :as args}]
  (reduce
    (fn [coll k]
      (let [value (-> (name k) System/getenv)
            anomaly {:from     ::start
                     :category :anomaly.category/fault
                     :message  {:readable (str k " missing or failed to load")
                                :data     {:var k
                                           :value value
                                           :throw-if-missing throw-if-missing
                                           :throw-parse-failed throw-parse-failed}}}]

        (try
          (cond
            (and (nil? value) (true? throw-if-missing)) (anom/throw+ anomaly)
            (nil? value)                                coll

            :else
            (try
              (let [parsed (edn/read value :throw-on-error true)
                    v (if (-> parsed type (= Symbol))
                        (str parsed)
                        parsed)]
                (assoc coll k v))

              (catch java.lang.Exception _ (if throw-parse-failed
                                            (anom/throw+ anomaly)
                                            (assoc coll k value))))))))
    {}
    vars))

(defstate env-config
          :start (start (mount/args)))
