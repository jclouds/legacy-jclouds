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

(ns org.jclouds.core
  "Core functionality used across blobstore and compute."
  (:use clojure.tools.logging)
  (:import java.io.File
           (com.google.common.collect ImmutableSet))
  (:require [clojure.string :as string]))

(def ^{:dynamic :true} module-lookup
     {:log4j 'org.jclouds.logging.log4j.config.Log4JLoggingModule
      :slf4j 'org.jclouds.logging.slf4j.config.SLF4JLoggingModule
      :lognull 'org.jclouds.logging.config.NullLoggingModule
      :ssh 'org.jclouds.ssh.jsch.config.JschSshClientModule
      :jsch 'org.jclouds.ssh.jsch.config.JschSshClientModule
      :sshj 'org.jclouds.sshj.config.SshjSshClientModule
      :enterprise 'org.jclouds.enterprise.config.EnterpriseConfigurationModule
      :apachehc 'org.jclouds.http.apachehc.config.ApacheHCHttpCommandExecutorServiceModule
      :ning 'org.jclouds.http.ning.config.NingHttpCommandExecutorServiceModule
      :bouncycastle 'org.jclouds.encryption.bouncycastle.config.BouncyCastleCryptoModule
      :joda 'org.jclouds.date.joda.config.JodaDateServiceModule
      :gae 'org.jclouds.gae.config.GoogleAppEngineConfigurationModule
      :gae-async 'org.jclouds.gae.config.AsyncGoogleAppEngineConfigurationModule})

(defn- instantiate [sym]
  (let [loader (.getContextClassLoader (Thread/currentThread))]
    (try
     (.newInstance #^Class (.loadClass loader (name sym)))
     (catch java.lang.ClassNotFoundException e
       (warn (str "Could not find " (name sym) " module.
Ensure the module is on the classpath.  You are maybe missing a dependency on
  org.jclouds/jclouds-jsch
  org.jclouds/jclouds-log4j
  org.jclouds/jclouds-ning
  or org.jclouds/jclouds-enterprise."))))))

(defn modules
  "Build a list of modules suitable for passing to compute or blobstore context"
  [& modules]
  (.build #^com.google.common.collect.ImmutableSet$Builder
          (reduce #(.add #^com.google.common.collect.ImmutableSet$Builder %1 %2)
                  (com.google.common.collect.ImmutableSet/builder)
                  (filter (complement nil?)
                          (map #(cond
                                 (keyword? %) (-> % module-lookup instantiate)
                                 (symbol? %) (instantiate %)
                                 :else %)
                               modules)))))

;;; Functions and macros to map keywords to java member functions
(defn dashed [a]
  (apply
   str (interpose "-" (map string/lower-case (re-seq #"[A-Z][^A-Z]*" a)))))

(defn ^String map-str
  "Apply f to each element of coll, concatenate all results into a
  String."
  [f coll]
  (apply str (map f coll)))

(defn camelize
  "Takes a string, or anything named, and converts it to camel case
   (capitalised initial component"
  [a]
  (map-str string/capitalize (.split (name a) "-")))

(defn camelize-mixed
  "Takes a string, or anything named, and converts it to mixed camel case
   (lower case initial component)"
  [a]
  (let [c (.split (name a) "-")]
    (apply str (string/lower-case (first c)) (map string/capitalize (rest c)))))

(defn kw-fn-symbol
  "Converts a keyword into a camel cased symbol corresponding to a function
   name"
  [kw]
  (symbol (camelize-mixed kw)))

(defmacro memfn-apply
  "Expands into a function that takes one argument,"
  [fn-name & args]
  `(fn [target# [~@args]]
     ((memfn ~fn-name ~@args) target# ~@args)))

(defmacro kw-memfn
  "Expands into code that creates a function that expects to be passed an
   object and any args, and calls the instance method corresponding to
   the camel cased version of the passed keyword, passing the arguments."
  [kw & args]
  `(memfn ~(kw-fn-symbol kw) ~@args))

(defmacro kw-memfn-apply
  "Expands into code that creates a function that expects to be passed an object
   and an arg vector containing the args, and calls the instance method
   corresponding to the camel cased version of the passed keyword, passing the
   arguments."
  [kw & args]
  `(fn [target# [~@args]]
     ((memfn ~(kw-fn-symbol kw) ~@args) target# ~@args)))

(defmacro kw-memfn-0arg
  "Expands into code that creates a function that expects to be passed an
   object, and calls the instance method corresponding to the camel cased
   version of the passed keyword if the argument is non-nil."
  [kw]
  `(fn [target# arg#]
     (if arg#
       ((kw-memfn ~kw) target#)
       target#)))

(defmacro kw-memfn-1arg
  "Expands into code that creates a function that expects to be passed an object
   and an arg, and calls the instance method corresponding to the camel cased
   version of the passed keyword, passing the argument."
  [kw]
  `(kw-memfn ~kw a#))

(defmacro kw-memfn-2arg
  "Expands into code that creates a function that expects to be passed an object
   and an arg vector containing 2 args, and calls the instance method
   corresponding to the camel cased version of the passed keyword, passing the
   arguments."
  [kw]
  `(kw-memfn-apply ~kw a# b#))

;; (defmacro memfn-overloads
;;   "Construct a function that applies arguments to the given member function."
;;   [name]
;;   `(fn [target# args#]
;;     (condp = (count args#)
;;       0 (. target# (~name))
;;       1 (. target# (~name (first args#)))
;;       2 (. target# (~name (first args#) (second args#)))
;;       3 (. target# (~name (first args#) (second args#) (nth args# 2)))
;;       4 (. target#
;;            (~name (first args#) (second args#) (nth args# 2) (nth args# 3)))
;;       5 (. target#
;;            (~name (first args#) (second args#) (nth args# 2) (nth args# 3)
;;                   (nth args# 4)))
;;       (throw
;;        (java.lang.IllegalArgumentException.
;;         (str
;;          "too many arguments passed.  Limit 5, passed " (count args#)))))))

;; (defmacro kw-memfn-overloads
;;   "Expands into code that creates a function that expects to be passed an
;;    object and an arg vector, and calls the instance method corresponding to
;;    the camel cased version of the passed keyword, passing the arguments.
;;    The function accepts different arities at runtime."
;;   [kw]
;;   `(memfn-overloads ~(kw-fn-symbol kw)))

(defmacro memfn-varargs
  "Construct a function that applies an argument sequence to the given member
   function, which accepts varargs. array-fn should accept a sequence and
   return a suitable array for passing as varargs."
  [name array-fn]
  `(fn [target# args#]
     (. target#
        (~name
         (if (or (seq? args#) (vector? args#)) (~array-fn args#) args#)))))

(defmacro kw-memfn-varargs
  "Expands into code that creates a function that expects to be passed an
   object and an arg vector, and calls the instance method corresponding to
   the camel cased version of the passed keyword, passing the arguments.
   The function accepts different arities at runtime."
  ([kw] `(kw-memfn-varargs ~kw int-array))
  ([kw array-fn] `(memfn-varargs ~(kw-fn-symbol kw) ~array-fn)))

(defmacro make-option-map
  "Builds a literal map from keyword, to a call on macro f with the keyword
   as an argument."
  [f keywords]
  `(hash-map
    ~@(reduce (fn [v# k#] (conj (conj v# k#) `(~f ~k#))) [] keywords)))

(defmacro define-accessor
  [class property obj-name]
  (list 'defn (symbol (str obj-name "-" (name property)))
        (vector  (with-meta (symbol obj-name) {:tag (.getName class)}))
        (list
         (symbol (str ".get" (camelize (name property))))
         (symbol obj-name))))

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
