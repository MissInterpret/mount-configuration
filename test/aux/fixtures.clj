(ns aux.fixtures
  (:require [clojure.java.io :as io]))

(defn cwd-path []
  (str (.getCanonicalPath (io/file ".")) "/"))

(def test-dir
  (-> (cwd-path)
      (io/file "test/resources")))

(def config.default "configuration.edn")
(def config.simple "simple-config.edn")
(def config.store "store-config.edn")

;; Fns ------------------------------------------------------
;;

(defn cp-config [config out]
  (fn [f]
    (let [in (io/file test-dir config)
          out (io/file out)]
      (io/copy in out)
      (f)
      (.delete out))))
