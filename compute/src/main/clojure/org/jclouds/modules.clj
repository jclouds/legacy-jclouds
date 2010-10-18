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
   java.util.Set
   [org.jclouds.compute.domain NodeMetadata Template]
   [com.google.common.base Supplier Predicate]
   [org.jclouds.compute.strategy AddNodeWithTagStrategy DestroyNodeStrategy RebootNodeStrategy GetNodeMetadataStrategy ListNodesStrategy]
   org.jclouds.compute.domain.NodeMetadataBuilder))


(defn compute-module
  []
  (.. (org.jclouds.compute.config.StandaloneComputeServiceContextModule$Builder.) 
    (defineAddNodeWithTagStrategy (defrecord ClojureAddNodeWithTagStrategy []
          AddNodeWithTagStrategy
          (^NodeMetadata execute [this ^String tag ^String name ^Template template]
            ())))
    (defineDestroyNodeStrategy (defrecord ClojureDestroyNodeStrategy []
          DestroyNodeStrategy
          (^NodeMetadata execute [this ^String id]
            ())))
    (defineRebootNodeStrategy (defrecord ClojureRebootNodeStrategy []
          RebootNodeStrategy
          (^NodeMetadata execute [this ^String id]
            ())))
    (defineGetNodeMetadataStrategy (defrecord ClojureGetNodeMetadataStrategy []
          GetNodeMetadataStrategy
          (^NodeMetadata execute [this ^String id]
            ())))
    (defineListNodesStrategy (defrecord ClojureListNodesStrategy []
          ListNodesStrategy
          (^Iterable list [this ]
            ())
            (^Iterable listDetailsOnNodesMatching [this ^Predicate filter]
            ())
          ))
      ;; this needs to return Set<Hardware>

    (defineHardwareSupplier
     (defrecord HardwareSupplier []
          Supplier
          (get [this]
            ())
          ))
      ;; this needs to return Set<Image>

  (defineImageSupplier (defrecord ImageSupplier []
          Supplier
          ( get [this]
            ())
          ))
  ;; this needs to return Set<Location>
    (defineLocationSupplier (defrecord LocationSupplier []
          Supplier
          ( get [this]
            ())
          ))
    (build)

  ))

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
