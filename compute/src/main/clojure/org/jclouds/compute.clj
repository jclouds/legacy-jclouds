;
; Licensed to jclouds, Inc. (jclouds) under one or more
; contributor license agreements.  See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership.  jclouds licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License.  You may obtain a copy of the License at
;
;   http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing,
; software distributed under the License is distributed on an
; "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
; KIND, either express or implied.  See the License for the
; specific language governing permissions and limitations
; under the License.
;

(ns org.jclouds.compute
  "A clojure binding to the jclouds ComputeService.

Current supported providers are:
   [aws-ec2, eucualyptus-partnercloud-ec2, elastichosts-lon-b, nova,
    cloudservers-uk, cloudservers-us, byon, cloudsigma-zrh, stub,
    trmk-ecloud, trmk-vcloudexpress, vcloud, bluelock, eucalyptus,
    slicehost, elastichosts-lon-p, elastichosts-sat-p, elastichosts,
    openhosting-east1, serverlove-z1-man, skalicloud-sdg-my, deltacloud]

Here's an example of getting some compute configuration from rackspace:

  (use 'org.jclouds.compute)

  (def provider \"cloudservers\")
  (def provider-identity \"username\")
  (def provider-credential \"password\")

  ;; create a compute service
  (def compute
    (compute-service provider provider-identity provider-credential))

  (with-compute-service [compute]
    (pprint (locations))
    (pprint (images))
    (pprint (nodes))
    (pprint (hardware-profiles)))

Here's an example of creating and running a small linux node in the group
webserver:

  ;; create a compute service using ssh and log4j extensions
  (def compute
    (compute-service
      provider provider-identity provider-credential :sshj :log4j))

  (create-node \"webserver\" compute)

See http://code.google.com/p/jclouds for details."
  (:use org.jclouds.core
    (org.jclouds predicate) [clojure.core.incubator :only (-?>)])
  (:import java.io.File
           java.util.Properties
           [org.jclouds.domain Location]
           [org.jclouds.compute
            ComputeService ComputeServiceContext ComputeServiceContextFactory]
           [org.jclouds.compute.domain
            Template TemplateBuilder ComputeMetadata NodeMetadata Hardware
            OsFamily Image]
           [org.jclouds.compute.options TemplateOptions]
           [org.jclouds.compute.predicates
            NodePredicates]
           [com.google.common.collect ImmutableSet]))

(defmacro deprecate-fwd [old-name new-name] `(defn ~old-name {:deprecated "beta-9"} [& args#] (apply ~new-name args#)))

(defn compute-service
  "Create a logged in context."
  ([#^String provider #^String provider-identity #^String provider-credential
    & options]
     (let [module-keys (set (keys module-lookup))
           ext-modules (filter #(module-keys %) options)
           opts (apply hash-map (filter #(not (module-keys %)) options))]
       (.. (ComputeServiceContextFactory.)
           (createContext
            provider provider-identity provider-credential
            (apply modules (concat ext-modules (opts :extensions)))
            (reduce #(do (.put %1 (name (first %2)) (second %2)) %1)
                    (Properties.) (dissoc opts :extensions)))
           (getComputeService)))))

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

(defn as-compute-service
  "Tries hard to produce a compute service from its input arguments"
  [& args]
  (cond
   (compute-service? (first args)) (first args)
   (compute-context? (first args)) (.getComputeService (first args))
   :else (apply compute-service args)))

(def *compute*)

(defmacro with-compute-service
  "Specify the default compute service"
  [[& compute-or-args] & body]
  `(binding [*compute* (as-compute-service ~@compute-or-args)]
     ~@body))

(defn locations
  "Retrieve the available compute locations for the compute context."
  ([] (locations *compute*))
  ([#^ComputeService compute]
     (seq (.listAssignableLocations compute))))

(defn nodes
  "Retrieve the existing nodes for the compute context."
  ([] (nodes *compute*))
  ([#^ComputeService compute]
    (seq (.listNodes compute))))

(defn nodes-with-details
  "Retrieve the existing nodes for the compute context."
  ([] (nodes-with-details *compute*))
  ([#^ComputeService compute]
    (seq (.listNodesDetailsMatching compute (NodePredicates/all)))))

(defn nodes-in-group
  "list details of all the nodes in the given group."
  ([group] (nodes-in-group group *compute*))
  ([#^String group #^ComputeService compute]
    (filter #(= (.getTag %) group) (nodes-with-details compute))))

(deprecate-fwd nodes-with-tag nodes-in-group)

(defn images
  "Retrieve the available images for the compute context."
  ([] (images *compute*))
  ([#^ComputeService compute]
     (seq (.listImages compute))))

(defn hardware-profiles
  "Retrieve the available node hardware profiles for the compute context."
  ([] (hardware-profiles *compute*))
  ([#^ComputeService compute]
     (seq (.listHardwareProfiles compute))))

(defn default-template
  ([] (default-template *compute*))
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

  ;; which is the same as wrapping the create-nodes command with an implicit
  ;; compute service.
  ;; Note that this will actually add another 2 nodes to the set called
  ;; \"webserver\"

  (with-compute-service [compute]
    (create-nodes \"webserver\" 2 ))

  ;; which is the same as specifying the default template
  (with-compute-service [compute]
    (create-nodes \"webserver\" 2 (default-template)))

  ;; which, on gogrid, is the same as constructing the smallest centos template
  ;; that has no layered software
  (with-compute-service [compute]
    (create-nodes \"webserver\" 2
      (build-template
        service
        {:os-family :centos :smallest true
         :image-name-matches \".*w/ None.*\"})))"
  ([group count]
     (create-nodes group count (default-template *compute*) *compute*))
  ([group count compute-or-template]
     (if (compute-service? compute-or-template)
       (create-nodes
        group count (default-template compute-or-template) compute-or-template)
       (create-nodes group count compute-or-template *compute*)))
  ([group count template #^ComputeService compute]
     (seq
      (.createNodesInGroup compute group count template))))

(deprecate-fwd run-nodes create-nodes)

(defn create-node
  "Create a node using the default or specified template.

  ;; simplest way to add a small linux node to the group webserver is to run
  (create-node \"webserver\" compute)

  ;; which is the same as wrapping the create-node command with an implicit compute
  ;; service.
  ;; Note that this will actually add another node to the set called
  ;;  \"webserver\"
  (with-compute-service [compute]
    (create-node \"webserver\" ))"
  ([group]
     (first (create-nodes group 1 (default-template *compute*) *compute*)))
  ([group compute-or-template]
     (if (compute-service? compute-or-template)
       (first
        (create-nodes
         group 1 (default-template compute-or-template) compute-or-template))
       (first (create-nodes group 1 compute-or-template *compute*))))
  ([group template compute]
     (first (create-nodes group 1 template compute))))

(deprecate-fwd run-node create-node)

(defn #^NodeMetadata node-details
  "Retrieve the node metadata, given its id."
  ([id] (node-details id *compute*))
  ([id #^ComputeService compute]
     (.getNodeMetadata compute id)))

(defn suspend-nodes-in-group
  "Reboot all the nodes in the given group."
  ([group] (suspend-nodes-in-group group *compute*))
  ([#^String group #^ComputeService compute]
    (.suspendNodesMatching compute (NodePredicates/inGroup group))))

(deprecate-fwd suspend-nodes-with-tag suspend-nodes-in-group)

(defn suspend-node
  "Suspend a node, given its id."
  ([id] (suspend-node id *compute*))
  ([id #^ComputeService compute]
     (.suspendNode compute id)))

(defn resume-nodes-in-group
  "Suspend all the nodes in the given group."
  ([group] (resume-nodes-in-group group *compute*))
  ([#^String group #^ComputeService compute]
    (.resumeNodesMatching compute (NodePredicates/inGroup group))))

(deprecate-fwd resume-nodes-with-tag resume-nodes-in-group)

(defn resume-node
  "Resume a node, given its id."
  ([id] (resume-node id *compute*))
  ([id #^ComputeService compute]
     (.resumeNode compute id)))

(defn reboot-nodes-in-group
  "Reboot all the nodes in the given group."
  ([group] (reboot-nodes-in-group group *compute*))
  ([#^String group #^ComputeService compute]
    (.rebootNodesMatching compute (NodePredicates/inGroup group))))

(deprecate-fwd reboot-nodes-with-tag reboot-nodes-in-group)

(defn reboot-node
  "Reboot a node, given its id."
  ([id] (reboot-node id *compute*))
  ([id #^ComputeService compute]
     (.rebootNode compute id)))

(defn destroy-nodes-in-group
  "Destroy all the nodes in the given group."
  ([group] (destroy-nodes-in-group group *compute*))
  ([#^String group #^ComputeService compute]
     (.destroyNodesMatching compute (NodePredicates/inGroup group))))

(deprecate-fwd destroy-nodes-with-tag destroy-nodes-in-group)

(defn destroy-node
  "Destroy a node, given its id."
  ([id] (destroy-node id *compute*))
  ([id #^ComputeService compute]
     (.destroyNode compute id)))

(defmacro state-predicate [node state]
  `(= (.getState ~node)
      (. org.jclouds.compute.domain.NodeState ~state)))

(defn pending?
  "Predicate for the node being in transition"
  [#^NodeMetadata node]
  (state-predicate node PENDING))

(defn running?
  "Predicate for the node being available for requests."
  [#^NodeMetadata node]
  (state-predicate node RUNNING))

(defn terminated?
  "Predicate for the node being halted."
  [#^NodeMetadata node]
  (state-predicate node TERMINATED))

(defn suspended?
  "Predicate for the node being suspended."
  [#^NodeMetadata node]
  (state-predicate node SUSPENDED))

(defn error-state?
  "Predicate for the node being in an error state."
  [#^NodeMetadata node]
  (state-predicate node ERROR))

(defn unrecognized-state?
  "Predicate for the node being in an unrecognized state."
  [#^NodeMetadata node]
  (state-predicate node UNRECOGNIZED))

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

(deprecate-fwd tag group)

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
(define-accessors NodeMetadata "node" credentials hardware state group)

(def
  ^{:doc "TemplateBuilder functions" :private true}
  template-map
  (merge
   (make-option-map
    kw-memfn-0arg [:smallest :fastest :biggest :any])
   (make-option-map
    kw-memfn-1arg
    [:os-family :location-id :architecture :image-id :hardware-id
     :os-name-matches :os-version-matches :os-description-matches
     :os-64-bit :image-version-matches :image-name-matches
     :image-description-matches :min-cores :min-ram])))

(def
  ^{:doc "TemplateOptions functions" :private true}
  options-map
  (merge
   (make-option-map
    kw-memfn-0arg
    [:destroy-on-error :enable-monitoring :no-placement-group :no-key-pair
     :with-details])
   (make-option-map
    kw-memfn-1arg
    [:run-script :install-private-key :authorize-public-key
     :override-credentials-with :override-login-user-with
     :override-login-credential-with
     ;; aws ec2 options
     :spot-price :spot-options :placement-group :subnet-id
     :block-device-mappings :unmapDeviceNamed :security-groups
     :key-pair :user-data])
   (make-option-map kw-memfn-varargs [:inbound-ports])
   (make-option-map
    kw-memfn-2arg
    [:block-on-port
     ;; aws ec2 options
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

;; TODO look at clojure-datalog
(defn build-template
  "Creates a template that can be used to run nodes.

The :os-family key expects a keyword version of OsFamily,
  eg. :os-family :ubuntu.

The :smallest, :fastest, :biggest, :any, and :destroy-on-error keys expect a
boolean value.

Options correspond to TemplateBuilder methods."
  [#^ComputeService compute
   {:keys [os-family location-id architecture image-id hardware-id
           os-name-matches os-version-matches os-description-matches
           os-64-bit image-version-matches image-name-matches
           image-description-matches min-cores min-ram
           run-script install-private-key authorize-public-key
           inbound-ports smallest fastest biggest any destroy-on-error]
    :as options}]
  (let [builder (.. compute (templateBuilder))]
    (doseq [[option value] options]
      (when-not (known-template-options option)
        (throw (Exception. (format "Invalid template builder option : %s" option))))
      ;; apply template builder options
      (try
        (apply-option builder template-map option value)
        (catch Exception e
            (throw (Exception. (format
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
