(ns missinterpret.mount-configuration.core
  "Loads all available configuration data merging them into a single
   map.

   Notes:
    - unified view of data is the map produced after merging
      env, resource, file data in that order.
    - Loaded in this order with each type bootstrapping in merge order
    - All types support attempt edn parsing

   Arguments
   ENV
   {:mount-configuration.env/X ...}
     vars: #{:ENV-VAR} of environment variables
     skip-missing: Skip any selected variables that don't have values
     throw-parse-failed: Throw an exception if the value can not be parsed to edn

   RESOURCE
   {:mount-configuration.resource/X ...}
     path: The path of the resource
     throw-if-missing: Throw an exception if the resource can not be found

   FILE
   {:mount-configuration.file/X ...}
     uri: The file uri
     throw-if-missing: Throw an exception if the file is missing
     omit-resources: Do not use resources to determine the file uri"
  (:require [mount.core :as mount]
            [mount.core :refer [defstate]]
            [missinterpret.mount-configuration.env :refer [env-config]]
            [missinterpret.mount-configuration.resource :refer [resource-config]]
            [missinterpret.mount-configuration.file :refer [file-config] :as config.file]))

;; Copyright 2024 Creative Commons

;; Mount ----------------------------------------------------------
;;

(defn start [_]
  (merge env-config resource-config file-config))

(defstate config
          :start (start (mount/args)))


;; Fn's ----------------------------------------------------------
;;

(defn source-config
  [source-type]
  (case source-type
    :env      env-config
    :resource resource-config
    :file     file-config
    nil))

(defn get-from
  [source-type key]
  (-> (source-config source-type)
      (get key)))

(defn get-in-from
  [source-type keys]
  (-> (source-config source-type)
      (get keys)))

(defn config
  "A reliable view of the configuration data when the optional
   config.file editing is used."
  []
  (if (:changed @config.file/edit-atom)
    (merge config (:edit @config.file/edit-atom))
    config))

io.github.missinterpret/anomalies {:mvn/version "0.1.0"}
mount/mount                       {:mvn/version "0.1.18"}