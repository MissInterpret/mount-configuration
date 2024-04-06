(ns missinterpret.mount-configuration.file-test
  (:require [clojure.edn :as edn]
            [clojure.test :refer :all]
            [mount.core :as mount]
            [aux.fixtures :as aux.fix]
            [missinterpret.mount-configuration.file :refer [file-config] :as cfg]))

(def path "/tmp/file-config.edn")

(use-fixtures :once (aux.fix/cp-config aux.fix/config.file path))

(deftest start-test
  (testing "Success; valid uri"
    (reset! cfg/edit-atom cfg/default)
    (let [mount-args {:mount-configuration.file/path path}]
      (mount/start-with-args mount-args)
      (is (map? file-config))
      (is (contains? file-config :file))
      (mount/stop)))

  (testing "Success; no uri"
    (reset! cfg/edit-atom cfg/default)
    (mount/start)
    (is (map? file-config))
    (mount/stop))

  (testing "Failed; broken uri"
    (reset! cfg/edit-atom cfg/default)
    (is (thrown? java.lang.Exception (mount/start-with-args {:mount-configuration.file/path "/a/b/c.edn"})))
    (mount/stop))

  (testing "Failed; no uri, throw-if-missing"
    (reset! cfg/edit-atom cfg/default)
    (is (thrown? java.lang.Exception (mount/start-with-args {:mount-configuration.file/throw-if-missing true
                                                             :mount-configuration.file/omit-resources true})))
    (mount/stop)))


