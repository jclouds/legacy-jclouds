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
    org.jclouds.compute.domain.NodeMetadata
    (org.jclouds.aws.ec2.domain Volume Snapshot AvailabilityZone)
    (org.jclouds.aws.ec2.options DescribeSnapshotsOptions DetachVolumeOptions CreateSnapshotOptions)))

(defn snapshot?
  "Returns true iff the argument is a org.jclouds.aws.ec2.domain.Snapshot."
  [s]
  (instance? Snapshot s))

(defn volume?
  "Returns true iff the argument is a org.jclouds.aws.ec2.domain.Volume."
  [v]
  (instance? Volume v))

(defn #^org.jclouds.aws.ec2.services.ElasticBlockStoreClient
  ebs-service
  "Returns the synchronous ElasticBlockStoreClient associated with
   the specified compute service, or compute/*compute* as bound by with-compute-service."
  [& [compute]]
  (-> (or compute compute/*compute*)
    .getContext .getProviderSpecificContext .getApi .getElasticBlockStoreServices))

(defn get-region
  "Returns the first argument as the corresponding Region if it is a
   keyword or already a Region instance. An optional second argument
   is returned if the first cannot be coerced into a Region.
   Returns nil otherwise."
  ([v] (get-region v nil))
  ([v default-region]
    (cond
      (keyword? v) (name v)
      (instance? Region v) v
      (instance? NodeMetadata v) (let [zone (.getLocationId v)]
                                   ; no easier way to go from zone -> region?
                                   (if (> (.indexOf zone "-") -1)
                                                       (subs zone 0 (-> zone count dec))
                                                       zone))
    :else default-region)))

(defn get-volume-id
  "Returns a string volume ID taken from the given string, keyword, or Volume argument."
  [v]
  (cond
    (instance? Volume v) (.getId #^Volume v)
    (keyword? v) (name v)
    (string? v) v
    :else (throw (IllegalArgumentException.
                   (str "Can't obtain volume id from argument of type " (class v))))))

(defn volumes
  "Returns a set of org.jclouds.aws.ec2.domain.Volume instances corresponding to the
   volumes in the specified region (defaulting to your account's default region).

   e.g. (with-compute-service [compute] (volumes))
        (with-compute-service [compute] (volumes :us-east-1 \"vol-6b218805\" ...))"
  [& [region & volume-ids]]
  (set
    (.describeVolumesInRegion (ebs-service)
      (get-region region)
      (into-array String (map get-volume-id
                           (if (get-region region)
                             volume-ids
                             (when region (cons region volume-ids))))))))
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

(defn- snapshot-options
  [optmap]
  (let [string-array #(let [v (% optmap)]
                        (into-array String (cond
                                             (keyword? v) [(name v)]
                                             (string? v) [v]
                                             :else (map as-string v))))]
    (-> (DescribeSnapshotsOptions.)
      (.ownedBy (string-array :owner))
      (.snapshotIds (string-array :ids))
      (.restorableBy (string-array :restorable-by)))))

(defn snapshots
  "Returns a set of org.jclouds.aws.ec2.domain.Snapshot instances that match
   the criteria provided.  Options include:

   :region - region string or keyword
   :owner - AWS account id (or \"amazon\" or \"self\")
   :restorable-by - AWS account id

   Multiple values for each type of criteria can be provided by passing a seq
   of the appropriate types as values.

    (with-compute-service [compute]
      (snapshots :owner \"self\")
      (snapshots :region :us-west-1 :ids [\"snap-44b3ab2d\" \"snap-9e8821f7\"]))"
  [& options]
  (let [options (apply hash-map options)
        region (:region options)
        options (snapshot-options (dissoc options :region))]
    (set
      (.describeSnapshotsInRegion (ebs-service)
        (get-region region)
        (into-array DescribeSnapshotsOptions [options])))))

(defn create-snapshot
  "Creates a snapshot of a volume in the specified region with an optional description.
   If provided, the description must be < 255 characters in length. Returns the
   org.jclouds.aws.ec2.domain.Snapshot object representing the created snapshot.

   e.g. (with-compute-service [compute]
          (create-snapshot some-volume-instance)
          (create-snapshot :us-east-1 \"vol-1dbe6785\" nil)
          (create-snapshot :us-east-1 \"vol-1dbe6785\" \"super-important data\"))"
  ([#^Volume volume] (create-snapshot volume nil))
  ([#^Volume volume description] (create-snapshot (.getRegion volume) (.getId volume) description))
  ([region volume-id description]
    (.createSnapshotInRegion (ebs-service)
      (get-region region)
      (as-string volume-id)
      (into-array CreateSnapshotOptions (when description
                                          [(.withDescription (CreateSnapshotOptions.) description)])))))

(defn delete-snapshot
  "Deletes a snapshot in the specified region.

   e.g. (with-compute-service [compute]
          (delete-snapshot :us-east-1 :snap-252310af)
          (delete-snapshot :us-east-1 \"snap-242adf03\"))"
  ([#^Snapshot snapshot] (delete-snapshot (.getRegion snapshot) (.getId snapshot)))
  ([region snapshot-id]
  (.deleteSnapshotInRegion (ebs-service)
    (get-region region)
    (as-string snapshot-id))))

(defn get-zone
  [v]
  (cond
    (instance? AvailabilityZone v) v
    (instance? NodeMetadata v) (AvailabilityZone/fromValue (.getLocationId #^NodeMetadata v))
    (string? v) (AvailabilityZone/fromValue v)
    (keyword? v) (AvailabilityZone/fromValue (name v))
    :else (throw (IllegalArgumentException.
                   (str "Can't obtain zone from argument of type " (class v))))))

(defn attach-volume
  "Attaches a volume to an instance, returning the resulting org.jclouds.aws.ec2.domain.Attachment.

   e.g. (with-compute-service [compute]
          (attach-volume :us-east-1 \"i-a92358c1\" :vol-45228a6d \"/dev/sdh\")
          (attach-volume some-node-instance :vol-45228a6d \"/dev/sdh\")
          (attach-volume some-node-instance some-volume-instance \"/dev/sdh\"))"
  ([#^NodeMetadata node volume device]
    (attach-volume node (.getId node) (get-volume-id volume) device))
  ([region instance-id volume-id device]
    (apply #(.attachVolumeInRegion (ebs-service)
              (get-region region) % %2 %3)
      (map as-string [volume-id instance-id device]))))

(defn detach-volume
  "Detatches a volume from the instance to which it is currently attached.
   The volume may be specified with a Volume instance, a string, or a keyword.
   Providing a logical true value for the :force option will cause the volume
   to be forcibly detached, regardless of whether it is in-use (mounted) or not.

   If the volume is specified as a string or keyword, one of the following options
   is additionally required:

   :region - the region where the volume is allocated
   :node - a node in the region where the volume is allocated

   FYI: It appears that issuing a detatch-volume command while the volume in question is mounted
   will cause the volume to be detatched immediately upon the volume beign unmounted."
  [volume & options]
  (let [options (apply hash-map options)
        volume-id (get-volume-id volume)
        region (get-region (if (instance? Volume volume)
                             (.getRegion volume)
                             (or (:region options) (:node options))))]
    (when (not region)
      (throw (IllegalArgumentException.
               "Must specify volume's region via :region or :node options, or by providing a Volume instance.")))
    (.detachVolumeInRegion (ebs-service)
      region
      volume-id
      (boolean (:force options))
      (into-array DetachVolumeOptions []))))

(defn create-volume
  "Creates a new volume given a set of options:

   - one of :zone (keyword, string, or AvailabilityZone) or :node (NodeMetadata)
   - one or both of :snapshot (keyword, string, or Snapshot instance) or :size
     (string, keyword, or number)
   - :device (string or keyword) provided *only* when you want to attach the new volume to
     the :node you specified!

   Returns a vector of [created org.jclouds.aws.ec2.domain.Volume,
                        optional org.jclouds.aws.ec2.domain.Attachment]

   Note that specifying :node instead of :zone will only attach the created volume
   :device is also provided.  Otherwise, the node is only used to obtain the desired
   availability zone.

   Note also that if :device and :node are specified, and the attach operation fails,
   you will have \"leaked\" the newly-created volume
   (volume creation and attachment cannot be done atomically).

   e.g. (with-compute-service [compute]
          (create-volume :zone :us-east-1a :size 250)
          (create-volume :node node-instance :size 250)
          (create-volume :node node-instance :size 250 :device \"/dev/sdj\")
          (create-volume :zone :eu-west-1b :snapshot \"snap-252310af\")
          (create-volume :zone :eu-west-1b :snapshot snapshot-instance)
          (create-volume :zone :eu-west-1b :snapshot \"snap-252310af\" :size :1024))"
  [& options]
  (when (-> options count odd?)
    (throw (IllegalArgumentException. "Must provide key-value pairs, e.g. :zone :us-east-1d :size 200")))
  (let [options (apply hash-map options)
        snapshot (get-string options :snapshot)
        snapshot (if (snapshot? snapshot) (.getId snapshot) snapshot)
        size (-?> (get-string options :size) as-int)
        #^NodeMetadata node (:node options)
        zone (or node (get-string options :zone))
        zone (if zone
               (get-zone zone)
               (throw (IllegalArgumentException. "Must supply a :zone or :node option.")))
        ebs (ebs-service)]
    (when (and (:device options) (not node))
      (throw (IllegalArgumentException. "Cannot create and attach new volume; no :node specified")))
    (let [new-volume (cond
                       (and snapshot size) (.createVolumeFromSnapshotInAvailabilityZone ebs zone size snapshot)
                       snapshot (.createVolumeFromSnapshotInAvailabilityZone ebs zone snapshot)
                       size (.createVolumeInAvailabilityZone ebs zone size)
                       :else (throw (IllegalArgumentException. "Must supply :size and/or :snapshot options.")))]
      [new-volume (when (:device options)
                    (attach-volume node new-volume (as-string (:device options))))])))

(defn delete-volume
  "Deletes a volume in the specified region.

   e.g. (with-compute-service [compute]
          (delete-volume :us-east-1 :vol-45228a6d)
          (delete-volume :us-east-1 \"vol-052b846c\"))"
  ([#^Volume volume]
    (delete-volume (.getRegion volume) (.getId volume)))
  ([region volume-id]
    (.deleteVolumeInRegion (ebs-service)
      (get-region region)
      (as-string volume-id))))