(ns missinterpret.mount-configuration.edn
  "Loading function to parse edn that support time literals"
  (:require [clojure.edn :as edn]
            [time-literals.read-write :as literals])
  (:import (java.io InputStreamReader PushbackReader)))

(defn read-string
  "Parses the EDN data from the input stream. Supports time reader"
  [input-stream]
  (with-open [r (InputStreamReader. input-stream)]
    (edn/read
      {:readers literals/tags}
      (PushbackReader. r))))
