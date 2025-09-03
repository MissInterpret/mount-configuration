(ns missinterpret.mount-configuration.env-test
  (:require [clojure.test :refer :all]
            [clojure.pprint :refer [pprint]]
            [mount.core :as mount]
            [aux.fixtures :as aux.fix]
            [missinterpret.mount-configuration.env :refer [env-config]]))

(deftest start
  (testing "Success; load of string value/not edn value (PATH)"
    (mount/start-with-args {:mount-configuration.env/vars #{:PATH}})
    (let [path (:PATH env-config)]
      (is (and (some? path) (string? path) )))
    (mount/stop))

  (testing "Failed; missing value"
    (is (thrown? java.lang.Exception (mount/start-with-args #:mount-configuration.env{:vars #{:MISSING} :throw-if-missing true})))
    (mount/stop))

  (testing "Flag; skip if missing"
    (mount/start-with-args #:mount-configuration.env{:vars #{:MISSING}})
    (is (nil? (:MISSING env-config)))
    (mount/stop))

  (testing "Flag; throw if parse failed"
    (is (thrown? java.lang.Exception (mount/start-with-args #:mount-configuration.env{:vars #{:PATH} :throw-parse-failed true})))
    (mount/stop)))
