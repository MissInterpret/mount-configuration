(ns missinterpret.mount-configuration.core-test
  (:require [clojure.test :refer :all]
            [mount.core :as mount]
            [aux.fixtures :as aux.fix]
            [missinterpret.mount-configuration.core :refer [config]]))

(def path "/tmp/file-config.edn")

(use-fixtures :once (aux.fix/cp-config aux.fix/config.file path))

#_(deftest start
  (testing "PATH for env loading; default configuration -> file-config.edn"
    (mount/start-with-args {:mount-configuration.env/vars #{:PATH}
                            :mount-configuration.file/path path})
    (is (:PATH config))
    (is (:file config))
    (mount/stop)))

