(ns missinterpret.mount-configuration.resource-test
  (:require [clojure.test :refer :all]
            [mount.core :as mount]
            [aux.fixtures :as aux.fix]
            [missinterpret.mount-configuration.resource :refer [resource-config]]))

(def path "/tmp/simple-config.edn")

(use-fixtures :once (aux.fix/cp-config aux.fix/config.simple path))

(deftest start
  #_(testing "Success; path from resources"
    (mount/start-with-args {:mount-configuration.resource/path aux.fix/config.simple})
    (println "from resources> ---------------------")
    (clojure.pprint/pprint resource-config)
    (is (contains? resource-config :default))
    (is (= false (:default resource-config)))
    (mount/stop))

  (testing "Success; no path, default loaded"
    (mount/start)
    (println "default file> ---------------------")
    (clojure.pprint/pprint resource-config)

    (is (= true (:default resource-config)))
    (mount/stop))

  #_(testing "Failed; no path; throw-if-missing"
    (is (thrown?
          java.lang.Exception
          (mount/start-with-args {:mount-configuration.resource/path "missing.edn"
                                  :mount-configuration.resource/throw-if-missing true})))
    (mount/stop)))
