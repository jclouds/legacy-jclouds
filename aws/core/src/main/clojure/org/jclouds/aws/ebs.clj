;;
;; Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
;;
;; ====================================================================
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;; http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.
;; ====================================================================
;;

(ns
  #^{:author "Chas Emerick, cemerick@snowtide.com"
     :doc "A clojure binding to the jclouds EBS service interface."}
  org.jclouds.aws.ebs
  (:require (org.jclouds [compute :as compute]))
  (:use (clojure.contrib def core))
  (:import org.jclouds.aws.domain.Region
    org.jclouds.aws.ec2.domain.AvailabilityZone
    (org.jclouds.aws.ec2.options DescribeSnapshotsOptions DetachVolumeOptions CreateSnapshotOptions)))

(defn #^org.jclouds.aws.ec2.services.ElasticBlockStoreClient
  ebs-services
  "Returns the synchronous ElasticBlockStoreClient associated with
   the specified compute service, or compute/*compute* as bound by with-compute-service."
  [& [compute]]
  (-> (or compute compute/*compute*)
    .getContext .getProviderSpecificContext .getApi .getElasticBlockStoreServices))

(defn as-region
  "Returns the first argument as the corresponding Region if it is a
   keyword or already a Region instance. An optional second argument
   is returned if the first cannot be coerced into a Region.
   Returns nil otherwise."
  [v & [default-region]]
  (cond
    (keyword? v) (Region/fromValue (name v))
    (instance? Region v) v
    :else default-region))

(defn describe-volumes
  "Returns a set of org.jclouds.aws.ec2.domain.Volume instances corresponding to the
   volumes in the specified region (defaulting to your account's default region).

   e.g. (with-compute-service [compute] (describe-volumes))
        (with-compute-service [compute] (describe-volumes :us-east-1 \"vol-6b218805\" ...))"
  [& [region & volume-ids]]
  (set
    (.describeVolumesInRegion (ebs-services)
      (as-region region Region/DEFAULT)
      (into-array String (if (as-region region)
                           volume-ids
                           (cons region volume-ids))))))

(defvar- snapshot-options-ops
  {:ids #(.snapshotIds % (into-array %2))
   :owners #(.ownedBy % (into-array %2))
   :restorable-by #(.restorableBy % (into-array %2))})

(defn- snapshot-options
  [options-seq]
  (let [optmap (apply hash-map options-seq)
        string-array #(let [v (% optmap)]
                        (into-array String (if (string? v) [v] v)))]
    (-> (DescribeSnapshotsOptions.)
      (.ownedBy (string-array :owner))
      (.snapshotIds (string-array :ids))
      (.restorableBy (string-array :restorable-by)))))

(defn describe-snapshots
  "Returns a set of org.jclouds.aws.ec2.domain.Snapshot instances corresponding to the
   snapshots in the specified region (defaulting to your account's default region)
   limited according to the further options provided.

   This returns all of the snapshots you own (you can also provide \"amazon\" or an AWS
                                               account ID here):
       (with-compute-service [compute]
         (describe-snapshots [:owner \"self\"]))

   This returns metadata on the two specified snapshots in us-west-1 (assuming you have access to them):
       (with-compute-service [compute]
         (describe-snapshots :us-west-1 [:ids [\"snap-44b3ab2d\" \"snap-9e8821f7\"]]))

   There is also a :restorable-by option. Option values can be provided as strings, or
   collections of strings."
  [& [region & option-keyvals]]
  (let [options (apply snapshot-options
                  (if (as-region region)
                    option-keyvals
                    (cons region option-keyvals)))]
  (set
    (.describeSnapshotsInRegion (ebs-services)
      (as-region region Region/DEFAULT)
      (into-array DescribeSnapshotsOptions [options])))))

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

(defn create-snapshot
  "Creates a snapshot of a volume in the specified region with an optional description.
   If provided, the description must be < 255 characters in length. Returns the
   org.jclouds.aws.ec2.domain.Snapshot object representing the created snapshot.

   e.g. (with-compute-service [compute]
          (create-snapshot :us-east-1 \"vol-1dbe6785\")
          (create-snapshot :us-east-1 \"vol-1dbe6785\" \"super-important data\"))"
  [region volume-id & [description]]
  (.createSnapshotInRegion (ebs-services)
    (as-region region)
    (as-string volume-id)
    (into-array CreateSnapshotOptions (when description
                                        [(.withDescription (CreateSnapshotOptions.) description)]))))

(defn delete-snapshot
  "Deletes a snapshot in the specified region.

   e.g. (with-compute-service [compute]
          (delete-snapshot :us-east-1 :snap-252310af)
          (delete-snapshot :us-east-1 \"snap-242adf03\"))"
  [region snapshot-id]
  (.deleteSnapshotInRegion (ebs-services)
    (as-region region)
    (as-string snapshot-id)))

(defn create-volume
  "Creates a new volume given a set of options.  :zone is required, one
   or both of :size and :snapshot may also be provided; all values can be strings or keywords,
   :size may be a number, and :zone may be a org.jclouds.aws.ec2.domain.AvailabilityZone.
   Returns the created org.jclouds.aws.ec2.domain.Volume.

   e.g. (with-compute-service [compute]
          (create-volume :zone :us-east-1a :size 250)
          (create-volume :zone :eu-west-1b :snapshot \"snap-252310af\")
          (create-volume :zone :eu-west-1b :snapshot \"snap-252310af\" :size :1024))"
  [& options]
  (when (-> options count odd?)
    (throw (IllegalArgumentException. "Must provide key-value pairs, e.g. :zone :us-east-1d :size 200")))
  (let [options (apply hash-map options)
        snapshot (get-string options :snapshot)
        size (-?> (get-string options :size) as-int)
        zone (get-string options :zone)
        zone (if zone
               (if (instance? AvailabilityZone zone)
                 zone
                 (AvailabilityZone/fromValue zone))
               (throw (IllegalArgumentException. "Must supply a :zone option.")))
        ebs (ebs-services)]
    (cond
      (and snapshot size) (.createVolumeFromSnapshotInAvailabilityZone ebs zone size snapshot)
      snapshot (.createVolumeFromSnapshotInAvailabilityZone ebs zone snapshot)
      size (.createVolumeInAvailabilityZone ebs zone size)
      :else (throw (IllegalArgumentException. "Must supply :size and/or :snapshot options.")))))

(defn delete-volume
  "Deletes a volume in the specified region.

   e.g. (with-compute-service [compute]
          (delete-volume :us-east-1 :vol-45228a6d)
          (delete-volume :us-east-1 \"vol-052b846c\"))"
  [region volume-id]
  (.deleteVolumeInRegion (ebs-services)
    (as-region region)
    (as-string volume-id)))

(defn attach-volume
  "Attaches a volume to an instance, returning the resulting org.jclouds.aws.ec2.domain.Attachment.
   The region must be a keyword or Region instance; the remaining parameters may be
   strings or keywords.

   e.g. (with-compute-service [compute]
          (attach-volume :us-east-1 :vol-45228a6d \"i-a92358c1\" \"/dev/sdh\"))"
  [region & [volume-id instance-id device :as options]]
  (apply #(.attachVolumeInRegion (ebs-services)
            (as-region region) % %2 %3)
    (map as-string options)))

(defn detach-volume
  "Detatches a volume from the instance to which it is currently attached.
   The optional force? parameter, if logically true, will cause the volume to be forcibly
   detached, regardless of whether it is in-use (mounted) or not.

   (It appears that issuing a detatch-volume command while the volume in question is mounted
    will cause the volume to be detatched immediately upon the volume beign unmounted."
  [region volume-id & [force?]]
  (.detachVolumeInRegion (ebs-services)
    (as-region region)
    (as-string volume-id)
    (boolean force?)
    (into-array DetachVolumeOptions [])))