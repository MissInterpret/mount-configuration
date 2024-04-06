(ns missinterpret.mount-configuration.file-test
  (:require [clojure.edn :as edn]
            [clojure.test :refer :all]
            [mount.core :as mount]
            [aux.fixtures :as aux.fix]
            [missinterpret.mount-configuration.file :refer [file-config] :as cfg]))

(def path "/tmp/simple-config.edn")

(use-fixtures :once (aux.fix/cp-config aux.fix/config.simple path))

(deftest start-test
  (testing "Success; valid uri"
    (let [mount-args {:mount-configuration.file/path path}]
      (mount/start-with-args mount-args)
      (is (map? file-config))
      (is (contains? file-config :test))
      (mount/stop)))

  (testing "Success; no uri"
    (mount/start)
    (is (map? file-config))
    (mount/stop))

  (testing "Failed; broken uri"
    (is (thrown? java.lang.Exception (mount/start-with-args {:mount-configuration.file/path "/a/b/c.edn"})))
    (mount/stop))

  (testing "Failed; no uri, throw-if-missing"
    (is (thrown? java.lang.Exception (mount/start-with-args {:mount-configuration.file/throw-if-missing true
                                                             :mount-configuration.file/omit-resources true})))
    (mount/stop)))


(deftest edit-test
  (testing "Success; fassoc - map value is added and successfully saved on stop"
    (let [mount-args {:mount-configuration.file/path path}]
      (mount/start-with-args mount-args)
      (cfg/fassoc :test :TEST)
      (mount/stop)
      (is (-> path
              slurp
              edn/read-string
              (get :test)))))
  (testing "Success; fassoc-in - map value is added and successfully saved on stop"
    (let [mount-args {:mount-configuration.file/path path}]
      (mount/start-with-args mount-args)
      (cfg/fassoc-in [:m :test] :TEST)
      (mount/stop)
      (is (-> path
              slurp
              edn/read-string
              (get-in [:m :test])))))
  (testing "Success; fdissoc - map value is removed and successfully saved on stop"
    (let [mount-args {:mount-configuration.file/path path}]
      (mount/start-with-args mount-args)

      (cfg/fdissoc :m)
      (mount/stop)
      (is (not (-> path
              slurp
              edn/read-string
              (contains? :m)))))))


