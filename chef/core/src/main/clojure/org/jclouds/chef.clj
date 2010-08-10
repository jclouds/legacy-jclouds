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

(ns 
  #^{:author "Adrian Cole"
     :doc "A clojure binding to the jclouds chef interface."}
  org.jclouds.chef
  (:use  org.jclouds.core (core))
  (:import 
        java.util.Properties
        [org.jclouds.chef ChefClient
          ChefService ChefContext ChefContextFactory]))
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

(defn chef-service
  "Create a logged in context."
  ([#^String identity #^String credential & options]
     (let [module-keys (set (keys module-lookup))
           ext-modules (filter #(module-keys %) options)
           opts (apply hash-map (filter #(not (module-keys %)) options))]
       (.. (ChefContextFactory.)
           (createContext identity credential 
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
    (seq (.listNodes (.getApi (.getContext chef))))))

(defn nodes-with-details
  "Retrieve the existing nodes in your chef server including all details."
  ([] (nodes *chef*))
  ([#^ChefService chef]
    (seq (.listNodesDetails chef))))