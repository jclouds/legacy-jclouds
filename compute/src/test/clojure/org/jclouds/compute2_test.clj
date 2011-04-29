;
;
; Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

(ns org.jclouds.compute2-test
  (:use [org.jclouds.compute2] :reload-all)
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
  [compute-service]
  (fn [f]
    (doseq [node (nodes compute-service)]
      (destroy-node (.getId node)))
    (f)))

(def *compute* (compute-service "stub" "" ""))

(use-fixtures :each (clean-stub-fixture *compute*))

(deftest compute-service?-test
  (is (compute-service? *compute*)))

(deftest as-compute-service-test
  (is (compute-service? (compute-service "stub" "user" "password")))
  (is (compute-service? *compute*))
  (is (compute-service? (compute-service (compute-context *compute*)))))

(deftest nodes-test
  (is (empty? (nodes *compute*)))
  (is (create-node "fred" *compute* (build-template *compute* {} )))
  (is (= 1 (count (nodes *compute*))))
  (is (= 1 (count (nodes-in-group "fred" *compute*))))
  (suspend-nodes-in-group "fred" *compute*)
  (is (suspended? (first (nodes-in-group "fred" *compute*))))
  (resume-nodes-in-group "fred" *compute*)
  (is (running? (first (nodes-in-group "fred" *compute*))))
  (reboot-nodes-in-group "fred" *compute*)
  (is (running? (first (nodes-in-group "fred" *compute*))))
  (is (create-nodes "fred" 2 (build-template *compute* {} ) *compute*))
  (is (= 3 (count (nodes-in-group "fred" *compute*))))
  (is (= "fred" (group (first (nodes *compute*)))))
  (destroy-nodes-in-group "fred" *compute*)
  (is (terminated? (first (nodes-in-group "fred" *compute*)))))

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
