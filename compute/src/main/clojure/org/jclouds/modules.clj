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
(ns org.jclouds.modules
  (:require
   [clojure.contrib.logging :as logging])
  (:import
   [org.jclouds.ssh SshClient ExecResponse]
   com.google.inject.Module
   org.jclouds.net.IPSocket
   [org.jclouds.compute ComputeService ComputeServiceContextFactory]
   [java.util Set Map]
   [org.jclouds.compute.domain NodeMetadata Template]
   [com.google.common.base Supplier Predicate]
   org.jclouds.compute.domain.NodeMetadataBuilder))


(defn compute-module
  []
  (org.jclouds.compute.config.JCloudsNativeStandaloneComputeServiceContextModule 
    (defrecord ClojureComputeServiceAdapter []
          org.jclouds.compute.JCloudsNativeComputeServiceAdapter
          (^NodeMetadata createNodeAndStoreCredentials [this ^String tag ^String name ^Template template ^Map credentialStore]
            ())
          (^Iterable listNodes [this ]
            ())
          (^Iterable listImages [this ]
            ())
          (^Iterable listHardware [this ]
            ())
          (^Iterable listLocations [this ]
            ())
          (^NodeMetadata getNode [this ^String id]
            ())
          (^void destroyNode [this ^String id]
            ())
          (^void rebootNode  [this ^String id]
            ()))))

(defn compute-context [module] 
  (ComputeServiceContextFactory/createStandaloneContext module))

(defrecord NodeListComputeService
    [node-list]
    org.jclouds.compute.ComputeService
    (listNodes [_] node-list)
    (getNodeMetadata
     [_ id]
     (some #(= (.getId %) id) node-list))
    (listNodesDetailsMatching
     [_ predicate]
     (filter #(.apply predicate %) node-list)))

(defn ssh-client-factory
  "Pass in a function that reifies org.jclouds.ssh.SshClient"
  [ctor]
  (reify
   org.jclouds.ssh.SshClient$Factory
   (^org.jclouds.ssh.SshClient create
    [_ ^IPSocket socket ^String username ^String password-or-key]
    (ctor socket username password-or-key))
   (^org.jclouds.ssh.SshClient create
    [_ ^IPSocket socket ^String username ^bytes password-or-key]
    (ctor socket username password-or-key))))

(defn ssh-module
  "Create a module that specifies the factory for creating an ssh service"
  [^org.jclouds.ssh.SshClient$Factory factory]
  (let [binder (atom nil)]
    (reify
     com.google.inject.Module
     (configure
      [this abinder]
      (reset! binder abinder)
      (.. @binder (bind org.jclouds.ssh.SshClient$Factory)
          (toInstance factory))))))
