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

(ns 
  #^{:author "Adrian Cole"
     :doc "A clojure binding to the jclouds chef interface.

Here's a quick example of how to manipulate a databag on the Opscode Platform, 
which is basically Chef Server as a Service.

(use 'org.jclouds.chef)

(def client \"YOUR_CLIENT\")
;; load the rsa key from ~/.chef/CLIENT_NAME.pem
(def credential (load-pem client))

;; create a connection to the opscode platform
(def chef (chef-service \"chef\" client credential :chef.endpoint \"https://api.opscode.com/organizations/YOUR_ORG\"))

(with-chef-service [chef]
  (create-databag \"cluster-config\")
  (update-databag-item \"cluster-config\" {:id \"master\" :name \"myhost.com\"})) 

;; note that you can create your chef connection like this to do in-memory testing
(def chef (chef-service \"transientchef\" \"\" \"\"))

See http://code.google.com/p/jclouds for details."}
  org.jclouds.chef
  (:use  [org.jclouds.core])
  (:require (org.danlarkin [json :as json]))
  (:import 
        java.util.Properties
        [org.jclouds.chef ChefClient
          ChefService ChefContext ChefContextFactory]
        [org.jclouds.chef.domain DatabagItem]))
(try
 (use '[clojure.contrib.reflect :only [get-field]])
 (catch Exception e
   (use '[clojure.contrib.java-utils
          :only [wall-hack-field]
          :rename {wall-hack-field get-field}])))

(defn load-pem
  "get the pem associated with the supplied identity"
  ([#^String identity]
     (slurp (str (. System getProperty "user.home") "/.chef/" identity ".pem"))))

;; TODO find a way to pass the chef provider by default

(defn chef-service
  "Create a logged in context to a chef server.

provider \"chef\" is a remote connection, and you can pass the option
   :chef.endpoint \"https://url\" to override the endpoint

provider \"transientchef\" is for in-memory when you are looking to do 
unit testing"
  ([#^String provider #^String identity #^String credential & options]
    (let [module-keys (set (keys module-lookup))
          ext-modules (filter #(module-keys %) options)
          opts (apply hash-map (filter #(not (module-keys %)) options))]
      (.. (ChefContextFactory.)
        (createContext provider identity credential 
          (apply modules (concat ext-modules (opts :extensions)))
          (reduce #(do (.put %1 (name (first %2)) (second %2)) %1)
                   (Properties.) (dissoc opts :extensions)))
        (getChefService)))))

(defn chef-context
  "Returns a chef context from a chef service."
  [#^ChefService chef]
  (.getContext chef))

(defn chef-service?
  [object]
  (instance? ChefService object))

(defn chef-context?
  [object]
  (instance? ChefContext object))

(defn as-chef-service
  "Tries hard to produce a chef service from its input arguments"
  [& args]
  (cond
   (chef-service? (first args)) (first args)
   (chef-context? (first args)) (.getChefService (first args))
   :else (apply chef-service args)))

(defn as-chef-api
  "Tries hard to produce a chef client from its input arguments"
  [& args]
  (cond
   (chef-service? (first args)) (.getApi (.getContext (first args)))
   (chef-context? (first args)) (.getApi (first args))
   :else (.getApi (.getContext (apply chef-service args)))))

(def *chef*)

(defmacro with-chef-service
  "Specify the default chef service"
  [[& chef-or-args] & body]
  `(binding [*chef* (as-chef-service ~@chef-or-args)]
     ~@body))

(defn nodes
  "Retrieve the names of the existing nodes in your chef server."
  ([] (nodes *chef*))
  ([#^ChefService chef]
    (seq (.listNodes (as-chef-api chef)))))

(defn nodes-with-details
  "Retrieve the existing nodes in your chef server including all details."
  ([] (nodes *chef*))
  ([#^ChefService chef]
    (seq (.listNodes chef))))

(defn clients
  "Retrieve the names of the existing clients in your chef server."
  ([] (clients *chef*))
  ([#^ChefService chef]
    (seq (.listClients (as-chef-api chef)))))

(defn clients-with-details
  "Retrieve the existing clients in your chef server including all details."
  ([] (clients *chef*))
  ([#^ChefService chef]
    (seq (.listClients chef))))

(defn cookbooks
  "Retrieve the names of the existing cookbooks in your chef server."
  ([] (cookbooks *chef*))
  ([#^ChefService chef]
    (seq (.listCookbooks (as-chef-api chef)))))

(defn cookbook-versions
  "Retrieve the versions of an existing cookbook in your chef server."
  ([name] (cookbook-versions *chef*))
  ([#^ChefService name chef]
    (seq (.getVersionsOfCookbook (as-chef-api chef) name))))

(defn cookbook-versions-with-details
  "Retrieve the existing cookbook versions in your chef server including all details."
  ([] (cookbook-versions *chef*))
  ([#^ChefService chef]
    (seq (.listCookbookVersions chef))))

(defn update-run-list
  "Updates the run-list associated with a tag"
  ([run-list tag] (update-run-list run-list tag *chef*))
  ([run-list tag #^ChefService chef]
    (.updateRunListForTag chef run-list tag)))

(defn run-list
  "Retrieves the run-list associated with a tag"
  ([tag] (run-list tag *chef*))
  ([tag #^ChefService chef]
    (seq (.getRunListForTag chef tag))))

(defn create-bootstrap
  "creates a client and bootstrap script associated with a tag"
  ([tag] (create-bootstrap tag *chef*))
  ([tag #^ChefService chef]
    (.createClientAndBootstrapScriptForTag chef tag)))

(defn databags
  "Retrieve the names of the existing data bags in your chef server."
  ([] (databags *chef*))
  ([#^ChefService chef]
    (seq (.listDatabags (as-chef-api chef)))))

(defn databag-exists?
  "Predicate to check presence of a databag"
  ([databag-name]
     (databag-exists? databag-name *chef*))
  ([databag-name #^ChefService chef]
     (.databagExists (as-chef-api chef) databag-name)))

(defn delete-databag
  "Delete a data bag, including its items"
  ([databag]
    (delete-databag databag *chef*))
  ([databag chef]
    (.deleteDatabag (as-chef-api chef) databag)))

(defn create-databag
  "create a data bag"
  ([databag]
    (create-databag databag *chef*))
  ([databag chef]
    (.createDatabag (as-chef-api chef) databag)))

(defn databag-items
  "Retrieve the names of the existing items in a data bag in your chef server."
  ([databag]
    (databag-items databag *chef*))
  ([databag chef]
    (seq (.listDatabagItems (as-chef-api chef) databag))))

(defn databag-item-exists?
  "Predicate to check presence of a databag item"
  ([databag-name item-id]
     (databag-item-exists? databag-name item-id *chef*))
  ([databag-name item-id #^ChefService chef]
     (.databagExists (as-chef-api chef) databag-name item-id)))

(defn databag-item
  "Get an item from the data bag"
  ([databag item-id]
    (databag-item databag item-id *chef*))
  ([databag item-id chef]
    (json/decode-from-str (str (.getDatabagItem (as-chef-api chef) databag item-id)))))

(defn delete-databag-item
  "delete an item from the data bag"
  ([databag item-id]
    (delete-databag-item databag item-id *chef*))
  ([databag item-id chef]
    (.deleteDatabagItem (as-chef-api chef) databag item-id)))

(defn create-databag-item
  "put a new item in the data bag.  Note the Map you pass must have an :id key:

ex.
  (create-databag-item \"cluster-config\" {:id \"master\" :name \"myhost.com\"}))"
  ([databag value]
    (create-databag-item databag value *chef*))
  ([databag value chef]
    (let [value-str (json/encode-to-str value)]
      (let [value-json (json/decode-from-str value-str)]
        (.createDatabagItem  (as-chef-api chef) databag 
          (DatabagItem. (get value-json :id) value-str))))))

(defn update-databag-item
  "updates an existing item in the data bag.  Note the Map you pass must have an :id key:

ex.
  (update-databag-item \"cluster-config\" {:id \"master\" :name \"myhost.com\"}))"
  ([databag value]
    (update-databag-item databag value *chef*))
  ([databag value chef]
    (let [value-str (json/encode-to-str value)]
      (let [value-json (json/decode-from-str value-str)]
        (.updateDatabagItem  (as-chef-api chef) databag 
          (DatabagItem. (get value-json :id) value-str))))))
