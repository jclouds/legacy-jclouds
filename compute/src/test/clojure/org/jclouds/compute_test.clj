;
;
; Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
;
; ====================================================================
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
; ====================================================================
;

(ns org.jclouds.compute-test
  (:use [org.jclouds.compute] :reload-all)
  (:use clojure.test)
  (:import
   org.jclouds.compute.domain.OsFamily
   clojure.contrib.condition.Condition))

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

(deftest nodes-test
  (is (empty? (nodes)))
  (is (create-node "fred" (build-template
         *compute* {} )))
  (is (= 1 (count (nodes))))
  (is (= 1 (count (nodes-in-group "fred"))))
  (suspend-nodes-in-group "fred")
  (is (suspended? (first (nodes-in-group "fred"))))
  (resume-nodes-in-group "fred")
  (is (running? (first (nodes-in-group "fred"))))
  (reboot-nodes-in-group "fred")
  (is (running? (first (nodes-in-group "fred"))))
  (is (create-nodes "fred" 2 (build-template
         *compute* {} )))
  (is (= 3 (count (nodes-in-group "fred"))))
  (is (= "fred" (group (first (nodes)))))
  (destroy-nodes-in-group "fred")
  (is (terminated? (first (nodes-in-group "fred")))))

(deftest nodes-test-deprecated
  (is (empty? (nodes)))
  (is (run-node "deprecated" (build-template
         *compute* {} )))
  (is (= 1 (count (nodes))))
  (is (= 1 (count (nodes-with-tag "deprecated"))))
  (suspend-nodes-with-tag "deprecated")
  (is (suspended? (first (nodes-with-tag "deprecated"))))
  (resume-nodes-with-tag "deprecated")
  (is (running? (first (nodes-with-tag "deprecated"))))
  (reboot-nodes-with-tag "deprecated")
  (is (running? (first (nodes-with-tag "deprecated"))))
  (is (run-nodes "deprecated" 2 (build-template
         *compute* {} )))
  (is (= 3 (count (nodes-with-tag "deprecated"))))
  (is (= "deprecated" (tag (first (nodes)))))
  (destroy-nodes-with-tag "deprecated")
  (is (terminated? (first (nodes-with-tag "deprecated")))))

(deftest build-template-test
  (let [service (compute-service "stub" "user" "password")]
    (testing "nullary"
      (is (>= (-> (build-template service {:fastest true})
                  bean :hardware bean :processors first bean :cores)
              8.0)))
    (testing "one arg"
      (is (> (-> (build-template service {:min-ram 512})
                 bean :hardware bean :ram)
             512)))
    (testing "enumerated"
      (is (= OsFamily/CENTOS
             (-> (build-template service {:os-family :centos})
                 bean :image bean :operatingSystem bean :family))))
    (testing "varags"
      (is (java.util.Arrays/equals
           (int-array [22 8080])
           (-> (build-template service {:inbound-ports [22 8080]})
               bean :options bean :inboundPorts))))
    (testing "invalid"
      (is (thrown? Condition (build-template service {:xx :yy}))))))
