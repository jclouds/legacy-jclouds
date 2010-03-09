(ns org.jclouds.blobstore-test
  (:use [org.jclouds.blobstore] :reload-all)
  (:use clojure.test)
  (:import [org.jclouds.blobstore BlobStoreContextFactory]
           [java.io ByteArrayOutputStream]))

(def stub-context (.createContext (BlobStoreContextFactory.) "transient" "" ""))
(def stub-blobstore (.getBlobStore stub-context))

(defn clean-stub-fixture [f]
  (with-blobstore [stub-blobstore]
    (doseq [container (containers)]
      (delete-container (.getName container)))
    (f)))

(use-fixtures :each clean-stub-fixture)

(deftest blobstore?-test
  (is (blobstore? stub-blobstore)))

(deftest blobstore-context?-test
  (is (blobstore-context? stub-context)))

(deftest blobstore-context-test
  (is (= stub-context (blobstore-context stub-blobstore))))

(deftest as-blobstore-test
  (is (blobstore? (blobstore "transient" "user" "password")))
  (is (blobstore? (as-blobstore stub-blobstore)))
  (is (blobstore? (as-blobstore stub-context))))

(deftest with-blobstore-test
  (with-blobstore [stub-blobstore]
    (is (= stub-blobstore *blobstore*))))

(deftest create-existing-container-test
  (is (not (container-exists? stub-blobstore "")))
  (is (not (container-exists? "")))
  (is (create-container stub-blobstore "fred"))
  (is (container-exists? stub-blobstore "fred")))

(deftest create-container-test
  (is (create-container stub-blobstore "fred"))
  (is (container-exists? stub-blobstore "fred")))

(deftest containers-test
  (is (empty? (containers stub-blobstore)))
  (is (create-container stub-blobstore "fred"))
  (is (= 1 (count (containers stub-blobstore)))))

(deftest list-container-test
  (is (create-container "container"))
  (is (empty? (list-container "container")))
  (is (create-blob "container" "blob1" "blob1"))
  (is (create-blob "container" "blob2" "blob2"))
  (is (= 2 (count (list-container "container"))))
  (is (= 1 (count (list-container "container" :max-results 1))))
  (create-directory "container" "dir")
  (is (create-blob "container" "dir/blob2" "blob2"))
  (is (= 3 (count (list-container "container"))))
  (is (= 4 (count (list-container "container" :recursive))))
  (is (= 1 (count (list-container "container" :in-directory "dir")))))

(deftest download-blob-test
  (let [name "test"
        container-name "test-container"
        data "test content"
        baos (ByteArrayOutputStream.)]
    (create-container container-name)
    (create-blob container-name name data)
    (download-blob container-name name baos)
    (is (= data (.toString baos)))))

;; TODO: more tests involving blob-specific functions

