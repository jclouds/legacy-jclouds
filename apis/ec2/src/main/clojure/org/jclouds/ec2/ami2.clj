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

(ns
  #^{:author "Hunter Hutchinson, hunter.hutchinson@gmail.com"
     :doc "A clojure binding to the jclouds AMI service interface."}
  org.jclouds.ec2.ami2
  (:use org.jclouds.compute2)
  (:import org.jclouds.aws.domain.Region
    org.jclouds.ec2.features.AMIApi
    org.jclouds.ec2.options.CreateImageOptions
    org.jclouds.compute.domain.NodeMetadata
    (org.jclouds.ec2.domain Volume Volume$Status Snapshot Snapshot$Status AvailabilityZoneInfo)))

(defn ^org.jclouds.ec2.features.AMIApi
  ami-service
  ""
  [compute]
  (-> compute
    .getContext
    .getProviderSpecificContext
    .getApi
    .getAMIApi().get))

(defn get-region
  "Coerces the first parameter into a Region string; strings, keywords, and
   NodeMetadata instances are acceptable arguments. An optional second argument
   is returned if the first cannot be coerced into a region string.
   Returns nil otherwise."
  ([v] (get-region v nil))
  ([v default-region]
    (cond
      (string? v) v
      (keyword? v) (name v)
      (instance? NodeMetadata v) (let [zone (location v)]
      ; no easier way to go from zone -> region?
      (if (> (.indexOf zone "-") -1)
        (subs zone 0 (-> zone count dec))
        zone))
      :else default-region)))

(defn- as-string
  [v]
  (cond
    (string? v) v
    (keyword? v) (name v)
    :else v))

(defn- get-string
  [map key]
  (as-string (get map key)))

(defn- as-int
  [v]
  (cond
    (number? v) (int v)
    (string? v) (Integer/parseInt v)
    :else (throw (IllegalArgumentException.
    (str "Don't know how to convert object of type " (class v) " to a string")))))

(defn create-image-in-region
  ([compute region name node-id description]
     (.createImageInRegion (ami-service compute)
                              (get-region region)
                              (as-string name)
                              (as-string node-id)
                              (into-array CreateImageOptions
                                          (when description
                                            [(.withDescription (CreateImageOptions.) description)])))))

