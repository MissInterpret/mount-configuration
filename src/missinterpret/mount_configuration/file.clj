(ns missinterpret.mount-configuration.file
  "Provides data from an edn file. "
  (:require [clojure.pprint :refer [pprint]]
            [clojure.edn :as edn]
            [missinterpret.anomalies.anomaly :as anom]
            [mount.core :refer [defstate] :as mount]
            [missinterpret.mount-configuration.env :refer [env-config]]
            [missinterpret.mount-configuration.resource :refer [resource-config]]))

;; Mount ----------------------------------------------------------
;;

(def edit-atom (atom {:changed false
                      :edit {}}))


(defn read-configuration [path]
  (-> path
      slurp
      edn/read-string))


(defn start [{:mount-configuration.file/keys [path throw-if-missing dont-bootstrap dont-save-on-stop]}]
  (let [anomaly {:from     ::start
                 :category :anomaly.category/fault
                 :message  {:readable (str path " missing or failed to load")
                            :data     {:path                path
                                       :throw-if-missing   throw-if-missing}}}
        file-path (cond
                   (some? path)                                      path
                   (get env-config :mount-configuration.file/path)   (get env-config :mount-configuration.file/path)
                   (and (not omit-resources)
                        (get resource-config :mount-configuration.file/path))
                   (get resource-config :mount-configuration.file/path))]
    (cond
      (and (nil? file-path) throw-if-missing) (anom/throw+ :path-missing anomaly)
      (nil? file-path) {}
      :else
      (try
        (let [data (read-configuration file-path)]
          (swap! edit-atom assoc :path path)
          (swap! edit-atom assoc :dont-save dont-save-on-stop)
          (assoc data :mount-configuration.file/path path))

        (catch java.lang.Exception _ (anom/throw+ :parse-exception anomaly))))))


(defn stop []
  (when (and (:changed @edit-atom)
             (not (:dont-save-on-stop @edit-atom)))
    (let [path (:path @edit-atom)
          data (:edit @edit-atom)]
      (->> data
           pprint
           with-out-str
           (spit path)))))

;; NOTE: The configuration data loaded at runtime.
(defstate file-config
          :start (start (mount/args))
          :stop (stop))


;; Editing Fns ----------------------------------------------------------
;;

(defn config
  "The view of the configuration data that reflects any edits, otherwise
   it is the same as the runtime loaded data."
  []
  (if (:changed @edit-atom)
    (:edit @edit-atom)
    file-config))


(defn fassoc
  "Editing eqivalent to assoc"
  [k value]
  (swap! edit-atom assoc-in [:edit k] value)
  (swap! edit-atom assoc :changed true)
  @edit-atom)


(defn fassoc-in
  "Editing equivalent to assoc-in"
  [keys value]
  (swap! edit-atom assoc-in (into [:edit] keys) value)
  (swap! edit-atom assoc :changed true)
  @edit-atom)


(defn fdissoc
  "Editing equivalent to dissoc"
  [k]
  (let [data (-> (:edit @edit-atom)
                 (dissoc k))]
    (swap! edit-atom assoc :edit data)
    (swap! edit-atom assoc :changed true))
  @edit-atom)
