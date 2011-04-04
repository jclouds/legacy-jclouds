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

(ns org.jclouds.blobstore2
  "A clojure binding for the jclouds BlobStore.

Current supported services are:
   [transient, filesystem, azureblob, atmos, walrus, scaleup-storage,
    ninefold-storage, googlestorage, synaptic, peer1-storage, aws-s3,
    eucalyptus-partnercloud-s3, cloudfiles-us, cloudfiles-uk, swift,
    scality-rs2, hosteurope-storage, tiscali-storage]

Here's a quick example of how to viewresources in rackspace

    (use 'org.jclouds.blobstore2)

    (def user \"rackspace_username\")
    (def password \"rackspace_password\")
    (def blobstore-name \"cloudfiles\")

    (def the-blobstore (blobstore blobstore-name user password))

    (pprint (locations the-blobstore))
    (pprint (containers the-blobstore))
    (pprint (blobs the-blobstore your_container_name))

See http://code.google.com/p/jclouds for details."
  (:use [org.jclouds.core])
  (:import [java.io File FileOutputStream OutputStream]
           java.util.Properties
           [org.jclouds.blobstore
            AsyncBlobStore domain.BlobBuilder BlobStore BlobStoreContext
            BlobStoreContextFactory domain.BlobMetadata domain.StorageMetadata
            domain.Blob options.ListContainerOptions]
           org.jclouds.io.Payloads
           org.jclouds.io.payloads.PhantomPayload
           java.util.Arrays
           [java.security DigestOutputStream MessageDigest]
           com.google.common.collect.ImmutableSet))

(try
  (require '[clojure.contrib.io :as io])
  (catch Exception e
    (require '[clojure.contrib.duck-streams :as io])))

(defn blobstore
  "Create a logged in context.
Options for communication style
     :sync and :async.
Options can also be specified for extension modules
     :log4j :enterprise :ning :apachehc :bouncycastle :joda :gae"
  [#^String provider #^String provider-identity #^String provider-credential
   & options]
  (let [module-keys (set (keys module-lookup))
        ext-modules (filter #(module-keys %) options)
        opts (apply hash-map (filter #(not (module-keys %)) options))]
    (let [context (.. (BlobStoreContextFactory.)
                      (createContext
                       provider provider-identity provider-credential
                       (apply modules
                              (concat ext-modules (opts :extensions)))
                       (reduce #(do (.put %1 (name (first %2)) (second %2)) %1)
                               (Properties.) (dissoc opts :extensions))))]
      (if (some #(= :async %) options)
        (.getAsyncBlobStore context)
        (.getBlobStore context)))))

(defn blobstore-context
  "Returns a blobstore context from a blobstore."
  [blobstore]
  (.getContext blobstore))

(defn blob?
  [object]
  (instance? Blob))

(defn blobstore?
  [object]
  (or (instance? BlobStore object)
      (instance? AsyncBlobStore object)))

(defn blobstore-context?
  [object]
  (instance? BlobStoreContext object))

(defn containers
  "List all containers in a blobstore."
  [blobstore] (.list blobstore))

(def #^{:private true} list-option-map
     {:after-marker #(.afterMarker %1 %2)
      :in-directory #(.inDirectory %1 %2)
      :max-results #(.maxResults %1 %2)
      :with-details #(when %2 (.withDetails %1))
      :recursive #(when %2 (.recursive %1))})

(defn list-container
  "Low-level container listing. Use list-blobs where possible since
  it's higher-level and returns a lazy seq. Options are:
  :after-marker string
  :in-direcory path
  :max-results n
  :with-details true
  :recursive true"
  [blobstore container-name & args]
  (let [options (apply hash-map args)
        list-options (reduce
                      (fn [lco [k v]]
                        ((list-option-map k) lco v)
                        lco)
                      (ListContainerOptions.)
                      options)]
    (.list blobstore container-name list-options)))

(defn- list-blobs-chunk [blobstore container prefix & [marker]]
  (apply list-container blobstore container
         (concat (when prefix
                   [:in-directory prefix])
                 (when (string? marker)
                   [:after-marker marker]))))

(defn- list-blobs-chunks [blobstore container prefix marker]
  (when marker
    (let [chunk (list-blobs-chunk blobstore container prefix marker)]
      (lazy-seq (cons chunk
                      (list-blobs-chunks blobstore container prefix
                                         (.getNextMarker chunk)))))))

(defn- concat-elements
  "Make a lazy concatenation of the lazy sequences contained in coll.
   Lazily evaluates coll.
   Note: (apply concat coll) or (lazy-cat coll) are not lazy wrt coll itself."
  [coll]
  (if-let [s (seq coll)]
    (lazy-seq (concat (first s) (concat-elements (next s))))))

(defn list-blobs
  "Returns a lazy seq of all blobs in the given container."
  ([blobstore container]
     (list-blobs blobstore container nil))
  ([blobstore container prefix]
     (concat-elements (list-blobs-chunks blobstore container prefix :start))))

(defn locations
  "Retrieve the available container locations for the blobstore context."
  [^BlobStore blobstore]
  (seq (.listAssignableLocations blobstore)))

(defn create-container
  "Create a container."
  ([blobstore container-name]
     (create-container blobstore container-name nil))
  ([^BlobStore blobstore container-name location]
     (.createContainerInLocation blobstore location container-name)))

(defn clear-container
  "Clear a container."
  [^BlobStore container-name]
  (.clearContainer blobstore container-name))

(defn delete-container
  "Delete a container."
  [^BlobStore blobstore container-name]
  (.deleteContainer blobstore container-name))

(defn container-exists?
  "Predicate to check presence of a container"
  [^BlobStore blobstore container-name]
  (.containerExists blobstore container-name))

(defn directory-exists?
  "Predicate to check presence of a directory"
  [^BlobStore blobstore container-name path]
  (.directoryExists blobstore container-name path))

(defn create-directory
  "Create a directory path."
  [^BlobStore blobstore container-name path]
  (.createDirectory blobstore container-name path))

(defn delete-directory
  "Delete a directory path."
  [^BlobStore blobstore container-name path]
  (.deleteDirectory blobstore container-name path))

(defn blob-exists?
  "Predicate to check presence of a blob"
  [^BlobStore blobstore container-name path]
  (.blobExists blobstore container-name path))

(defn put-blob
  "Put a blob.  Metadata in the blob determines location."
  [^BlobStore blobstore container-name blob]
  (.putBlob blobstore container-name blob))

(defn blob-metadata
  "Get metadata from given path"
  [^BlobStore blobstore container-name path]
  (.blobMetadata blobstore container-name path))

(defn get-blob
  "Get blob from given path"
  [^BlobStore blobstore container-name path]
  (.getBlob blobstore container-name path))

(defn sign-get
  "Get a signed http GET request for manipulating a blob in another
   application, Ex. curl."
  [^BlobStore blobstore container-name name]
  (.signGetBlob (.. blobstore getContext getSigner) container-name name))

(defn sign-put
  "Get a signed http PUT request for manipulating a blob in another
   application, Ex. curl. A Blob with at least the name and content-length
   must be given."
  [^BlobStore blobstore container-name ^Blob blob]
  (.signPutBlob (.. blobstore getContext getSigner)
                container-name
                blob))

(defn sign-delete
  "Get a signed http DELETE request for manipulating a blob in another
   applicaiton, Ex. curl."
  [^BlobStore blobstore container-name name]
  (.signRemoveBlob (.. blobstore getContext getSigner) container-name name))

(defn get-blob-stream
  "Get an inputstream from the blob at a given path"
  [^BlobStore blobstore container-name path]
  (.getInput (.getPayload (.getBlob blobstore container-name path))))

(defn remove-blob
  "Remove blob from given path"
  [^BlobStore blobstore container-name path]
  (.removeBlob blobstore container-name path))

(defn count-blobs
  "Count blobs"
  [^BlobStore blobstore container-name]
  (.countBlobs blobstore container-name))

(defn blobs
  "List the blobs in a container:
     blobstore container -> blobs

   List the blobs in a container under a path:
     blobstore container dir -> blobs

   example:
     (pprint
       (blobs
         (blobstore-context flightcaster-creds)
         \"somecontainer\" \"some-dir\"))"
  ([^BlobStore blobstore container-name]
     (.list blobstore container-name))
  ([^BlobStore blobstore container-name dir]
     (.list blobstore container-name
            (.inDirectory (new ListContainerOptions) dir))))

(defn blob
  "Create a new blob with the specified payload and options."
  ([^BlobStore blobstore ^String name
    {:keys [payload content-type content-length content-md5 calculate-md5
            content-disposition content-encoding content-language metadata]}]
     {:pre [(not (and content-md5 calculate-md5))
            (not (and (nil? payload) calculate-md5))]}
     (let [blob-builder (if payload
                          (.payload (.blobBuilder blobstore name) payload)
                          (.forSigning (.blobBuilder blobstore name)))
           blob-builder (if content-length ;; Special case, arg is prim.
                          (.contentLength blob-builder content-length)
                          blob-builder)
           blob-builder (if calculate-md5 ;; Only do calculateMD5 OR contentMD5.
                          (.calculateMD5 blob-builder)
                          (if content-md5
                            (.contentMD5 blob-builder content-md5)
                            blob-builder))]
       (doto blob-builder
         (.contentType content-type)
         (.contentDisposition content-disposition)
         (.contentEncoding content-encoding)
         (.contentLanguage content-language)
         (.userMetadata metadata))
       (.build blob-builder))))

(defn md5-blob
  "add a content md5 to a blob, or make a new blob that has an md5.
   note that this implies rebuffering, if the blob's payload isn't repeatable"
  ([^Blob blob]
     (Payloads/calculateMD5 blob))
  ([^BlobStore blobstore ^String name payload]
     (md5-blob (blob blobstore name {:payload payload}))))

(defn upload-blob
  "Create anrepresenting text data:
     container, name, string -> etag"
  ([^BlobStore blobstore container-name name data]
     (put-blob blobstore container-name
               (md5-blob blobstore name data))))

(define-accessors StorageMetadata "blob" type id name
  location-id uri last-modfied)
(define-accessors BlobMetadata "blob" content-type)

(defn blob-etag [blob]
  (.getETag blob))

(defn blob-md5 [blob]
  (.getContentMD5 blob))