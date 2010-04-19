;;
;; Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
;;
;; ====================================================================
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;; http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.
;; ====================================================================
;;

(ns org.jclouds.blobstore
  "A clojure binding for the jclouds BlobStore.

Current supported services are:
   [s3, azureblob, atmos, cloudfiles]

Here's a quick example of how to view blob resources in rackspace

    (use 'org.jclouds.blobstore)
    (use 'clojure.contrib.pprint)

    (def user \"rackspace_username\")
    (def password \"rackspace_password\")
    (def blobstore-name \"cloudfiles\")

    (with-blobstore [blobstore-name user password]
      (pprint (containers))
      (pprint (blobs blobstore your_container_name)))

See http://code.google.com/p/jclouds for details."
  (:use [org.jclouds.core])
  (:import [java.io File FileOutputStream OutputStream]
           [org.jclouds.blobstore
            AsyncBlobStore BlobStore BlobStoreContext BlobStoreContextFactory
            domain.BlobMetadata domain.StorageMetadata domain.Blob
            options.ListContainerOptions]
           [org.jclouds.encryption.internal JCEEncryptionService]
           [java.util Arrays]
           [com.google.common.collect ImmutableSet]))

(try
  (require '[clojure.contrib.io :as io])
  (catch Exception e
    (require '[clojure.contrib.duck-streams :as io])))

(defn blobstore
  "Create a logged in context.
Options for communication style
     :sync and :async.
Options can also be specified for extension modules
     :log4j :enterprise :httpnio :apachehc :bouncycastle :joda :gae"
  [#^String service #^String account #^String key & options]
  (let [context
        (.createContext
         (BlobStoreContextFactory.) service account key
         (apply modules (filter #(not (#{:sync :async} %)) options)))]
    (if (some #(= :async %) options)
      (.getAsyncBlobStore context)
      (.getBlobStore context))))

(defn blobstore-context
  "Returns a blobstore context from a blobstore."
  [blobstore]
  (.getContext blobstore))

(defn blobstore?
  [object]
  (or (instance? BlobStore object)
      (instance? AsyncBlobStore object)))

(defn blobstore-context?
  [object]
  (instance? BlobStoreContext object))

(defn as-blobstore
  "Tries hard to produce a blobstore from its input arguments"
  [& args]
  (cond
   (blobstore? (first args)) (first args)
   (blobstore-context? (first args)) (.getBlobStore (first args))
   :else (apply blobstore args)))

(def *blobstore*)

(def *encryption-service* (JCEEncryptionService.)) ;; TODO: use guice

(def *max-retries* 3)

(defmacro with-blobstore [[& blobstore-or-args] & body]
  `(binding [*blobstore* (as-blobstore ~@blobstore-or-args)]
     ~@body))

(defn containers
  "List all containers in a blobstore."
  ([] (containers *blobstore*))
  ([blobstore] (.list blobstore)))

(def #^{:private true} list-option-map
     {:after-marker #(.afterMarker %1 %2)
      :in-directory #(.inDirectory %1 %2)
      :max-results #(.maxResults %1 %2)
      :with-details #(when %2 (.withDetails %1))
      :recursive #(when %2 (.recursive %1))})

(defn list-container
  "List a container. Options are:
  :after-marker string
  :in-direcory path
  :max-results n
  :with-details true
  :recursive true"
  [blobstore & args]
  (if (blobstore? blobstore)
    (let [[container-name & args] args
          options (apply hash-map args)
          list-options (reduce
                        (fn [lco [k v]]
                          ((list-option-map k) lco v)
                          lco)
                        (ListContainerOptions.)
                        options)]
      (.list blobstore container-name list-options))
    (apply list-container *blobstore* blobstore args)))

(defn create-container
  "Create a container."
  ([container-name]
     (create-container container-name "default" *blobstore*))
  ([container-name location-name]
     (create-container container-name location-name *blobstore*))
  ([container-name location-name blobstore]
     (.createContainerInLocation blobstore location-name container-name)))

(defn clear-container
  "Clear a container."
  ([container-name]
     (clear-container container-name *blobstore*))
  ([container-name blobstore]
     (.clearContainer blobstore container-name)))

(defn delete-container
  "Delete a container."
  ([container-name]
     (delete-container container-name *blobstore*))
  ([container-name blobstore]
     (.deleteContainer blobstore container-name)))

(defn container-exists?
  "Predicate to check presence of a container"
  ([container-name]
     (container-exists? container-name *blobstore*))
  ([container-name blobstore]
     (.containerExists blobstore container-name)))

(defn directory-exists?
  "Predicate to check presence of a directory"
  ([container-name path]
     (directory-exists? container-name path *blobstore*))
  ([container-name path blobstore]
     (.directoryExists blobstore container-name path)))

(defn create-directory
  "Create a directory path."
  ([container-name path]
     (create-directory container-name path *blobstore*))
  ([container-name path blobstore]
     (.createDirectory blobstore container-name path)))

(defn delete-directory
  "Delete a directory path."
  ([container-name path]
     (delete-directory container-name path *blobstore*))
  ([container-name path blobstore]
     (.deleteDirectory blobstore container-name path)))

(defn blob-exists?
  "Predicate to check presence of a blob"
  ([container-name path]
     (blob-exists? container-name path *blobstore*))
  ([container-name path blobstore]
     (.blobExists blobstore container-name path)))

(defn put-blob
  "Put a blob.  Metadata in the blob determines location."
  ([container-name blob]
     (put-blob container-name blob *blobstore*))
  ([container-name blob blobstore]
     (.putBlob blobstore container-name blob)))

(defn blob-metadata
  "Get blob metadata from given path"
  ([container-name path]
     (blob-metadata container-name path *blobstore*))
  ([container-name path blobstore]
     (.blobMetadata blobstore container-name path)))

(defn get-blob
  "Get blob from given path"
  ([container-name path]
     (get-blob container-name path *blobstore*))
  ([container-name path blobstore]
     (.getBlob blobstore container-name path)))

(defn remove-blob
  "Remove blob from given path"
  ([container-name path]
     (remove-blob container-name path *blobstore*))
  ([container-name path blobstore]
     (.removeBlob blobstore container-name path)))

(defn count-blobs
  "Count blobs"
  ([container-name]
     (count-blobs container-name *blobstore*))
  ([container-name blobstore]
     (.countBlob blobstore container-name)))

(defn blobs
  "List the blobs in a container:
blobstore container -> blobs

list the blobs in a container under a path:
blobstore container dir -> blobs

example:
    (pprint
      (blobs
      (blobstore-context flightcaster-creds)
       \"somecontainer\" \"some-dir\"))"
  ([blobstore container-name]
     (.list (as-blobstore blobstore) container-name))

  ([blobstore container-name dir]
     (.list (as-blobstore blobstore) container-name
            (.inDirectory (new ListContainerOptions) dir))))

(defn upload-blob
  "Create an blob representing text data:
container, name, string -> etag"
  ([container-name name data] ;; TODO: allow payload to be a stream
     (upload-blob container-name name data *blobstore*))
  ([container-name name data blobstore]
     (put-blob container-name
               (doto (.newBlob blobstore name)
                 (.setPayload data)
                 (.generateMD5))
               blobstore)))

(defmulti #^{:arglists '[[container-name name target]
                         [container-name name target blobstore]]}
  download-blob (fn [& args]
                  (if (= (count args) 3)
                    ::short-form
                    (class (last (butlast args))))))

(defmethod download-blob ::short-form
  [container-name name target]
  (download-blob container-name name target *blobstore*))

(defmethod download-blob OutputStream [container-name name target blobstore
                                       & [retries]]
  (let [blob (get-blob container-name name blobstore)
        digest-stream (.md5OutputStream ;; TODO: not all clouds use MD5
                       *encryption-service* target)]
    (io/copy (.getContent blob) digest-stream)
    (let [digest (.getMD5 digest-stream)
          metadata-digest (.getContentMD5 (.getMetadata blob))]
      (when-not (Arrays/equals digest metadata-digest)
        (if (<= (or retries 0) *max-retries*)
          (recur container-name name target blobstore [(inc (or retries 1))])
          (throw (Exception. (format "Download failed for %s/%s"
                                     container-name name))))))))

(defmethod download-blob File [container-name name target blobstore]
  (download-blob container-name name (FileOutputStream. target) blobstore))

(define-accessors StorageMetadata "blob" type id name
  location-id uri last-modfied)
(define-accessors BlobMetadata "blob" content-type)

(defn blob-etag [blob]
  (.getETag blob))

(defn blob-md5 [blob]
  (.getContentMD5 blob))
