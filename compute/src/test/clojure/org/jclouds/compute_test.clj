(ns org.jclouds.compute-test
  (:use [org.jclouds.compute] :reload-all)
  (:use clojure.test))

(defmacro with-private-vars [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context.  From users mailing
list, Alan Dipert and MeikelBrandmeyer."
  `(let ~(reduce #(conj %1 %2 `@(ns-resolve '~ns '~%2)) [] fns)
     ~@tests))

(with-private-vars [org.jclouds.compute [instantiate]]
  (deftest instantiate-test
    (is (instance? String (instantiate 'java.lang.String)))))

(deftest os-families-test
  (is (some #{"centos"} (map str (os-families)))))

(deftest modules-empty-test
  (is (.isEmpty (modules))))

(deftest modules-instantiate-test
  (binding [org.jclouds.compute/module-lookup
            (assoc org.jclouds.compute/module-lookup
              :string 'java.lang.String)]
    (is (instance? String (first (modules :string))))
    (is (= 1 (count (modules :string))))))

(deftest modules-instantiate-fail-test
  (binding [org.jclouds.compute/module-lookup
            (assoc org.jclouds.compute/module-lookup
              :non-existing 'this.doesnt.Exist)]
    (is (.isEmpty (modules :non-existing)))))
