(ns missinterpret.mount-configuration.env
  "Provides data from environment variables by iterating over the argument map:
   {:archivist.config.env/vars #{:ENV-VAR}} populating a map of the key with
   its environment variable value.

   Note: values from env variables are parsed as edn"
  (:require [clojure.edn :as edn]
            [clojure.pprint :refer [pprint]]
            [mount.core :refer [defstate] :as mount]
            [missinterpret.anomalies.anomaly :as anom])
  (:import (clojure.lang Symbol)))

;; Mount ----------------------------------------------------------
;;

(defn start [{:mount-configuration.env/keys [vars skip-missing throw-parse-failed] :as args}]
  (reduce
    (fn [coll k]
      (let [value (-> (name k) System/getenv)
            anomaly {:from     ::start
                     :category :anomaly.category/fault
                     :message  {:readable (str k " missing or failed to load")
                                :data     {:var k
                                           :value value
                                           :skip-missing skip-missing
                                           :throw-parse-failed throw-parse-failed}}}]
        (try
          (cond
            (and (nil? value) skip-missing)       coll
            (and (nil? value) (not skip-missing)) (anom/throw+ :not-found anomaly)

            :else
            (try
              (let [parsed (edn/read-string value)
                    v (if (-> parsed type (= Symbol))
                        (str parsed)
                        parsed)]
                (assoc coll k v))

              (catch java.lang.Exception _ (if throw-parse-failed
                                            (anom/throw+ :parse-exception anomaly)
                                            (assoc coll k value))))))))
    {}
    vars))

(defstate env-config
          :start (start (mount/args)))



