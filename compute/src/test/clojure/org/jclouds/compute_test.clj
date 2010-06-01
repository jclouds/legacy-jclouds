;;
;; Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
;;
;; ====================================================================
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;; http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.
;; ====================================================================
;;

(ns org.jclouds.compute-test
  (:use [org.jclouds.compute] :reload-all)
  (:use clojure.test))

(defmacro with-private-vars [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context.  From users mailing
list, Alan Dipert and MeikelBrandmeyer."
  `(let ~(reduce #(conj %1 %2 `@(ns-resolve '~ns '~%2)) [] fns)
     ~@tests))

(deftest os-families-test
  (is (some #{"centos"} (map str (os-families)))))


(defn clean-stub-fixture
  "This should allow basic tests to easily be run with another service."
  [service account key & options]
  (fn [f]
    (with-compute-service [(apply compute-service service account key options)]
      (doseq [node (nodes)]
        (destroy-node (.getId node)))
      (f))))

(use-fixtures :each (clean-stub-fixture "stub" "" ""))

(deftest compute-service?-test
  (is (compute-service? *compute*)))

(deftest as-compute-service-test
  (is (compute-service? (compute-service "stub" "user" "password")))
  (is (compute-service? (as-compute-service *compute*)))
  (is (compute-service? (as-compute-service (compute-context *compute*)))))