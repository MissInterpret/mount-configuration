(ns missinterpret.mount-configuration.file
  "Provides data from an edn file. "
  (:require [missinterpret.anomalies.anomaly :as anom]
            [missinterpret.edn-io.data-readers :as data-readers] ;; Required for tag literals during write
            [missinterpret.edn-io.edn :as edn]
            [missinterpret.mount-configuration.env :refer [env-config]]
            [clojure.java.io :as io]
            [mount.core :refer [defstate] :as mount]
            [clojure.pprint :refer [pprint]]
            [missinterpret.mount-configuration.resource :refer [resource-config]]))

;; State -----------------------------------------------------------

(def default {:changed false
              :edit {}
              :dont-save-on-stop false})

(def edit-atom (atom default))


;; FS -------------------------------------------------------------

(defn commit-changes []
  (when (:changed @edit-atom)
    (let [path (:path @edit-atom)
          data (:edit @edit-atom)]
      (->> data
           pprint
           with-out-str
           (spit path)))))


;; Mount ----------------------------------------------------------

(defn start [{:mount-configuration.file/keys [path throw-if-missing dont-bootstrap dont-save-on-stop] :as args}]
  (let [anomaly {:from     ::start
                 :category :anomaly.category/fault
                 :message  {:readable (str path " missing or failed to load")
                            :data     {:path               path
                                       :throw-if-missing   throw-if-missing}}}
        file-path (if dont-bootstrap
                    path
                    (cond
                      (some? path) path
                      (get env-config :mount-configuration.file/path)
                      (get env-config :mount-configuration.file/path)

                      (get resource-config :mount-configuration.file/path)
                      (get resource-config :mount-configuration.file/path)))
        missing (or (nil? file-path) (-> (io/file file-path) (.exists) not))]
    (cond
      (and missing (true? throw-if-missing))
      (anom/throw+ anomaly)

      (nil? file-path)
      {}

      (and (some? file-path) missing)
      (do
        (swap! edit-atom assoc :path file-path)
        {})

      :else
      (let [data (-> (slurp file-path) (edn/read :throw-on-error true))]
        (swap! edit-atom assoc :path file-path)
        (swap! edit-atom assoc :dont-save-on-stop dont-save-on-stop)
        data))))


(defn stop []
  (when (not (true? (:dont-save-on-stop @edit-atom)))
    (commit-changes)))


;; NOTE: The configuration data loaded at runtime.
(defstate file-config
          :start (start (mount/args))
          :stop (stop))


;; Editing Fns ----------------------------------------------------------

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
