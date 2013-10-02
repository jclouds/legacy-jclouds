;
; Licensed to the Apache Software Foundation (ASF) under one or more
; contributor license agreements.  See the NOTICE file distributed with
; this work for additional information regarding copyright ownership.
; The ASF licenses this file to You under the Apache License, Version 2.0
; (the "License"); you may not use this file except in compliance with
; the License.  You may obtain a copy of the License at
;
;     http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;

(ns org.jclouds.compute2-test
  (:use [org.jclouds.compute2] :reload-all)
  (:use clojure.test)
  (:require [org.jclouds.ssh-test :as ssh-test])
  (:import
    org.jclouds.compute.domain.OsFamily
    java.net.InetAddress
    org.jclouds.scriptbuilder.domain.Statements
    org.jclouds.compute.options.TemplateOptions
    org.jclouds.compute.options.TemplateOptions$Builder
    org.jclouds.compute.options.RunScriptOptions
    org.jclouds.compute.options.RunScriptOptions$Builder
    org.jclouds.domain.LoginCredentials
    java.util.NoSuchElementException
    ))

(defmacro with-private-vars [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context.  From users mailing
list, Alan Dipert and MeikelBrandmeyer."
  `(let ~(reduce #(conj %1 %2 `@(ns-resolve '~ns '~%2)) [] fns)
     ~@tests))

(deftest os-families-test
  (is (some #{"centos"} (map str (os-families)))))

(def compute-stub (compute-service "stub" "compute2.clj" "" :extensions [(ssh-test/ssh-test-client ssh-test/no-op-ssh-client)]))

(defn clean-stub-fixture
  "This should allow basic tests to easily be run with another service."
  [compute-service]
  (fn [f]
    (doseq [node (nodes compute-service)]
      (destroy-node compute-service (.getId node)))
    (f)))

(use-fixtures :each (clean-stub-fixture compute-stub))

(deftest compute-service?-test
  (is (compute-service? compute-stub)))

(deftest as-compute-service-test
  (is (compute-service? (compute-service "stub" "compute2.clj" "")))
  (is (compute-service? compute-stub))
  (is (compute-service? (compute-service (compute-context compute-stub)))))

(deftest nodes-test
  (is (create-node compute-stub "fred" (build-template compute-stub {} )))
  (is (= 1 (count (nodes-in-group compute-stub "fred"))))
  ;; pass in a function that selects node metadata based on NodeMetadata field
  (is (= 1 (count (nodes-with-details-matching compute-stub (in-group? "fred")))))
  ;; or make your query inline
  (is (= 1 (count (nodes-with-details-matching compute-stub #(= (.getGroup %) "fred")))))
  ;; or get real fancy, and use the underlying Predicate object jclouds uses
  (is (= 1 (count (nodes-with-details-matching compute-stub
    (reify com.google.common.base.Predicate
      (apply [this input] (= (.getGroup input) "fred")))))))
  (is (= 0 (count (nodes-with-details-matching compute-stub (in-group? "othergroup")))))
  (suspend-nodes-matching compute-stub (in-group? "fred"))
  (is (suspended? (first (nodes-with-details-matching compute-stub (in-group? "fred")))))
  (resume-nodes-matching compute-stub (in-group? "fred"))
  (is (running? (first (nodes-in-group compute-stub "fred"))))
  (reboot-nodes-matching compute-stub (in-group? "fred"))
  (is (running? (first (nodes-in-group compute-stub "fred"))))
  (is (create-nodes compute-stub "fred" 2 (build-template compute-stub {} )))
  (is (= 3 (count (nodes-in-group compute-stub "fred"))))
  (is (= "fred" (group (first (nodes compute-stub)))))
  (destroy-nodes-matching compute-stub (in-group? "fred"))
  (is (terminated? (first (nodes-in-group compute-stub "fred")))))

(defn localhost? [node]
  "Returns true if the localhost address is in the node's private ips"
  (seq? (some #(= "localhost" %) (private-ips node))))

(deftest compound-predicate-test
  (is (create-node compute-stub "my-group" (build-template compute-stub {})))
  (is (= 0 (count (nodes-with-details-matching compute-stub #(and (suspended? %) (not (localhost? %)))))))
  (is (= 0 (count (nodes-with-details-matching compute-stub #(and (suspended? %) (localhost? %))))))
  (is (= 0 (count (nodes-with-details-matching compute-stub #(and (running? %) (localhost? %))))))
  (is (= 1 (count (nodes-with-details-matching compute-stub #(and (running? %) (not (localhost? %))))))))

(deftest run-script-on-nodes-matching-with-options-test
  (let [echo (Statements/exec "echo hello")
        script-options (.. (RunScriptOptions$Builder/overrideLoginCredentials (.build (.password (.user (org.jclouds.domain.LoginCredentials/builder) "user")  "pwd")))
                        (runAsRoot false)
                        (wrapInInitScript false))
        pred #(= (.getGroup %) "scriptednode")]
    (is (create-node compute-stub "scriptednode" (build-template compute-stub {})))
    (is (run-script-on-nodes-matching compute-stub pred echo script-options))
    (is (thrown? NoSuchElementException
      (run-script-on-nodes-matching compute-stub #(= (.getGroup %) "nonexistingnode") echo script-options)))))

(deftest run-script-on-node-with-options-test
  (let [echo (Statements/exec "echo hello")
        script-options (.. (RunScriptOptions$Builder/overrideLoginCredentials (.build (.password (.user (org.jclouds.domain.LoginCredentials/builder) "user")  "pwd")))
                        (runAsRoot false)
                        (wrapInInitScript false))
        test_node (create-node compute-stub "scriptednode" (build-template compute-stub {}))]
    (is (run-script-on-node compute-stub (id test_node) echo script-options))
    (is (thrown? NoSuchElementException
      (run-script-on-node compute-stub "nonexistingnode" echo script-options)))))

(deftest build-template-test
  (let [service (compute-service "stub" "compute2.clj" "")]
    (testing "nullary"
      (is (>= (-> (build-template service {:fastest true})
                  bean :hardware bean :processors first bean :cores)
              8.0)))
    (testing "one arg"
      (is (> (-> (build-template service {:min-ram 512})
                 bean :hardware bean :ram)
             512))
      (let [credentials (.build (.password (.user (org.jclouds.domain.LoginCredentials/builder) "user")  "pwd"))
            f (juxt #(.identity %) #(.credential %))
            template (build-template
                      service
                      {:override-login-credentials credentials})
            node (create-node service "something" template)]
        (is (= (-> node bean :credentials f)
               (f credentials)))
        (let [identity "fred"
            f #(.identity %)
            template (build-template service {:override-login-user identity})
            node (create-node service "something" template)]
        (is (= (-> node bean :credentials f) identity)))
        (let [credential "fred"
              f #(.credential %)
              template (build-template
                        service {:override-login-password credential})
              node (create-node service "something" template)]
          (is (= (-> node bean :credentials f) credential)))))
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
      (is (thrown? Exception (build-template service {:xx :yy}))))))
