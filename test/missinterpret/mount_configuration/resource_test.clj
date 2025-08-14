(ns missinterpret.mount-configuration.resource-test
  (:require [clojure.test :refer :all]
            [mount.core :as mount]
            [aux.fixtures :as aux.fix]
            [missinterpret.mount-configuration.resource :refer [resource-config]]))

(deftest start
  (testing "Success; path from resources"
    (mount/start-with-args {:mount-configuration.resource/path aux.fix/config.rsrc})
    (is (contains? resource-config :resource))
    (is (contains? resource-config :time))
    (mount/stop))

  (testing "Failed; no path; throw-if-missing"
    (is (thrown?
          java.lang.Exception
          (mount/start-with-args {:mount-configuration.resource/path "missing.edn"
                                  :mount-configuration.resource/throw-if-missing true})))
    (mount/stop)))
