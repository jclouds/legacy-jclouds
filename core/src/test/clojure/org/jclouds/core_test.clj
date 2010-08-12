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

(ns org.jclouds.core-test
  (:use [org.jclouds.core] :reload-all)
  (:use clojure.test))

(defmacro with-private-vars [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context.  From users mailing
list, Alan Dipert and MeikelBrandmeyer."
  `(let ~(reduce #(conj %1 %2 `@(ns-resolve '~ns '~%2)) [] fns)
     ~@tests))

(with-private-vars [org.jclouds.core [instantiate]]
  (deftest instantiate-test
    (is (instance? String (instantiate 'java.lang.String)))))

(deftest modules-empty-test
  (is (.isEmpty (modules))))

(deftest modules-instantiate-test
  (binding [module-lookup
            (assoc module-lookup
              :string 'java.lang.String)]
    (is (instance? String (first (modules :string))))
    (is (= 1 (count (modules :string)))))
  (testing "pre-instantiated"
    (is (instance? String (first (modules "string")))))
  (testing "symbol"
    (is (instance? String (first (modules 'java.lang.String))))))

(deftest modules-instantiate-fail-test
  (binding [module-lookup
            (assoc module-lookup
              :non-existing 'this.doesnt.Exist)]
    (is (.isEmpty (modules :non-existing)))))
