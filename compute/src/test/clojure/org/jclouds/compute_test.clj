(ns org.jclouds.compute-test
  (:use [org.jclouds.compute] :reload-all)
  (:use clojure.test))

(deftest os-families-test
  (is (some #{"centos"} (map str (os-families)))))
