/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.cloudstack.features;

import java.util.Set;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Snapshot;
import org.jclouds.cloudstack.domain.SnapshotPolicy;
import org.jclouds.cloudstack.domain.SnapshotPolicySchedule;
import org.jclouds.cloudstack.options.CreateSnapshotOptions;
import org.jclouds.cloudstack.options.ListSnapshotPoliciesOptions;
import org.jclouds.cloudstack.options.ListSnapshotsOptions;

/**
 * Provides synchronous access to CloudStack Snapshot features.
 * <p/>
 * 
 * @see SnapshotAsyncClient
 * @see http://download.cloud.com/releases/2.2.0/api/TOC_User.html
 * @author Richard Downer
 */
public interface SnapshotClient {

   /**
    * Creates an instant snapshot of a volume.
    *
    * @param volumeId The ID of the disk volume
    * @param options optional arguments
    * @return an asynchronous job structure
    */
   AsyncCreateResponse createSnapshot(String volumeId, CreateSnapshotOptions... options);

   /**
    * Lists all available snapshots for the account, matching the query described by the options.
    *
    * @param options optional arguments
    * @return the snapshots matching the query
    */
   Set<Snapshot> listSnapshots(ListSnapshotsOptions... options);

   /**
    * Gets a snapshot by its ID.
    *
    * @param id the snapshot ID
    * @return the snapshot with the requested ID
    */
   Snapshot getSnapshot(String id);

   /**
    * Deletes a snapshot of a disk volume.
    *
    * @param id The ID of the snapshot
    * @return an asynchronous job structure
    */
   void deleteSnapshot(String id);

   /**
    * Creates a snapshot policy for the account.
    *
    * @param schedule how to schedule snapshots
    * @param numberToRetain maximum number of snapshots to retain
    * @param timezone Specifies a timezone for this command. For more information on the timezone parameter, see Time Zone Format.
    * @param volumeId the ID of the disk volume
    * @return the newly-created snapshot policy
    */
   SnapshotPolicy createSnapshotPolicy(SnapshotPolicySchedule schedule, String numberToRetain, String timezone, String volumeId);

   /**
    * Deletes a snapshot policy for the account.
    *
    * @param id The ID of the snapshot policy
    * @return
    */
   void deleteSnapshotPolicy(String id);

   /**
    * Deletes snapshot policies for the account.
    *
    * @param id IDs of snapshot policies
    * @return
    */
   void deleteSnapshotPolicies(Iterable<String> id);

   /**
    * Lists snapshot policies.
    *
    * @param volumeId the ID of the disk volume
    * @param options optional arguments
    * @return the snapshot policies matching the query
    */
   Set<SnapshotPolicy> listSnapshotPolicies(String volumeId, ListSnapshotPoliciesOptions... options);

}
