(ns org.jclouds.core
  "Core functionality used across blobstore and compute."
  (:use clojure.contrib.logging
        [clojure.contrib.str-utils2 :only [capitalize lower-case map-str]]
        [clojure.contrib.java-utils :only [wall-hack-field]])
  (:import java.io.File
           (com.google.common.collect ImmutableSet)))

(def module-lookup
     {:log4j 'org.jclouds.logging.log4j.config.Log4JLoggingModule
      :ssh 'org.jclouds.ssh.jsch.config.JschSshClientModule
      :enterprise 'org.jclouds.enterprise.config.EnterpriseConfigurationModule
      :httpnio 'org.jclouds.http.httpnio.config.NioTransformingHttpCommandExecutorServiceModule
      :apachehc 'org.jclouds.http.apachehc.config.ApacheHCHttpCommandExecutorServiceModule
      :bouncycastle 'org.jclouds.encryption.bouncycastle.config.BouncyCastleEncryptionServiceModule
      :joda 'org.jclouds.date.joda.config.JodaDateServiceModule
      :gae 'org.jclouds.gae.config.GoogleAppEngineConfigurationModule})

(defn- instantiate [sym]
  (let [loader (.getContextClassLoader (Thread/currentThread))]
    (try
     (.newInstance #^Class (.loadClass loader (name sym)))
     (catch java.lang.ClassNotFoundException e
       (warn (str "Could not find " (name sym) " module.
Ensure the module is on the classpath.  You are maybe missing a dependency on
  org.jclouds/jclouds-jsch
  org.jclouds/jclouds-log4j
  or org.jclouds/jclouds-enterprise."))))))

(defn modules
  "Build a list of modules suitable for passing to compute or blobstore context"
  [& modules]
  (.build #^com.google.common.collect.ImmutableSet$Builder
          (reduce #(.add #^com.google.common.collect.ImmutableSet$Builder %1 %2)
                  (com.google.common.collect.ImmutableSet/builder)
                  (filter (complement nil?)
                          (map (comp instantiate module-lookup) modules)))))

(defn seq-from-immutable-set [#^ImmutableSet set]
  (map #(.getValue %) set))

(defn dashed [a]
  (apply str (interpose "-" (map lower-case (re-seq #"[A-Z][^A-Z]*" a)))))

(defn camelize [a]
  (map-str capitalize (.split a "-")))

(defn camelize-mixed [a]
  (let [c (.split a "-")]
    (apply str (lower-case (first c)) (map capitalize (rest c)))))

(defmacro option-fn-0arg [key]
  `(fn [builder#]
     (~(symbol (str "." (camelize-mixed (name key)))) builder#)))

(defmacro option-fn-1arg [key]
  `(fn [builder# value#]
     (~(symbol (str "." (camelize-mixed (name key)))) builder# value#)))

(defmacro make-option-map [f keywords]
  `[ ~@(reduce (fn [v# k#] (conj (conj v# k#) `(~f ~k#))) [] keywords)])

(defmacro define-accessor
  [class property obj-name]
  (list 'defn (symbol (str obj-name "-" (name property)))
        (vector  (with-meta (symbol obj-name) {:tag (.getName class)}))
        (list (symbol (str ".get" (camelize (name property)))) (symbol obj-name))))

(defmacro define-accessors
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
