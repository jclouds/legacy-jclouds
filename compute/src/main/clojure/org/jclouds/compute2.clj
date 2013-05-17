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

(ns org.jclouds.compute2
  "A clojure binding to the jclouds ComputeService.

 jclouds supports many compute providers including Amazon EC2 (aws-ec2),
 Rackspace Cloud Servers (cloudservers-us), GoGrid (gogrid), and BlueLock
 vCloud (bluelock-vcloud-zone01).  There are over a dozen to choose from.  

 Current supported providers are available via the following dependency:
  org.jclouds/jclouds-allcompute

 You can inquire about which providers are loaded via the following:
  (seq (org.jclouds.providers.Providers/allCompute))
  (seq (org.jclouds.apis.Apis/allCompute))

Here's an example of getting some compute configuration from rackspace:

  (use 'org.jclouds.compute2)
  (use 'clojure.pprint)

  (def provider \"cloudservers-us\")
  (def provider-identity \"username\")
  (def provider-credential \"password\")

  ;; create a compute service
  (def compute
    (compute-service provider provider-identity provider-credential))

  (pprint (locations compute))
  (pprint (images compute))
  (pprint (nodes compute))
  (pprint (hardware-profiles compute)))

Here's an example of creating and running a small linux node in the group webserver:

  ;; create a compute service using ssh and log4j extensions
  (def compute
    (compute-service
      provider provider-identity provider-credential :sshj :log4j))

  (create-node \"webserver\" compute)

  See http://code.google.com/p/jclouds for details.
  "
  (:use org.jclouds.core
    (org.jclouds predicate) [clojure.core.incubator :only (-?>)])
  (:import java.io.File
    java.util.Properties
    [org.jclouds ContextBuilder]
    [org.jclouds.domain Location]
    [org.jclouds.compute
     ComputeService ComputeServiceContext]
    [org.jclouds.compute.domain
     Template TemplateBuilder ComputeMetadata NodeMetadata Hardware
     OsFamily Image]
    [org.jclouds.compute.options TemplateOptions RunScriptOptions
     RunScriptOptions$Builder]
    [org.jclouds.compute.predicates
     NodePredicates]
    [com.google.common.collect ImmutableSet])
  )

(defn compute-service
  "Create a logged in context."
  ([#^String provider #^String provider-identity #^String provider-credential
    & options]
    (let [module-keys (set (keys module-lookup))
          ext-modules (filter #(module-keys %) options)
          opts (apply hash-map (filter #(not (module-keys %)) options))]
      (.. (ContextBuilder/newBuilder provider)
          (credentials provider-identity provider-credential)
          (modules (apply modules (concat ext-modules (opts :extensions))))
          (overrides (reduce #(do (.put %1 (name (first %2)) (second %2)) %1)
            (Properties.) (dissoc opts :extensions)))
          (buildView ComputeServiceContext)
          (getComputeService))))
  ([#^ComputeServiceContext compute-context]
    (.getComputeService compute-context)))

(defn compute-context
  "Returns a compute context from a compute service."
  [compute]
  (.getContext compute))

(defn compute-service?
  [object]
  (instance? ComputeService object))

(defn compute-context?
  [object]
  (instance? ComputeServiceContext object))

(defn locations
  "Retrieve the available compute locations for the compute context."
  ([#^ComputeService compute]
    (seq (.listAssignableLocations compute))))

(defn nodes
  "Retrieve the existing nodes for the compute context."
  ([#^ComputeService compute]
    (seq (.listNodes compute))))

(defn nodes-with-details
  "Retrieve the existing nodes for the compute context."
  ([#^ComputeService compute]
    (seq (.listNodesDetailsMatching compute (NodePredicates/all)))))

(defn nodes-with-details-matching
  "List details for all nodes matching fn pred.
  pred should be a fn of one argument that takes a ComputeMetadata and returns true or false.
  "
  ([#^ComputeService compute pred]
    (seq (.listNodesDetailsMatching compute (to-predicate pred)))))

(defn nodes-in-group
  "list details of all the nodes in the given group."
  ([#^ComputeService compute #^String group]
    (filter #(= (.getGroup %) group) (nodes-with-details compute))))

(defn images
  "Retrieve the available images for the compute context."
  ([#^ComputeService compute]
    (seq (.listImages compute))))

(defn hardware-profiles
  "Retrieve the available node hardware profiles for the compute context."
  ([#^ComputeService compute]
    (seq (.listHardwareProfiles compute))))

(defn default-template
  ([#^ComputeService compute]
    (.. compute (templateBuilder)
      (options
        (org.jclouds.compute.options.TemplateOptions$Builder/authorizePublicKey
          (slurp (str (. System getProperty "user.home") "/.ssh/id_rsa.pub"))))
      build)))

(defn create-nodes
  "Create the specified number of nodes using the default or specified
   template.
  ;; Simplest way to add 2 small linux nodes to the group webserver is to run
  (create-nodes \"webserver\" 2 compute)
  ;; Note that this will actually add another 2 nodes to the set called
  ;; \"webserver\""
  ([group count compute]
    (create-nodes
      group count (default-template compute) compute))
  ([#^ComputeService compute group count template]
    (seq
      (.createNodesInGroup compute group count template))))

(defn create-node
  "Create a node using the default or specified template.

  ;; simplest way to add a small linux node to the group webserver is to run
  (create-node \"webserver\" compute)

  ;; Note that this will actually add another node to the set called
  ;;  \"webserver\""
  ([compute group]
    (create-node compute group (default-template compute)))
  ([compute group template]
    (first (create-nodes compute group 1 template))))

(defn #^NodeMetadata node-details
  "Retrieve the node metadata, given its id."
  ([#^ComputeService compute id]
    (.getNodeMetadata compute id)))

(defn suspend-nodes-matching
  "Suspend all nodes matching the fn pred.
  pred should be a fn of one argument that takes a ComputeMetadata and returns true or false."
  ([#^ComputeService compute pred]
    (.suspendNodesMatching compute (to-predicate pred))))

(defn suspend-node
  "Suspend a node, given its id."
  ([#^ComputeService compute id]
    (.suspendNode compute id)))

(defn resume-nodes-matching
  "Suspend all the nodes in the fn pred.
  pred should be a fn of one argument that takes a ComputeMetadata and returns true or false."
  ([#^ComputeService compute pred]
    (.resumeNodesMatching compute (to-predicate pred))))

(defn resume-node
  "Resume a node, given its id."
  ([#^ComputeService compute id]
    (.resumeNode compute id)))

(defn reboot-nodes-matching
  "Reboot all the nodes in the fn pred.
  pred should be a fn of one argument that takes a ComputeMetadata and returns true or false."
  ([#^ComputeService compute pred]
    (.rebootNodesMatching compute (to-predicate pred))))

(defn reboot-node
  "Reboot a node, given its id."
  ([#^ComputeService compute id]
    (.rebootNode compute id)))

(defn destroy-nodes-matching
  "Destroy all the nodes in the fn pred.
  pred should be a fn of one argument that takes a ComputeMetadata and returns true or false.
 
  ;; destroy all nodes
  (destroy-nodes-matching compute (constantly true))
  "
  ([#^ComputeService compute pred]
    (.destroyNodesMatching compute (to-predicate pred))))

(defn destroy-node
  "Destroy a node, given its id."
  ([#^ComputeService compute id]
    (.destroyNode compute id)))

(defn run-script-on-node
  "Run a script on a node"
  ([#^ComputeService compute id command #^RunScriptOptions options]
    (.runScriptOnNode compute id command options)))

(defn run-script-on-nodes-matching
  "Run a script on the nodes matching the given predicate"
  ([#^ComputeService compute pred command #^RunScriptOptions options]
    (.runScriptOnNodesMatching compute (to-predicate pred) command options)))

(defmacro status-predicate [node status]
  `(= (.getStatus ~node)
    (. org.jclouds.compute.domain.NodeMetadata$Status ~status)))

(defn pending?
  "Predicate for the node being in transition"
  [#^NodeMetadata node]
  (status-predicate node PENDING))

(defn running?
  "Predicate for the node being available for requests."
  [#^NodeMetadata node]
  (status-predicate node RUNNING))

(defn terminated?
  "Predicate for the node being halted."
  [#^NodeMetadata node]
  (or
    (= node nil)
    (status-predicate node TERMINATED)))

(defn suspended?
  "Predicate for the node being suspended."
  [#^NodeMetadata node]
  (status-predicate node SUSPENDED))

(defn error-status?
  "Predicate for the node being in an error status."
  [#^NodeMetadata node]
  (status-predicate node ERROR))

(defn unrecognized-status?
  "Predicate for the node being in an unrecognized status."
  [#^NodeMetadata node]
  (status-predicate node UNRECOGNIZED))

(defn in-group?
  "Returns a predicate fn which returns true if the node is in the given group, false otherwise"
  [group]
  #(= (.getGroup %) group))

(defn public-ips
  "Returns the node's public ips"
  [#^NodeMetadata node]
  (.getPublicAddresses node))

(defn private-ips
  "Returns the node's private ips"
  [#^NodeMetadata node]
  (.getPrivateAddresses node))

(defn group
  "Returns a the node's group"
  [#^NodeMetadata node]
  (.getGroup node))

(defn hostname
  "Returns the compute node's name"
  [#^ComputeMetadata node]
  (.getName node))

(defn location
  "Returns the compute node's location id"
  [#^ComputeMetadata node]
  (-?> node .getLocation .getId))

(defn id
  "Returns the compute node's id"
  [#^ComputeMetadata node]
  (.getId node))

(define-accessors Template image hardware location options)
(define-accessors Image version os-family os-description architecture)
(define-accessors Hardware processors ram volumes)
(define-accessors NodeMetadata "node" credentials hardware status group)

(def
  ^{:doc "TemplateBuilder functions" :private true}
  template-map
  (merge
    (make-option-map
      kw-memfn-0arg [:smallest :fastest :biggest :any])
    (make-option-map
      kw-memfn-1arg
      [:from-hardware :from-image :from-template
       :os-family :location-id :image-id :hardware-id :hypervisor-matches 
       :os-name-matches :os-description-matches :os-version-matches
       :os-arch-matches :os-64-bit :image-name-matches
       :image-version-matches :image-description-matches :image-matches
       :min-cores :min-ram :min-disk])))

(def
  ^{:doc "TemplateOptions functions" :private true}
  options-map
  (merge
    (make-option-map
      kw-memfn-0arg
      [;; ec2 trmk-ecloud trmk-vcloudexpress
         :no-key-pair
       ;; aws-ec2
         :enable-monitoring :no-placement-group])
    (make-option-map
      kw-memfn-1arg
      [;; RunScriptOptions
         :override-login-credentials
         :override-login-user
         :override-login-password :override-login-private-key
         :override-authenticate-sudo
         
         :name-task :run-as-root :wrap-in-init-script :block-on-complete
         :block-on-port
       ;; TemplateOptions
         :run-script :install-private-key :authorize-public-key :tags
       ;; cloudstack
         :security-group-id :network-id :network-ids :setup-static-nat
         :ip-on-default-network :ips-to-networks
       ;; ec2
         :security-groups :user-data :block-device-mappings
         :unmap-device-named
       ;; cloudstack ec2
         :key-pair
       ;; aws-ec2
         :placement-group :subnet-id :spot-price :spot-options
         :iam-instance-profile-name :iam-instance-profile-arn
       ;; cloudstack aws-ec2
         :security-group-ids
       ;; softlayer
         :domain-name
       ;; trmk-ecloud trmk-vcloudexpress
         :ssh-key-fingerprint
       ;; vcloud
         :description :customization-script :ip-address-allocation-mode])
    (make-option-map
      kw-memfn-varargs
      [;; from TemplateOptions
         :inbound-ports])
    (make-option-map
      kw-memfn-2arg
      [;; from TemplateOptions
         :block-on-port
       ;; ec2 options
         :map-ephemeral-device-to-device-name])
    {:map-ebs-snapshot-to-device-name
     (kw-memfn-apply :map-ebs-snapshot-to-device-name
       device-name snapshot-id size-in-gib delete-on-termination)
     :map-new-volume-to-device-name
     (kw-memfn-apply :map-new-volume-to-device-name
       device-name size-in-gib delete-on-termination)}))

(def
  ^{:doc "All receognised options"}
  known-template-options
  (set (mapcat keys [options-map template-map])))

(defn os-families []
  (. OsFamily values))

(def enum-map {:os-family (os-families)})

(defn translate-enum-value [kword value]
  (or (-> (filter #(= (name value) (str %)) (kword enum-map)) first)
    value))

(defn apply-option [builder option-map option value]
  (when-let [f (option-map option)]
    (f builder (translate-enum-value option value))))

(defn build-template
  "Creates a template that can be used to run nodes.

The :os-family key expects a keyword version of OsFamily,
  eg. :os-family :ubuntu.

The :smallest, :fastest, :biggest, and :any keys expect a
boolean value.

Options correspond to TemplateBuilder methods."
  [#^ComputeService compute
   {:keys [from-hardware from-image from-template
       os-family location-id image-id hardware-id
       os-name-matches os-description-matches os-version-matches
       os-arch-matches os-64-bit mage-name-matches
       image-version-matches image-description-matches image-matches
       min-cores min-ram min-disk smallest fastest biggest any]
    :as options}]
  (let [builder (.. compute (templateBuilder))]
    (doseq [[option value] options]
      (when-not (known-template-options option)
        (throw (Exception. (format "Invalid template builder option : %s" option))))
      ;; apply template builder options
      (try
        (apply-option builder template-map option value)
        (catch Exception e
          (throw (Exception. 
            (format
              "Problem applying template builder %s with value %s: %s"
              option (pr-str value) (.getMessage e))
            e)))))
    (let [template (.build builder)
          template-options (.getOptions template)]
      (doseq [[option value] options]
        ;; apply template option options
        (try
          (apply-option template-options options-map option value)
          (catch Exception e
            (throw (Exception. 
              (format
                "Problem applying template option %s with value %s: %s"
                option (pr-str value) (.getMessage e))
              e)))))
      template)))
