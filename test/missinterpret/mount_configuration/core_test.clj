(ns missinterpret.mount-configuration.core-test
  (:require [clojure.test :refer :all]
            [mount.core :as mount]
            [aux.fixtures :as aux.fix]
            [missinterpret.mount-configuration.core :refer [config]]))

(def path "/tmp/simple-config.edn")

(use-fixtures :once (aux.fix/cp-config aux.fix/config.simple path))

(deftest start
  (testing "PATH for env loading; default configuration -> simple-config.edn"
    (mount/start-with-args {:mount-configuration.env/vars #{:PATH}})
    (is (contains? config :PATH))
    (is (contains? config :default))
    (mount/stop)))

