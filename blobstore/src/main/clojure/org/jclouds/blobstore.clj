(ns
#^{:doc
"
a lib for interacting with jclouds BlobStore.

Current supported services are:
   [s3, azureblob, atmos, cloudfiles]

Here's a quick example of how to view blob resources in rackspace

(ns example.jclouds
  (:use org.jclouds.blobstore)
  (:use clojure.contrib.pprint)
)

 (def user \"rackspace_username\")
 (def password \"rackspace_password\")
 (def blobstore-name \"cloudfiles\")

 (def blobstore (blobstore-context blobstore-name user password ))

 (pprint (containers blobstore))
 (pprint (blobs blobstore "your_container_name" ))

"}
org.jclouds.blobstore
  (:use clojure.contrib.duck-streams)
  (:import java.io.File)
  (:import org.jclouds.blobstore.BlobStore)
  (:import org.jclouds.blobstore.BlobStoreContext)
  (:import org.jclouds.blobstore.BlobStoreContextFactory)
  (:import org.jclouds.blobstore.domain.Blob)
  (:import org.jclouds.blobstore.options.ListContainerOptions))

(defn blobstore-context
  ([{service :service account :account key :key}]
     (blobstore-context service account key))
  ([s a k] (.createContext (new BlobStoreContextFactory) s a k )))

(defn containers [blobstore] (.list (.getBlobStore blobstore) ))

(defn blobs

"
http://code.google.com/p/jclouds

list the blobs in a container:
blobstore container -> blobs

list the blobs in a container under a path:
blobstore container dir -> blobs

example: (pprint
(blobs
(blobstore-context flightcaster-creds)
\"somecontainer\" \"some-dir\"))
"
  ([blobstore container-name]
     (.list (.getBlobStore blobstore) container-name ))

  ([blobstore container-name dir]
     (.list (.getBlobStore blobstore) container-name (.inDirectory (new ListContainerOptions) dir) ))
)

(defn put-blob

"
http://code.google.com/p/jclouds

Create an blob representing text data:
container, name, string -> etag
"
  ([blobstore container-name name data]
    (.putBlob (.getBlobStore blobstore) container-name (doto (.newBlob (.getBlobStore blobstore) name) (.setPayload data))))
)

