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

(ns org.jclouds.chef-test
  (:use [org.jclouds.chef] :reload-all)
  (:use [clojure.test]))

(defn clean-stub-fixture
  "This should allow basic tests to easily be run with another service."
  [service account key & options]
  (fn [f]
 (with-chef-service [(apply chef-service service account key options)]
(doseq [databag (databags)]
  (delete-databag databag))
(f))))

(use-fixtures :each (clean-stub-fixture "transientchef" "" ""))

(deftest chef-service?-test
  (is (chef-service? *chef*)))

(deftest as-chef-service-test
  (is (chef-service? (chef-service "transientchef" "" "")))
  (is (chef-service? (as-chef-service *chef*)))
  (is (chef-service? (as-chef-service (chef-context *chef*)))))

(deftest create-existing-databag-test
  (is (not (databag-exists? "")))
  (create-databag "fred")
  (is (databag-exists? "fred")))

(deftest create-databag-test
  (create-databag "fred")
  (is (databag-exists? "fred")))

(deftest databags-test
  (is (empty? (databags)))
  (create-databag "fred")
  (is (= 1 (count (databags)))))

(deftest databag-items-test
  (create-databag "databag")
  (is (empty? (databag-items "databag")))
  (is (create-databag-item "databag" {:id "databag-item1" :value "databag-value1"}))
  (is (create-databag-item "databag" {:id "databag-item2" :value "databag-value2"}))
  (is (= 2 (count (databag-items "databag")))))

(deftest databag-item-test
  (create-databag "databag")
  (is (create-databag-item "databag" {:id "databag-item1" :value "databag-value1"}))
  (is (create-databag-item "databag" {:id "databag-item2" :value "databag-value2"}))
  (is (= {:id "databag-item2" :value "databag-value2"} (databag-item "databag" "databag-item2"))))

(deftest run-list-test
  (update-run-list #{"recipe[foo]"} "tag")
  (is (= ["recipe[foo]"] (run-list "tag"))))
