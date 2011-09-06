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

(ns org.jclouds.aws.ec2
  "AWS EC2 specific functionality"
  (:require
   [org.jclouds.core :as core])
  (:import
   org.jclouds.aws.ec2.domain.SpotInstanceRequest
   org.jclouds.aws.ec2.options.RequestSpotInstancesOptions))

(def
  ^{:doc "TemplateBuilder functions" :private true}
  spot-option-map
  (core/make-option-map
   core/kw-memfn-1arg
   [:valid-from :valid-until :type :launch-group :availability-zone-group]))

(defn spot-types []
  (. org.jclouds.aws.ec2.domain.SpotInstanceRequest$Type values))

(def enum-map {:type (spot-types)})

(defn translate-enum-value [kword value]
  (or (-> (filter #(= (name value) (str %)) (kword enum-map)) first)
      value))

(defn apply-option
  [options [option value]]
  (when-let [f (spot-option-map option)]
    (f options (translate-enum-value option value)))
  options)

(defn spot-options
  "Build a spot request options object, for passing to the :spot-options
   key of the template builder options.

   Takes a hash-map of keys and values that correspond to the methods of
   RequestSpotInstancesOptions.

   Options are:
       :valid-from :valid-until :type :launch-group :availability-zone-group

   :type takes either :one-time or :persistent"
  [request-map]
  (reduce
   apply-option
   (RequestSpotInstancesOptions.) request-map))
