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

(ns org.jclouds.blobstore2-test
  (:use [org.jclouds.blobstore2] :reload-all)
  (:use [clojure.test])
  (:import [org.jclouds.blobstore BlobStoreContextFactory]
           [org.jclouds.crypto CryptoStreams]
           [java.io ByteArrayOutputStream]
           [org.jclouds.util Strings2]))

(defn clean-stub-fixture
  "This should allow basic tests to easily be run with another service."
  [blobstore]
  (fn [f]
    (doseq [container (containers blobstore)]
      (delete-container blobstore (.getName container)))
    (f)))

(def *blobstore* (blobstore "transient" "" ""))

(use-fixtures :each (clean-stub-fixture *blobstore*))

(deftest blobstore?-test
  (is (blobstore? *blobstore*)))

(deftest as-blobstore-test
  (is (blobstore? (blobstore "transient" "user" "password")))
  (is (blobstore? *blobstore*)))

(deftest create-existing-container-test
  (is (not (container-exists? *blobstore* "")))
  (is (create-container *blobstore* "fred"))
  (is (container-exists? *blobstore* "fred")))

(deftest create-container-test
  (is (create-container *blobstore* "fred"))
  (is (container-exists? *blobstore* "fred")))

(deftest locations-test
  (is (not (empty? (locations *blobstore*))))
  (is (create-container *blobstore* "fred"
                        :location (first (locations *blobstore*)))))

(deftest containers-test
  (is (empty? (containers *blobstore*)))
  (is (create-container *blobstore* "fred"))
  (is (= 1 (count (containers *blobstore*)))))

(deftest blobs-test
  (is (create-container *blobstore* "container"))
  (is (empty? (blobs *blobstore* "container")))
  (is (put-blob *blobstore* "container"
                (blob "blob1" :payload "blob1" :calculate-md5 true)))
  (is (put-blob *blobstore* "container"
                (blob "blob2" :payload "blob2" :calculate-md5 true)))
  (is (= 2 (count (blobs *blobstore* "container"))))
  (is (= 1 (count (blobs *blobstore* "container" :max-results 1))))
  (create-directory *blobstore* "container" "dir")
  (is (put-blob *blobstore* "container"
                (blob "dir/blob2" :payload "blob2" :calculate-md5 true)))
  (is (= 3 (count-blobs *blobstore* "container")))
  (is (= 3 (count (blobs *blobstore* "container"))))
  (is (= 4 (count (blobs *blobstore* "container" :recursive true))))
  (is (= 3 (count (blobs *blobstore* "container" :with-details true))))
  (is (= 1 (count (blobs *blobstore* "container" :in-directory "dir")))))

(deftest large-container-list-test
  (let [container-name "test"
        total-blobs 5000]
    ;; create a container full of blobs
    (create-container *blobstore* container-name)
    (dotimes [i total-blobs] (put-blob *blobstore* container-name
                                       (blob (str i)
                                             :payload (str i)
                                             :calculate-md5 true)))
    ;; verify
    (is (= total-blobs (count-blobs *blobstore* container-name)))))

(deftest container-seq-test
  (is (create-container *blobstore* "container"))
  (is (empty? (container-seq *blobstore* "container")))
  (is (empty? (container-seq *blobstore* "container" "/a"))))

(deftest get-blob-test
  (is (create-container *blobstore* "blob"))
  (is (put-blob *blobstore* "blob"
                (blob "blob1" :payload "blob1" :calculate-md5 true)))
  (is (put-blob *blobstore* "blob"
                (blob "blob2" :payload "blob2" :calculate-md5 true)))
  (is (= "blob2" (Strings2/toStringAndClose (get-blob-stream *blobstore*
                                                             "blob" "blob2")))))

(deftest put-blob-test
  ;; Check multipart works
  (is (create-container *blobstore* "blobs"))
  (is (put-blob *blobstore* "blobs"
                (blob "blob1" :payload "blob1")
                :multipart? true))
  (is (= 1 (count (blobs *blobstore* "blobs")))))

(deftest sign-get-test
  (let [request (sign-get *blobstore* "container" "path")]
    (is (= "http://localhost/container/path" (str (.getEndpoint request))))
    (is (= "GET" (.getMethod request)))))

(deftest sign-put-test
  (let [request (sign-put *blobstore* "container"
                          (blob "path" :content-length 10))]
    (is (= "http://localhost/container/path" (str (.getEndpoint request))))
    (is (= "PUT" (.getMethod request)))
    (is (= "10" (first (.get (.getHeaders request) "Content-Length"))))
    (is (nil?
         (first (.get (.getHeaders request) "Content-Type"))))))

(deftest sign-put-with-headers-test
  (let [request (sign-put *blobstore*
                 "container"
                 (blob "path"
                       :content-length 10
                       :content-type "x"
                       :content-language "en"
                       :content-disposition "f"
                       :content-encoding "g"))]
    (is (= "PUT" (.getMethod request)))
    (is (= "10" (first (.get (.getHeaders request) "Content-Length"))))
    (is (= "x" (first (.get (.getHeaders request) "Content-Type"))))
    (is (= "en" (first (.get (.getHeaders request) "Content-Language"))))
    (is (= "f" (first (.get (.getHeaders request) "Content-Disposition"))))
    (is (= "g" (first (.get (.getHeaders request) "Content-Encoding"))))))

(deftest sign-delete-test
  (let [request (sign-delete *blobstore* "container" "path")]
    (is (= "http://localhost/container/path" (str (.getEndpoint request))))
    (is (= "DELETE" (.getMethod request)))))

(deftest blob-test
  (let [a-blob (blob "test-name"
                     :payload (.getBytes "test-payload")
                     :calculate-md5 true)]
    (is (= (seq (.. a-blob (getPayload) (getContentMetadata) (getContentMD5)))
           (seq (CryptoStreams/md5 (.getBytes "test-payload")))))))

;; TODO: more tests involving blob-specific functions
