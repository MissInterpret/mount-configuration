(ns missinterpret.mount-configuration.file-edit-test
  (:require [missinterpret.edn-io.data-readers :as data-readers] ;; Required for tag literals during write
            [missinterpret.edn-io.edn :as edn]
            [clojure.test :refer :all]
            [mount.core :as mount]
            [aux.fixtures :as aux.fix]
            [missinterpret.mount-configuration.file :refer [file-config] :as cfg]))

(def path "/tmp/edit-config.edn")

(use-fixtures :once (aux.fix/cp-config aux.fix/config.edit path))

(deftest edit-test
  (testing "Success; fassoc - map value is added and successfully saved on stop"
    (reset! cfg/edit-atom cfg/default)
    (let [mount-args {:mount-configuration.file/path path}]
      (mount/start-with-args mount-args)
      (cfg/fassoc :test :TEST)
      (mount/stop)
      (is (-> path
              slurp
              edn/read
              (get :test)))))
  (testing "Success; fassoc-in - map value is added and successfully saved on stop"
      (let [mount-args {:mount-configuration.file/path path}]
        (reset! cfg/edit-atom cfg/default)
        (mount/start-with-args mount-args)
        (cfg/fassoc-in [:m :test] :TEST)
        (mount/stop)
        (is (-> path
                slurp
                edn/read
                (get-in [:m :test])))))
  (testing "Success; fdissoc - map value is removed and successfully saved on stop"
      (let [mount-args {:mount-configuration.file/path path}]
        (reset! cfg/edit-atom cfg/default)
        (mount/start-with-args mount-args)

        (cfg/fdissoc :m)
        (mount/stop)
        (is (not (-> path
                     slurp
                     edn/read
                     (contains? :m)))))))


