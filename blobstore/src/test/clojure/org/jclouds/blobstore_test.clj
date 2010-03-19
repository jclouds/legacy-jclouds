(ns org.jclouds.blobstore-test
  (:use [org.jclouds.blobstore] :reload-all)
  (:use [clojure.test])
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
        data-file (java.io.File/createTempFile "jclouds" "data")]
    (try (create-container container-name)
         (create-blob container-name name data)
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
           (create-blob container-name name data)
           (is (thrown? Exception
                        (download-blob container-name name data-file)))
           (finally (.delete data-file))))))

;; TODO: more tests involving blob-specific functions

(deftest corruption-hunt
  (let [service "transient"
        account ""
        secret-key ""
        container-name "test"
        name "work-file"
        upload-filename "/home/phil/work-file"
        total-downloads 100
        threads 10
        blob-s (blobstore service account secret-key)]

    ;; upload
    (create-container blob-s container-name)
    (when-not (blob-exists? blob-s container-name name)
      (create-blob blob-s container-name name
                   (java.io.File. upload-filename)))

    ;; download
    (let [total (atom total-downloads)]
      (defn new-agent []
        (agent name))

      (defn dl-and-restart [file]
        (when-not (<= @total 0)
          (with-open [baos (java.io.ByteArrayOutputStream.)]
            (try
             (download-blob blob-s container-name file baos)
             (catch Exception e
               (with-open [of (java.io.FileOutputStream.
                               (java.io.File/createTempFile "jclouds" ".dl"))]
                 (.write of (.toByteArray baos)))
               (throw e))))
          (swap! total dec)
          (send *agent* dl-and-restart)
          file))

      (defn start-agents []
        (let [agents (map (fn [_] (new-agent))
                          (range threads))]
          (doseq [a agents]
            (send-off a dl-and-restart))
          agents))

      (let [agents (start-agents)]
        (apply await agents)
        (is (every? nil? (map agent-errors agents)))))))
