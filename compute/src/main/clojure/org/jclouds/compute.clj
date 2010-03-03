(ns
    #^{:doc "
a lib for interacting with jclouds ComputeService.

Current supported services are:
   [ec2, rimuhosting, terremark, vcloud, hostingdotcom]

Here's an example of getting some compute configuration from rackspace:

  (ns example.jclouds
    (:use org.jclouds.compute
          clojure.contrib.pprint))

  (def user \"username\")
  (def password \"password\")
  (def compute-name \"cloudservers\")

  (def compute (compute-context compute-name user password))

  (pprint (locations compute))
  (pprint (images compute))
  (pprint (nodes compute))
  (pprint (sizes compute))

"}
  org.jclouds.compute
  (:use clojure.contrib.duck-streams
        [clojure.contrib.str-utils2 :only [capitalize lower-case map-str]]
        [clojure.contrib.java-utils :only [wall-hack-field]])
  (:import java.io.File
           org.jclouds.domain.Location
           org.jclouds.compute.options.TemplateOptions
           (org.jclouds.compute ComputeService
                                ComputeServiceContext
                                ComputeServiceContextFactory)
           (org.jclouds.compute.domain Template TemplateBuilder ComputeMetadata
                                       NodeMetadata Size OsFamily Image
                                       Architecture)
           (com.google.common.collect ImmutableSet)))

(def module-lookup
     {:log4j org.jclouds.logging.log4j.config.Log4JLoggingModule
      :ssh org.jclouds.ssh.jsch.config.JschSshClientModule
      :enterprise org.jclouds.enterprise.config.EnterpriseConfigurationModule})

(defn modules
  "Build a list of modules suitable for passing to compute-context"
  [& modules]
  (.build #^com.google.common.collect.ImmutableSet$Builder
          (reduce #(.add #^com.google.common.collect.ImmutableSet$Builder %1
                         (.newInstance #^Class (%2 module-lookup)))
                  (com.google.common.collect.ImmutableSet/builder)
                  modules)))

(defn compute-context
  "Create a logged in context."
  ([s a k]
     (compute-context s a k (modules :log4j :ssh :enterprise)))
  ([#^String s #^String a #^String k #^ImmutableSet m]
     (.createContext (new ComputeServiceContextFactory) s a k m)))

(defn- seq-from-immutable-set [#^ImmutableSet set]
  (map #(.getValue %) set))

(defn locations
  "Retrieve the available compute locations for the compute context."
  [#^ComputeServiceContext compute]
  (seq-from-immutable-set (.getLocations (.getComputeService compute))))

(defn nodes
  "Retrieve the existing nodes for the compute context."
  ([#^ComputeServiceContext compute]
     (seq-from-immutable-set (.getNodes (.getComputeService compute))))
  ([#^ComputeServiceContext compute #^String tag]
     (seq-from-immutable-set (.getNodesWithTag (.getComputeService compute) tag))))

(defn images
  "Retrieve the available images for the compute context."
  [#^ComputeServiceContext compute]
  (seq-from-immutable-set (.getImages (.getComputeService compute))))

(defn sizes
  "Retrieve the available node sizes for the compute context."
  [#^ComputeServiceContext compute]
  (seq-from-immutable-set (.getSizes (.getComputeService compute))))

(defn default-template [#^ComputeServiceContext compute]
  (.. compute (getComputeService) (templateBuilder)
      (osFamily OsFamily/UBUNTU)
      smallest
      (options
       (org.jclouds.compute.options.TemplateOptions$Builder/authorizePublicKey
        (slurp (str (. System getProperty "user.home") "/.ssh/id_rsa.pub"))))
      build))

(defn run-nodes
  "Create the specified number of nodes using the default or specified
   template."
  ([compute tag count]
     (run-nodes compute tag count (default-template compute)))
  ([#^ComputeServiceContext compute tag count template]
     (seq-from-immutable-set
      (.runNodesWithTag
       (.getComputeService compute) tag count template))))

(defn run-node
  "Create a node using the default or specified template."
  ([compute tag]
    (run-nodes compute tag 1 (default-template compute)))
  ([compute tag template]
     (run-nodes compute tag 1 template)))

(defn #^NodeMetadata node-details
  "Retrieve the node metadata."
  [#^ComputeServiceContext compute node]
  (.getNodeMetadata (.getComputeService compute) node ))

(defn reboot-nodes
  "Reboot all the nodes with the given tag."
  ([#^ComputeServiceContext compute #^String tag]
    (.rebootNodesWithTag (.getComputeService compute) tag )))

(defn reboot-node
  "Reboot a given node."
  ([#^ComputeServiceContext compute
    #^ComputeMetadata node]
    (.rebootNode (.getComputeService compute) node )))

(defn destroy-nodes
  "Destroy all the nodes with the given tag."
  ([#^ComputeServiceContext compute #^String tag]
    (.destroyNodesWithTag (.getComputeService compute) tag )))

(defn destroy-node
  "Destroy a given node."
  ([#^ComputeServiceContext compute
    #^ComputeMetadata node]
    (.destroyNode (.getComputeService compute) node )))

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

(defn unknown-state?
  "Predicate for the node being in an unknown state."
  [#^NodeMetadata node]
  (state-predicate node UNKNOWN))

(defn public-ips
  "Returns the node's public ips"
  [#^NodeMetadata node]
  (map #(.getHostAddress %) (.getPublicAddresses node)))

(defn private-ips
  "Returns the node's private ips"
  [#^NodeMetadata node]
  (map #(.getHostAddress %) (.getPrivateAddresses node)))

(defn tag
  "Returns a the node's tag"
  [#^NodeMetadata node]
  (.getTag node))

(defn hostname
  "Returns the compute node's name"
  [#^ComputeMetadata node]
  (.getName node))

(defn- dashed [a]
  (apply str (interpose "-" (map lower-case (re-seq #"[A-Z][^A-Z]*" a)))))

(defn- camelize [a]
  (apply str (map-str capitalize (.split a "-"))))

(defn camelize-mixed [a]
  (let [c (.split a "-")]
    (apply str (lower-case (first c)) (map capitalize (rest c)))))

(defmacro #^{:private true} define-accessor
  [class property obj-name]
  (list 'defn (symbol (str obj-name "-" (name property)))
        (vector  (with-meta (symbol obj-name) {:tag (.getName class)}))
        (list (symbol (str ".get" (camelize (name property)))) (symbol obj-name))))

(defmacro #^{:private true} define-accessors
  "Defines read accessors, modelled on class-name-property-name.  If the second
  argument is a string, it is used instead of the class-name prefix."
  [class & properties]
  (let [obj-name (if (string? (first properties))
                   (first properties)
                   (dashed (.getName class)))
        properties (if (string? (first properties))
                     (rest properties)
                     properties)]
    `(do
       ~@(for [property properties]
           `(define-accessor ~class ~property ~obj-name)))))

(define-accessors Template image size location options)
(define-accessors Image version os-family os-description architecture)
(define-accessors Size cores ram disk)
(define-accessors NodeMetadata "node" credentials extra state tag)

(defmacro option-fn-0arg [key]
  `(fn [builder#]
     (~(symbol (str "." (camelize-mixed (name key)))) builder#)))

(defmacro option-fn-1arg [key]
  `(fn [builder# value#]
     (~(symbol (str "." (camelize-mixed (name key)))) builder# value#)))

(defn builder-options [builder]
  (or (wall-hack-field org.jclouds.compute.internal.TemplateBuilderImpl :options builder)
      (TemplateOptions.)))

(defmacro option-option-fn-0arg [key]
  `(fn [builder#]
     (let [options# (builder-options builder#)]
       (~(symbol (str "." (camelize-mixed (name key)))) options#)
       (.options builder# options#))))

(defn- seq-to-array [args]
  (if (or (seq? args) (vector? args))
    (int-array args)
    args))

(defmacro option-option-fn-1arg [key]
  `(fn [builder# value#]
     (let [options# (builder-options builder#)]
       (~(symbol (str "." (camelize-mixed (name key)))) options# (seq-to-array value#))
       (.options builder# options#))))

(defmacro make-option-map [f keywords]
  `[ ~@(reduce (fn [v# k#] (conj (conj v# k#) `(~f ~k#))) [] keywords)])

(def option-1arg-map
     (apply array-map
            (concat
             (make-option-map option-fn-1arg
                              [:os-family :location-id :architecture :image-id :size-id
                               :os-description-matches :image-version-matches
                               :image-description-matches :min-cores :min-ram])
             (make-option-map option-option-fn-1arg
                              [:run-script :install-private-key :authorize-public-key :inbound-ports]))))
(def option-0arg-map
     (apply hash-map
            (concat
             (make-option-map option-fn-0arg
                              [:smallest :fastest :biggest])
             (make-option-map option-option-fn-0arg
                              [:destroy-on-error]))))

(defn os-families []
  (. OsFamily values))
(defn architectures []
  (. Architecture values))

(def enum-map {:os-family (os-families)
               :architecture (architectures)})

(defn add-option-with-value-if [builder kword]
  (loop [enums (sequence enum-map)]
    (if (not (empty? enums))
      (let [enum (first enums)
            value (filter #(= (name kword) (str %)) (second enum))]
        (if (not (empty? value))
          (((first enum) option-1arg-map) builder (first value))
          (recur (rest enums)))))))

(defn add-option-if [builder kword]
  (let [f (option-0arg-map kword)]
    (if f (f builder))))

(defn add-keyword-option [builder option]
  (if (not (or (add-option-with-value-if builder option)
               (add-option-if builder option)))
    (println "Unknown option " option)))

(defn add-value-option [builder option value]
  (let [f (option-1arg-map option)]
    (if f
      (f builder value)
      (println "Unknown option" option))))

(defn build-template [#^ComputeServiceContext compute option & options]
  (let [builder (.. compute (getComputeService) (templateBuilder))]
    (loop [option option
           remaining options]
      (if (empty? remaining)
        (add-keyword-option builder option)
        (let [next-is-keyword (keyword? (first remaining))
              arg (if (not next-is-keyword)
                    (first remaining))
              next (if next-is-keyword
                     (first remaining)
                     (fnext remaining))
              remaining (if (keyword? (first remaining))
                          (rest remaining)
                          (drop 2 remaining))]
          (if arg
            (add-value-option builder option arg)
            (add-keyword-option builder option))
          (if next
            (recur next remaining)))))
    (.build builder)))

