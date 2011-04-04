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

(ns org.jclouds.blobstore-test
  (:use [org.jclouds.blobstore] :reload-all)
  (:use [clojure.test])
  (:import [org.jclouds.blobstore BlobStoreContextFactory]
           [org.jclouds.crypto CryptoStreams]
           [java.io ByteArrayOutputStream]
           [org.jclouds.util Strings2]))

(defn clean-stub-fixture
  "This should allow basic tests to easily be run with another service."
  [service account key & options]
  (fn [f]
    (with-blobstore [(apply blobstore service account key options)]
      (doseq [container (containers)]
        (delete-container (.getName container)))
      (f))))

(use-fixtures :each (clean-stub-fixture "transient" "" ""))

(deftest blobstore?-test
  (is (blobstore? *blobstore*)))

(deftest as-blobstore-test
  (is (blobstore? (blobstore "transient" "user" "password")))
  (is (blobstore? (as-blobstore *blobstore*)))
  (is (blobstore? (as-blobstore (blobstore-context *blobstore*)))))

(deftest create-existing-container-test
  (is (not (container-exists? "")))
  (is (create-container "fred"))
  (is (container-exists? "fred")))

(deftest create-container-test
  (is (create-container "fred"))
  (is (container-exists? "fred")))

(deftest locations-test
  (is (not (empty? (locations))))
  (is (create-container "fred" (first (locations)))))

(deftest containers-test
  (is (empty? (containers)))
  (is (create-container "fred"))
  (is (= 1 (count (containers)))))

(deftest list-container-test
  (is (create-container "container"))
  (is (empty? (list-container "container")))
  (is (upload-blob "container" "blob1" "blob1"))
  (is (upload-blob "container" "blob2" "blob2"))
  (is (= 2 (count (list-container "container"))))
  (is (= 1 (count (list-container "container" :max-results 1))))
  (create-directory "container" "dir")
  (is (upload-blob "container" "dir/blob2" "blob2"))
  (is (= 3 (count-blobs  "container")))
  (is (= 3 (count (list-container "container"))))
  (is (= 4 (count (list-container "container" :recursive true))))
  (is (= 3 (count (list-container "container" :with-details true))))
  (is (= 1 (count (list-container "container" :in-directory "dir")))))

(deftest list-blobs-test
  (is (create-container "container"))
  (is (empty? (list-blobs "container")))
  (is (empty? (list-blobs "container" "/a" *blobstore*))))

(deftest get-blob-test
  (is (create-container "blob"))
  (is (upload-blob "blob" "blob1" "blob1"))
  (is (upload-blob "blob" "blob2" "blob2"))
  (is (= "blob2" (Strings2/toStringAndClose (get-blob-stream "blob" "blob2")))))

(deftest download-blob-test
  (let [name "test"
        container-name "test-container"
        data "test content"
        data-file (java.io.File/createTempFile "jclouds" "data")]
    (try (create-container container-name)
         (upload-blob container-name name data)
         (download-blob container-name name data-file)
         (is (= data (slurp (.getAbsolutePath data-file))))
         (finally (.delete data-file)))))

(deftest download-checksum-test
  (binding [get-blob (fn [blobstore c-name name]
                       (let [blob (.newBlob blobstore name)
                             md (.getMetadata blob)]
                         (.setPayload blob "bogus payload")
                         (.setContentMD5 md (.getBytes "bogus MD5"))
                         blob))]
    (let [name "test"
          container-name "test-container"
          data "test content"
          data-file (java.io.File/createTempFile "jclouds" "data")]
      (try (create-container container-name)
           (upload-blob container-name name data)
           (is (thrown? Exception
                        (download-blob container-name name data-file)))
           (finally (.delete data-file))))))

(deftest sign-blob-request-test
  (testing "delete"
    (let [request (sign-blob-request "container" "path" {:method :delete})]
      (is (= "http://localhost/container/path" (str (.getEndpoint request))))
      (is (= "DELETE" (.getMethod request)))))
  (testing "default request"
    (let [request (sign-blob-request "container" "path")]
      (is (= "http://localhost/container/path" (str (.getEndpoint request))))
      (is (= "GET" (.getMethod request)))))
  (testing "get"
    (let [request (sign-blob-request "container" "path" {:method :get})]
      (is (= "http://localhost/container/path" (str (.getEndpoint request))))
      (is (= "GET" (.getMethod request)))))
  (testing "put"
    (let [request (sign-blob-request
                   "container" "path" {:method :put :content-length 10})]
      (is (= "http://localhost/container/path" (str (.getEndpoint request))))
      (is (= "PUT" (.getMethod request)))
      (is (= "10" (first (.get (.getHeaders request) "Content-Length"))))
      (is (nil?
           (first (.get (.getHeaders request) "Content-Type"))))))
  (testing "put with headers"
    (let [request (sign-blob-request
                   "container" "path"
                   {:method :put :content-length 10
                    :content-type "x"
                    :content-language "en"
                    :content-disposition "f"
                    :content-encoding "g"})]
      (is (= "PUT" (.getMethod request)))
      (is (= "10" (first (.get (.getHeaders request) "Content-Length"))))
      (is (= "x" (first (.get (.getHeaders request) "Content-Type"))))
      (is (= "en" (first (.get (.getHeaders request) "Content-Language"))))
      (is (= "f" (first (.get (.getHeaders request) "Content-Disposition"))))
      (is (= "g" (first (.get (.getHeaders request) "Content-Encoding")))))))

(deftest sign-get-test
  (let [request (sign-get "container" "path")]
    (is (= "http://localhost/container/path" (str (.getEndpoint request))))
    (is (= "GET" (.getMethod request)))))

(deftest sign-put-test
  (let [request (sign-put "container"
                          (blob2 "path" {:content-length 10}))]
    (is (= "http://localhost/container/path" (str (.getEndpoint request))))
    (is (= "PUT" (.getMethod request)))
    (is (= "10" (first (.get (.getHeaders request) "Content-Length"))))
    (is (nil?
         (first (.get (.getHeaders request) "Content-Type"))))))

(deftest sign-put-with-headers-test
  (let [request (sign-put
                 "container"
                 (blob2 "path" {:content-length 10
                                :content-type "x"
                                :content-language "en"
                                :content-disposition "f"
                                :content-encoding "g"}))]
    (is (= "PUT" (.getMethod request)))
    (is (= "10" (first (.get (.getHeaders request) "Content-Length"))))
    (is (= "x" (first (.get (.getHeaders request) "Content-Type"))))
    (is (= "en" (first (.get (.getHeaders request) "Content-Language"))))
    (is (= "f" (first (.get (.getHeaders request) "Content-Disposition"))))
    (is (= "g" (first (.get (.getHeaders request) "Content-Encoding"))))))

(deftest sign-delete-test
  (let [request (sign-delete "container" "path")]
    (is (= "http://localhost/container/path" (str (.getEndpoint request))))
    (is (= "DELETE" (.getMethod request)))))

(deftest blob2-test
  (let [a-blob (blob2 "test-name" {:payload (.getBytes "test-payload")
                                   :calculate-md5 true})]
    (is (= (seq (.. a-blob (getPayload) (getContentMetadata) (getContentMD5)))
           (seq (CryptoStreams/md5 (.getBytes "test-payload")))))))

;; TODO: more tests involving blob-specific functions

(deftest corruption-hunt
  (let [container-name "test"
        name "work-file"
        total-downloads 100
        threads 10]

    ;; upload
    (create-container container-name)
    (when-not (blob-exists? container-name name)
      (let [data-stream (java.io.ByteArrayOutputStream.)]
        (dotimes [i 5000000] (.write data-stream i))
        (upload-blob container-name name (.toByteArray data-stream))))

    ;; download
    (let [total (atom total-downloads)]
      (defn new-agent []
        (agent name))

      (defn dl-and-restart [blob-s file]
        (when-not (<= @total 0)
          (with-open [baos (java.io.ByteArrayOutputStream.)]
            (try
             (download-blob container-name file baos blob-s)
             (catch Exception e
               (with-open [of (java.io.FileOutputStream.
                               (java.io.File/createTempFile "jclouds" ".dl"))]
                 (.write of (.toByteArray baos)))
               (throw e))))
          (swap! total dec)
          (send *agent* (partial dl-and-restart blob-s))
          file))

      (defn start-agents []
        (let [agents (map (fn [_] (new-agent))
                          (range threads))]
          (doseq [a agents]
            (send-off a (partial dl-and-restart *blobstore*)))
          agents))

      (let [agents (start-agents)]
        (apply await agents)
        (is (every? nil? (map agent-errors agents)))))))
