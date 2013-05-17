/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.cinder.v1.features;

import org.jclouds.openstack.cinder.v1.domain.Snapshot;
import org.jclouds.openstack.cinder.v1.options.CreateSnapshotOptions;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Volume Snapshots via their REST API.
 * 
 * @see SnapshotAsyncApi
 * @see <a href="http://api.openstack.org/">API Doc</a>
 * @author Everett Toews
 */
public interface SnapshotApi {
   /**
    * Returns a summary list of Snapshots.
    *
    * @return The list of Snapshots
    */
   FluentIterable<? extends Snapshot> list();

   /**
    * Returns a detailed list of Snapshots.
    *
    * @return The list of Snapshots
    */
   FluentIterable<? extends Snapshot> listInDetail();

   /**
    * Return data about the given Snapshot.
    *
    * @param snapshotId Id of the Snapshot
    * @return Details of a specific Snapshot
    */
   Snapshot get(String snapshotId);

   /**
    * Creates a new Snapshot. The Volume status must be Available.
    * 
    * @param volumeId The Volume Id from which to create the Snapshot
    * @param options See CreateSnapshotOptions
    * @return The new Snapshot
    */
   Snapshot create(String volumeId, CreateSnapshotOptions... options);

   /**
    * Delete a Snapshot.
    *
    * @param snapshotId Id of the Snapshot
    * @return true if successful, false otherwise
    */
   boolean delete(String snapshotId);
}
