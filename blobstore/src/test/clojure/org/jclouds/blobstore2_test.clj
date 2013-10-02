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

(ns org.jclouds.blobstore2-test
  (:use [org.jclouds.blobstore2] :reload-all)
  (:use [clojure.test])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream
            StringBufferInputStream]
           [org.jclouds.util Strings2]))

(defn clean-stub-fixture
  "This should allow basic tests to easily be run with another service."
  [blobstore]
  (fn [f]
    (doseq [container (containers blobstore)]
      (delete-container blobstore (.getName container)))
    (f)))

(def blobstore-stub (blobstore "transient" "" ""))

(use-fixtures :each (clean-stub-fixture blobstore-stub))

(deftest blobstore?-test
  (is (blobstore? blobstore-stub)))

(deftest as-blobstore-test
  (is (blobstore? (blobstore "transient" "user" "password"))))

(deftest create-existing-container-test
  (is (not (container-exists? blobstore-stub "")))
  (is (create-container blobstore-stub "fred"))
  (is (container-exists? blobstore-stub "fred")))

(deftest create-container-test
  (is (create-container blobstore-stub "fred"))
  (is (container-exists? blobstore-stub "fred")))

(deftest locations-test
  (is (not (empty? (locations blobstore-stub))))
  (is (create-container blobstore-stub "fred"
                        :location (first (locations blobstore-stub)))))

(deftest containers-test
  (is (empty? (containers blobstore-stub)))
  (is (create-container blobstore-stub "fred"))
  (is (= 1 (count (containers blobstore-stub)))))

(deftest blobs-test
  (is (create-container blobstore-stub "container"))
  (is (empty? (blobs blobstore-stub "container")))
  (is (put-blob blobstore-stub "container"
                (blob "blob1" :payload "blob1" :calculate-md5 true)))
  (is (put-blob blobstore-stub "container"
                (blob "blob2" :payload "blob2" :calculate-md5 true)))
  (is (= 2 (count (blobs blobstore-stub "container"))))
  (is (= 1 (count (blobs blobstore-stub "container" :max-results 1))))
  (create-directory blobstore-stub "container" "dir")
  (is (put-blob blobstore-stub "container"
                (blob "dir/blob2" :payload "blob2" :calculate-md5 true)))
  (is (= 3 (count-blobs blobstore-stub "container")))
  (is (= 3 (count (blobs blobstore-stub "container"))))
  (is (= 4 (count (blobs blobstore-stub "container" :recursive true))))
  (is (= 3 (count (blobs blobstore-stub "container" :with-details true))))
  (is (= 1 (count (blobs blobstore-stub "container" :in-directory "dir")))))

(deftest large-container-list-test
  (let [container-name "test"
        total-blobs 5000]
    ;; create a container full of blobs
    (create-container blobstore-stub container-name)
    (dotimes [i total-blobs] (put-blob blobstore-stub container-name
                                       (blob (str i)
                                             :payload (str i)
                                             :calculate-md5 true)))
    ;; verify
    (is (= total-blobs (count-blobs blobstore-stub container-name)))))

(deftest container-seq-test
  (is (create-container blobstore-stub "container"))
  (is (empty? (container-seq blobstore-stub "container")))
  (is (empty? (container-seq blobstore-stub "container" "/a"))))

(deftest get-blob-test
  (is (create-container blobstore-stub "blob"))
  (is (put-blob blobstore-stub "blob"
                (blob "blob1" :payload "blob1" :calculate-md5 true)))
  (is (put-blob blobstore-stub "blob"
                (blob "blob2" :payload "blob2" :calculate-md5 true)))
  (is (= "blob2" (Strings2/toStringAndClose (get-blob-stream blobstore-stub
                                                             "blob" "blob2")))))

(deftest put-blob-test
  ;; Check multipart works
  (is (create-container blobstore-stub "blobs"))
  (is (put-blob blobstore-stub "blobs"
                (blob "blob1" :payload "blob1")
                :multipart? true))
  (is (= 1 (count (blobs blobstore-stub "blobs")))))

(deftest sign-get-test
  (let [request (sign-get blobstore-stub "container" "path")]
    (is (= "http://localhost/container/path" (str (.getEndpoint request))))
    (is (= "GET" (.getMethod request)))))

(deftest sign-put-test
  (let [request (sign-put blobstore-stub "container"
                          (blob "path" :content-length 10))]
    (is (= "http://localhost/container/path" (str (.getEndpoint request))))
    (is (= "PUT" (.getMethod request)))
    (is (= "10" (first (.get (.getHeaders request) "Content-Length"))))
    (is (nil?
         (first (.get (.getHeaders request) "Content-Type"))))))

(deftest sign-put-with-headers-test
  (let [request (sign-put blobstore-stub
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
  (let [request (sign-delete blobstore-stub "container" "path")]
    (is (= "http://localhost/container/path" (str (.getEndpoint request))))
    (is (= "DELETE" (.getMethod request)))))

(deftest blob-test
  (let [a-blob (blob "test-name"
                     :payload "test-payload"
                     :calculate-md5 true)]
    (is (= (seq (.. a-blob (getPayload) (getContentMetadata) (getContentMD5)))
           (seq (.digest (doto (java.security.MessageDigest/getInstance "MD5")
                               (.reset)
                               (.update (.getBytes "test-payload")))))))))

(deftest payload-protocol-test
  (is (instance? org.jclouds.io.Payload (payload "test")))
  (is (blob "blob1" :payload (payload "blob1")))
  (is (create-container blobstore-stub "container"))
  (is (= "blob1"
         (do
           (put-blob blobstore-stub "container"
                     (blob "blob1"
                           :payload "blob1"))
           (Strings2/toStringAndClose (get-blob-stream blobstore-stub
                                                       "container" "blob1")))))
  (is (= "blob2"
         (do
           (put-blob blobstore-stub "container"
                     (blob "blob2"
                           :payload (StringBufferInputStream. "blob2")))
           (Strings2/toStringAndClose (get-blob-stream blobstore-stub
                                                       "container" "blob2")))))
  (is (= "blob3"
         (do
           (put-blob blobstore-stub "container"
                     (blob "blob3"
                           :payload (.getBytes "blob3")))
           (Strings2/toStringAndClose (get-blob-stream blobstore-stub
                                                       "container" "blob3")))))
  (is (= "blob4"
         (do
           (put-blob blobstore-stub "container"
                     (blob "blob4"
                           :payload #(.write % (.getBytes "blob4"))))
           (Strings2/toStringAndClose (get-blob-stream blobstore-stub
                                                       "container" "blob4"))))))

;; TODO: more tests involving blob-specific functions
