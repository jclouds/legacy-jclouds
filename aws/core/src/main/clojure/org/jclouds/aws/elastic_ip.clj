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
  #^{:author "Chas Emerick, cemerick@snowtide.com"
     :doc "A clojure binding for the jclouds AWS elastic IP address interface."}
  org.jclouds.aws.elastic-ip
  (:require (org.jclouds [compute :as compute])
    [org.jclouds.aws.ebs :as ebs])
  (:use (clojure.contrib def core))
  (:import org.jclouds.compute.domain.NodeMetadata
    (org.jclouds.aws.ec2.domain PublicIpInstanceIdPair)))

(defn #^org.jclouds.aws.ec2.services.ElasticIPAddressClient
  eip-service
  "Returns the synchronous ElasticIPAddressClient associated with
   the specified compute service, or compute/*compute* as bound by with-compute-service."
  [& [compute]]
  (-> (or compute compute/*compute*)
    .getContext .getProviderSpecificContext .getApi .getElasticIPAddressServices))

(defn allocate
  "Claims a new elastic IP address within the (optionally) specified region for your account.
   Region may be a string, keyword, or a node from which the region
   is inferred.  Returns the IP address as a string."
  ([] (allocate nil))
  ([region]
    (.allocateAddressInRegion (eip-service) (ebs/get-region region))))

(defn associate
  "Associates an elastic IP address with a node."
  ([#^NodeMetadata node public-ip]
    (associate node public-ip (.getProviderId node)))
  ([region public-ip instance-id]
    (.associateAddressInRegion (eip-service)
      (ebs/get-region region)
      public-ip
      instance-id)))

(defn addresses
  "Returns a map of elastic IP addresses to maps with slots:

   :region - the region (string/keyword/NodeMetadata) the IP address is allocated within
   :node-id - the ID of the instance with which the IP address is associated (optional)

   You may optionally specify which IP addresses you would like to query."
  ([] (addresses nil))
  ([region & public-ips]
    (into {} (for [#^PublicIpInstanceIdPair pair (.describeAddressesInRegion (eip-service)
                                                   (ebs/get-region region)
                                                   (into-array String public-ips))]
               [(.getPublicIp pair) (merge {:region (.getRegion pair)}
                                      (when (.getInstanceId pair) {:node-id (.getInstanceId pair)}))]))))

(defn dissociate
  "Dissociates an elastic IP address from the node with which it is currently associated."
  [region public-ip]
  (.disassociateAddressInRegion (eip-service)
    (ebs/get-region region)
    public-ip))

(defn release
  "Disclaims an elastic IP address from your account."
  ([public-ip] (release nil public-ip))
  ([region public-ip]
    (.releaseAddressInRegion (eip-service)
      (ebs/get-region region)
      public-ip)))
