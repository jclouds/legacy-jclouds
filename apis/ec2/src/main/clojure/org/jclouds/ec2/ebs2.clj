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

(ns
  #^{:author "Chas Emerick, cemerick@snowtide.com"
     :doc "A clojure binding to the jclouds EBS service interface."}
  org.jclouds.ec2.ebs2
  (:use org.jclouds.compute2 [clojure.core.incubator :only (-?>)])
  (:import org.jclouds.aws.domain.Region
    org.jclouds.compute.domain.NodeMetadata
    (org.jclouds.ec2.domain Volume Volume$Status Snapshot Snapshot$Status AvailabilityZoneInfo)
    (org.jclouds.ec2.options DescribeSnapshotsOptions DetachVolumeOptions CreateSnapshotOptions)))
(defn snapshot?
  "Returns true iff the argument is a org.jclouds.ec2.domain.Snapshot."
  [s]
  (instance? Snapshot s))

(defn volume?
  "Returns true iff the argument is a org.jclouds.ec2.domain.Volume."
  [v]
  (instance? Volume v))

(defn ^org.jclouds.ec2.services.ElasticBlockStoreClient
  ebs-service
  ""
  [compute]
  (-> compute
    .getContext
    .getProviderSpecificContext
    .getApi
    .getElasticBlockStoreServices))

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

(defn get-volume-id
  "Returns a string volume ID taken from the given string, keyword, or Volume argument."
  [v]
  (cond
    (instance? Volume v) (.getId ^Volume v)
    (keyword? v) (name v)
    (string? v) v
    :else (throw (IllegalArgumentException.
    (str "Can't obtain volume id from argument of type " (class v))))))

(defn volumes
  "Returns a set of org.jclouds.ec2.domain.Volume instances corresponding to the
   volumes in the specified region (defaulting to your account's default region)."
  [compute & [region & volume-ids]]
  (set
    (.describeVolumesInRegion (ebs-service compute)
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

   :region - region string, keyword, or NodeMetadata
   :owner - AWS account id (or \"amazon\" or \"self\")
   :restorable-by - AWS account id

   Multiple values for each type of criteria can be provided by passing a seq
   of the appropriate types as values."
  [compute & options]
  (let [options (apply hash-map options)
        region (:region options)
        options (snapshot-options (dissoc options :region))]
    (set
      (.describeSnapshotsInRegion (ebs-service compute)
        (get-region region)
        (into-array DescribeSnapshotsOptions [options])))))

(defn create-snapshot
  "Creates a snapshot of a volume in the specified region with an optional description.
   If provided, the description must be < 255 characters in length. Returns the
   org.jclouds.aws.ec2.domain.Snapshot object representing the created snapshot."
  ([compute ^Volume volume] (create-snapshot compute volume nil))
  ([compute ^Volume volume description] (create-snapshot compute (.getRegion volume) (.getId volume) description))
  ([compute region volume-id description]
    (.createSnapshotInRegion (ebs-service compute)
      (get-region region)
      (as-string volume-id)
      (into-array CreateSnapshotOptions (when description
        [(.withDescription (CreateSnapshotOptions.) description)])))))

(defn delete-snapshot
  "Deletes a snapshot in the specified region."
  ([compute ^Snapshot snapshot] (delete-snapshot compute (.getRegion snapshot) (.getId snapshot)))
  ([compute region snapshot-id]
    (.deleteSnapshotInRegion (ebs-service compute)
      (get-region region)
      (as-string snapshot-id))))

(defn get-zone
  [v]
  (cond
    (instance? AvailabilityZoneInfo v) (.getZone v)
    (instance? NodeMetadata v) (location ^NodeMetadata v)
    (string? v) v
    (keyword? v) (name v)
    :else (throw (IllegalArgumentException.
    (str "Can't obtain zone from argument of type " (class v))))))

(defn attach-volume
  "Attaches a volume to an instance, returning the resulting org.jclouds.aws.ec2.domain.Attachment."
  ([compute ^NodeMetadata node volume device]
    (attach-volume compute node (.getProviderId node) (get-volume-id volume) device))
  ([compute region instance-id volume-id device]
    (apply #(.attachVolumeInRegion (ebs-service compute)
      (get-region region) % %2 %3)
      (map as-string [volume-id instance-id device]))))

(defn detach-volume
  "Detaches a volume from the instance to which it is currently attached.
   The volume may be specified with a Volume instance, a string, or a keyword.
   Providing a logical true value for the :force option will cause the volume
   to be forcibly detached, regardless of whether it is in-use (mounted) or not.

   If the volume is specified as a string or keyword, one of the following options
   is additionally required:

   :region - the region where the volume is allocated
   :node - a node in the region where the volume is allocated

   FYI: It appears that issuing a detatch-volume command while the volume in question is mounted
   will cause the volume to be detatched immediately upon the volume beign unmounted."
  [compute volume & options]
  (let [options (apply hash-map options)
        volume-id (get-volume-id volume)
        region (get-region (if (instance? Volume volume)
      (.getRegion volume)
      (or (:region options) (:node options))))]
    (when (not region)
      (throw (IllegalArgumentException.
        "Must specify volume's region via :region or :node options, or by providing a Volume instance.")))
    (.detachVolumeInRegion (ebs-service compute)
      region
      volume-id
      (boolean (:force options))
      (into-array DetachVolumeOptions []))))

(defn create-volume
  "Creates a new volume given a set of options:

   - one of :zone (keyword, string, or AvailabilityZoneInfo) or :node (NodeMetadata)
   - one or both of :snapshot (keyword, string, or Snapshot instance) or :size
     (string, keyword, or number)
   - :device (string or keyword) provided *only* when you want to attach the new volume to
     the :node you specified!

   Returns a vector of [created org.jclouds.ec2.domain.Volume,
                        optional org.jclouds.ec2.domain.Attachment]

   Note that specifying :node instead of :zone will only attach the created volume
   :device is also provided.  Otherwise, the node is only used to obtain the desired
   availability zone.

   Note also that if :device and :node are specified, and the attach operation fails,
   you will have \"leaked\" the newly-created volume
   (volume creation and attachment cannot be done atomically)."
  [compute & options]
  (when (-> options count odd?)
    (throw (IllegalArgumentException. "Must provide key-value pairs, e.g. :zone :us-east-1d :size 200")))
  (let [options (apply hash-map options)
        snapshot (get-string options :snapshot)
        snapshot (if (snapshot? snapshot) (.getId snapshot) snapshot)
        size (-?> (get-string options :size) as-int)
        ^NodeMetadata node (:node options)
        zone (or node (get-string options :zone))
        zone (if zone
      (get-zone zone)
      (throw (IllegalArgumentException. "Must supply a :zone or :node option.")))
        ebs (ebs-service compute)]
    (when (and (:device options) (not node))
      (throw (IllegalArgumentException. "Cannot create and attach new volume; no :node specified")))
    (let [new-volume (cond
      (and snapshot size) (.createVolumeFromSnapshotInAvailabilityZone ebs zone size snapshot)
      snapshot (.createVolumeFromSnapshotInAvailabilityZone ebs zone snapshot)
      size (.createVolumeInAvailabilityZone ebs zone size)
      :else (throw (IllegalArgumentException. "Must supply :size and/or :snapshot options.")))]
      [new-volume (when (:device options)
        (attach-volume compute node new-volume (as-string (:device options))))])))

(defn delete-volume
  "Deletes a volume in the specified region."
  ([compute ^Volume volume]
    (delete-volume (.getRegion volume) (.getId volume)))
  ([compute region volume-id]
    (.deleteVolumeInRegion (ebs-service compute)
      (get-region region)
      (as-string volume-id))))

(defn status
  "Returns the status of the given entity; works for Volumes and Snapshots."
  [k]
  (.getStatus k))

(defn status-available?
  [^Volume v]
  (= Volume$Status/AVAILABLE (status v)))

(defn status-creating?
  [^Volume v]
  (= Volume$Status/CREATING (status v)))

(defn status-deleting?
  [^Volume v]
  (= Volume$Status/DELETING (status v)))

(defn status-in-use?
  [^Volume v]
  (= Volume$Status/IN_USE (status v)))

(defn status-completed?
  [^Snapshot s]
  (= Snapshot$Status/COMPLETED (status s)))

(defn status-error?
  [^Snapshot s]
  (= Snapshot$Status/ERROR (status s)))

(defn status-pending?
  [^Snapshot s]
  (= Snapshot$Status/PENDING (status s)))