(ns missinterpret.mount-configuration.file-test
  (:require [missinterpret.edn-io.data-readers :as data-readers] ;; Required for tag literals during write
            [clojure.pprint :refer [pprint]]
            [missinterpret.edn-io.edn :as edn]
            [clojure.java.io :as io]
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

  (testing "Success; path provided, file does not exist. No edit, no write on exit"
    (reset! cfg/edit-atom cfg/default)
    (let [f (io/file "/tmp/missing.edn")
          mount-args {:mount-configuration.file/path (str f)}]
      (mount/start-with-args mount-args)
      (is (map? file-config))
      (mount/stop)
      (is (not (.exists f)))))

  (testing "Success; path provided, file does not exist. Edited, written on exit"
    (reset! cfg/edit-atom cfg/default)
    (let [f (io/file "/tmp/missing.edn")
          mount-args {:mount-configuration.file/path (str f)}]
      (mount/start-with-args mount-args)
      (is (map? (cfg/fassoc :test :TEST)))
      (mount/stop)
      (is (.exists f))
      (= {:test :TEST} (slurp f) edn/read)
      (io/delete-file f)))

  (testing "Failed; broken uri"
    (reset! cfg/edit-atom cfg/default)
    (is (thrown? java.lang.Exception (mount/start-with-args {:mount-configuration.file/path "/a/b/c.edn"
                                                             :mount-configuration.file/throw-if-missing true})))
    (mount/stop)))
