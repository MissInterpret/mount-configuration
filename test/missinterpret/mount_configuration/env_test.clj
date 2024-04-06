(ns missinterpret.mount-configuration.env-test
  (:require [clojure.test :refer :all]
            [mount.core :as mount]
            [aux.fixtures :as aux.fix]
            [missinterpret.mount-configuration.env :refer [env-config]]))

(deftest start
  (testing "Success; load of string value/not edn value (PATH)"
    (mount/start-with-args {:mount-configuration.env/vars #{:PATH}})
    (is (:PATH env-config))
    (mount/stop))

  (testing "Failed; missing value"
    (is (thrown? java.lang.Exception (mount/start-with-args {:mount-configuration.env/vars #{:MISSING}})))
    (mount/stop))

  (testing "Flag; skip-missing"
    (mount/start-with-args #:mount-configuration.env{:vars #{:MISSING}
                                                     :skip-missing true})
    (is (nil? (:MISSING env-config)))
    (mount/stop))

  (testing "Flag; skip-missing"
    (is (thrown? java.lang.Exception (mount/start-with-args #:mount-configuration.env{:vars #{:PATH} :throw-parse-failed true})))
    (mount/stop)))
