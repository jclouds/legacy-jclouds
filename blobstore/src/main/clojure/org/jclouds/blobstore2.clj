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

(ns org.jclouds.blobstore2
  "A clojure binding for the jclouds BlobStore.

Current supported services are:
   [transient, filesystem, azureblob, atmos, walrus, scaleup-storage,
    ninefold-storage, googlestorage, synaptic, peer1-storage, aws-s3,
    eucalyptus-partnercloud-s3, cloudfiles-us, cloudfiles-uk, swift,
    scality-rs2, hosteurope-storage, tiscali-storage]

Here's a quick example of how to view resources in rackspace

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
           [org.jclouds ContextBuilder]
           [org.jclouds.blobstore
            AsyncBlobStore domain.BlobBuilder BlobStore BlobStoreContext
            domain.BlobMetadata domain.StorageMetadata
            domain.Blob domain.internal.BlobBuilderImpl options.PutOptions
            options.PutOptions$Builder
            options.CreateContainerOptions options.ListContainerOptions]
           [org.jclouds.io Payload Payloads payloads.StreamingPayload]
           java.util.Arrays
           [java.security DigestOutputStream MessageDigest]
           com.google.common.collect.ImmutableSet))

;;
;; Payload support for creating Blobs.
;;

(defprotocol PayloadSource
  "Various types can have PayloadSource extended onto them so that they are
   easily coerced into a Payload."
  (^Payload payload [arg] "Coerce arg into a Payload."))

(extend-protocol PayloadSource
  Payload
  (payload [p] p)
  java.io.InputStream
  (payload [is] (Payloads/newInputStreamPayload is))
  String
  (payload [s] (Payloads/newStringPayload s))
  java.io.File
  (payload [f] (Payloads/newFilePayload f))
  clojure.lang.IFn
  ;; This will let you pass a closure to payload that takes an OutputStream
  ;; as argument and writes to it when called from a StreamingPayload.
  (payload [func]
           (StreamingPayload. (reify org.jclouds.io.WriteTo
                                     (writeTo [this output-stream]
                                              (func output-stream))))))

;; something in clojure 1.3 (namespaces?) does not like a private type called byte-array-type, 
;; so we refer to (class (make-array ...)) directly; and it only parses if it is its own block, 
;; hence separating it from the above
(extend-protocol PayloadSource
  (class (make-array Byte/TYPE 0))
  (payload [ba] (Payloads/newByteArrayPayload ba)))

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
    (let [context (.. (ContextBuilder/newBuilder provider)
                      (credentials provider-identity provider-credential)
                      (modules (apply modules (concat ext-modules (opts :extensions))))
                      (overrides (reduce #(do (.put %1 (name (first %2)) (second %2)) %1)
                        (Properties.) (dissoc opts :extensions)))
                      (buildView BlobStoreContext))]
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

(defn blobs
  "Returns a set of blobs in the given container, as directed by the
   query options below.
   Options are:
     :after-marker string
     :in-directory path
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

(defn- container-seq-chunk
  [blobstore container prefix marker]
  (apply blobs blobstore container
         (concat (when prefix
                   [:in-directory prefix])
                 (when (string? marker)
                   [:after-marker marker]))))

(defn- container-seq-chunks [blobstore container prefix marker]
  (when marker ;; When getNextMarker returns null, there's no more.
    (let [chunk (container-seq-chunk blobstore container prefix marker)]
      (lazy-seq (cons chunk
                      (container-seq-chunks blobstore container prefix
                                            (.getNextMarker chunk)))))))

(defn- concat-elements
  "Make a lazy concatenation of the lazy sequences contained in coll.
   Lazily evaluates coll.
   Note: (apply concat coll) or (lazy-cat coll) are not lazy wrt coll itself."
  [coll]
  (if-let [s (seq coll)]
    (lazy-seq (concat (first s) (concat-elements (next s))))))

(defn container-seq
  "Returns a lazy seq of all blobs in the given container."
  ([blobstore container]
     (container-seq blobstore container nil))
  ([blobstore container prefix]
     ;; :start has no special meaning, it is just a non-null (null indicates
     ;; end), non-string (markers are strings).
     (concat-elements (container-seq-chunks blobstore container prefix
                                            :start))))

(defn locations
  "Retrieve the available container locations for the blobstore context."
  [^BlobStore blobstore]
  (seq (.listAssignableLocations blobstore)))

(defn create-container
  "Create a container."
  [^BlobStore blobstore container-name & {:keys [location public-read?]}]
  (let [cco (CreateContainerOptions.)
        cco (if public-read? (.publicRead cco) cco)]
    (.createContainerInLocation blobstore location container-name cco)))

(defn clear-container
  "Clear a container."
  [^BlobStore blobstore container-name]
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
  [^BlobStore blobstore container-name blob & {:keys [multipart?]}]
  (let [options (if multipart?
                  (PutOptions$Builder/multipart)
                  (PutOptions.))]
    (.putBlob blobstore container-name blob options)))

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
  (.getInput (.getPayload (get-blob blobstore container-name path))))

(defn remove-blob
  "Remove blob from given path"
  [^BlobStore blobstore container-name path]
  (.removeBlob blobstore container-name path))

(defn count-blobs
  "Count blobs"
  [^BlobStore blobstore container-name]
  (.countBlobs blobstore container-name))

(defn blob
  "Create a new blob with the specified payload and options.

   The payload argument can be anything accepted by the PayloadSource protocol."
  ([^String name &
    {:keys [payload content-type content-length content-md5 calculate-md5
            content-disposition content-encoding content-language metadata]}]
     {:pre [(not (and content-md5 calculate-md5))
            (not (and (nil? payload) calculate-md5))]}
     (let [blob-builder (.name (BlobBuilderImpl.) name)
           blob-builder (if payload
                          (.payload blob-builder
                                    (org.jclouds.blobstore2/payload payload))
                          (.forSigning blob-builder))
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

(define-accessors StorageMetadata "blob" type id name
  location-id uri last-modified)
(define-accessors BlobMetadata "blob" content-type)

(defn blob-etag [blob]
  (.getETag blob))

(defn blob-md5 [blob]
  (.getContentMD5 blob))
