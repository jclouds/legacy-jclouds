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
  #^{:author "Chas Emerick, cemerick@snowtide.com"
     :doc "A clojure binding for the jclouds AWS elastic IP address interface."}
  org.jclouds.ec2.elastic-ip2
  (:require (org.jclouds [compute2 :as compute])
    [org.jclouds.ec2.ebs2 :as ebs])
  (:import org.jclouds.compute.domain.NodeMetadata
    (org.jclouds.ec2.domain PublicIpInstanceIdPair)))

(defn ^org.jclouds.ec2.features.ElasticIPAddressApi
  eip-service
  "Returns an ElasticIPAddressApi for the given ComputeService"
  [compute]
  (-> compute
    .getContext .getProviderSpecificContext .getApi .getElasticIPAddressApi().get))

(defn allocate
  "Claims a new elastic IP address within the (optionally) specified region for your account.
   Region may be a string, keyword, or a node from which the region
   is inferred.  Returns the IP address as a string."
  ([compute] (allocate compute nil))
  ([compute region]
    (.allocateAddressInRegion (eip-service compute) (ebs/get-region region))))

(defn associate
  "Associates an elastic IP address with a node."
  ([compute ^NodeMetadata node public-ip]
    (associate node public-ip (.getProviderId node)))
  ([compute region public-ip instance-id]
    (.associateAddressInRegion (eip-service compute)
      (ebs/get-region region)
      public-ip
      instance-id)))

(defn addresses
  "Returns a map of elastic IP addresses to maps with slots:

   :region - the region (string/keyword/NodeMetadata) the IP address is allocated within
   :node-id - the ID of the instance with which the IP address is associated (optional)

   You may optionally specify which IP addresses you would like to query."
  ([compute] (addresses compute nil))
  ([compute region & public-ips]
    (into {} (for [^PublicIpInstanceIdPair pair (.describeAddressesInRegion (eip-service compute)
                                                   (ebs/get-region region)
                                                   (into-array String public-ips))]
               [(.getPublicIp pair) (merge {:region (.getRegion pair)}
                                      (when (.getInstanceId pair) {:node-id (.getInstanceId pair)}))]))))

(defn dissociate
  "Dissociates an elastic IP address from the node with which it is currently associated."
  [compute region public-ip]
  (.disassociateAddressInRegion (eip-service compute)
    (ebs/get-region region)
    public-ip))

(defn release
  "Disclaims an elastic IP address from your account."
  ([compute public-ip] (release compute public-ip nil))
  ([compute public-ip region]
    (.releaseAddressInRegion (eip-service compute)
      (ebs/get-region region)
      public-ip)))
