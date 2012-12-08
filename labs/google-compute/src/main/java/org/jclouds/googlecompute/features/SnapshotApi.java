/*
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

package org.jclouds.googlecompute.features;

import org.jclouds.collect.PagedIterable;
import org.jclouds.concurrent.Timeout;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.domain.Snapshot;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to Snapshots via their REST API.
 *
 * @author David Alves
 * @see SnapshotAsyncApi
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/snapshots"/>
 *
 * TODO test snapshots (unsupported as of 11/27/2012)
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface SnapshotApi {

   /**
    * Returns the specified persistent snapshot resource.
    *
    * @param projectName name of the project scoping this request.
    * @param snapshotName     name of the persistent snapshot resource to return.
    * @return a Snapshot resource.
    */
   public Snapshot get(String projectName, String snapshotName);

   /**
    * Creates a persistent snapshot resource in the specified project using the data included in the request.
    *
    * @param projectName name of the project scoping this request.
    * @param snapshot        the snapshot to be inserted.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   public Operation insert(String projectName, Snapshot snapshot);

   /**
    * Deletes the specified persistent snapshot resource.
    *
    * @param projectName name of the project scoping this request.
    * @param snapshotName    name of the persistent snapshot resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   public Operation delete(String projectName, String snapshotName);

   /**
    * Retrieves the list of persistent snapshot resources contained within the specified project.
    *
    * @param projectName name of the project scoping this request.
    * @param listOptions listing options @see ListOptions for details on how to build filters and use pagination
    * @return a collection that might be paginated
    * @see org.jclouds.googlecompute.options.ListOptions , org.jclouds.googlecompute.domain.PagedList ,PagedIterable
    */
   public PagedIterable<Snapshot> list(String projectName, @Nullable ListOptions listOptions);
}
