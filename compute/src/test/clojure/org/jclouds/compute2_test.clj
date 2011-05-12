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
  (:require [org.jclouds.ssh-test :as ssh-test])
  (:import
    org.jclouds.compute.domain.OsFamily
    clojure.contrib.condition.Condition
    java.net.InetAddress
    org.jclouds.scriptbuilder.domain.Statements
    org.jclouds.compute.options.TemplateOptions
    org.jclouds.compute.options.TemplateOptions$Builder
    org.jclouds.domain.Credentials
    java.util.NoSuchElementException
    ))

(defmacro with-private-vars [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context.  From users mailing
list, Alan Dipert and MeikelBrandmeyer."
  `(let ~(reduce #(conj %1 %2 `@(ns-resolve '~ns '~%2)) [] fns)
     ~@tests))

(deftest os-families-test
  (is (some #{"centos"} (map str (os-families)))))

(def *compute* (compute-service "stub" "" "" :extensions [(ssh-test/ssh-test-client ssh-test/no-op-ssh-client)]))

(defn clean-stub-fixture
  "This should allow basic tests to easily be run with another service."
  [compute-service]
  (fn [f]
    (doseq [node (nodes compute-service)]
      (destroy-node compute-service (.getId node)))
    (f)))

(use-fixtures :each (clean-stub-fixture *compute*))

(deftest compute-service?-test
  (is (compute-service? *compute*)))

(deftest as-compute-service-test
  (is (compute-service? (compute-service "stub" "user" "password")))
  (is (compute-service? *compute*))
  (is (compute-service? (compute-service (compute-context *compute*)))))

(deftest nodes-test
  (is (create-node *compute* "fred" (build-template *compute* {} )))
  (is (= 1 (count (nodes-in-group *compute* "fred"))))
  (is (= 1 (count (nodes-with-details-matching *compute* #(= (.getGroup %) "fred")))))
  (is (= 1 (count (nodes-with-details-matching *compute*
    (reify com.google.common.base.Predicate
      (apply [this input] (= (.getGroup input) "fred")))))))
  (is (= 0 (count (nodes-with-details-matching *compute* #(= (.getGroup %) "othergroup")))))
  (suspend-nodes-matching *compute* #(= (.getGroup %) "fred"))
  (is (suspended? (first (nodes-with-details-matching *compute* #(= (.getGroup %) "fred")))))
  (resume-nodes-matching *compute* #(= (.getGroup %) "fred"))
  (is (running? (first (nodes-in-group *compute* "fred"))))
  (reboot-nodes-matching *compute* #(= (.getGroup %) "fred"))
  (is (running? (first (nodes-in-group *compute* "fred"))))
  (is (create-nodes *compute* "fred" 2 (build-template *compute* {} )))
  (is (= 3 (count (nodes-in-group *compute* "fred"))))
  (is (= "fred" (group (first (nodes *compute*)))))
  (destroy-nodes-matching *compute* #(= (.getGroup %) "fred"))
  (is (terminated? (first (nodes-in-group *compute* "fred")))))

(defn localhost? [node]
  "Returns true if the localhost address is in the node's private ips"
  (seq? (some #(= (InetAddress/getLocalHost) %) (private-ips node))))

(deftest compound-predicate-test
  (is (create-node *compute* "my-group" (build-template *compute* {})))
  (is (= 0 (count (nodes-with-details-matching *compute* #(and (suspended? %) (not (localhost? %)))))))
  (is (= 0 (count (nodes-with-details-matching *compute* #(and (suspended? %) (localhost? %))))))
  (is (= 0 (count (nodes-with-details-matching *compute* #(and (running? %) (localhost? %))))))
  (is (= 1 (count (nodes-with-details-matching *compute* #(and (running? %) (not (localhost? %))))))))

(deftest run-script-on-nodes-matching-test
  (let [echo (Statements/exec "echo hello")
        script-options (.. (TemplateOptions$Builder/overrideCredentialsWith (Credentials. "user" "password"))
                        (runAsRoot false)
                        (wrapInInitScript false))
        pred #(= (.getGroup %) "scriptednode")]
    (is (create-node *compute* "scriptednode" (build-template *compute* {})))
    (is (run-script-on-nodes-matching *compute* pred echo script-options))
    (is (thrown? NoSuchElementException
      (run-script-on-nodes-matching *compute* #(= (.getGroup %) "nonexistingnode") echo script-options)))))

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
