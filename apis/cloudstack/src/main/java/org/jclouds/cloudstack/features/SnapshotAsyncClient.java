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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudstack.binders.BindIdListToCommaDelimitedQueryParam;
import org.jclouds.cloudstack.binders.BindSnapshotPolicyScheduleToQueryParam;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Snapshot;
import org.jclouds.cloudstack.domain.SnapshotPolicy;
import org.jclouds.cloudstack.domain.SnapshotPolicySchedule;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.CreateSnapshotOptions;
import org.jclouds.cloudstack.options.ListSnapshotPoliciesOptions;
import org.jclouds.cloudstack.options.ListSnapshotsOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides synchronous access to CloudStack Snapshot features.
 * <p/>
 * 
 * @see SnapshotClient
 * @see http://download.cloud.com/releases/2.2.0/api/TOC_User.html
 * @author Richard Downer
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface SnapshotAsyncClient {

   /**
    * Creates an instant snapshot of a volume.
    *
    * @param volumeId The ID of the disk volume
    * @param options optional arguments
    * @return an asynchronous job structure
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "createSnapshot")
   @Unwrap
   ListenableFuture<AsyncCreateResponse> createSnapshot(@QueryParam("volumeid") String volumeId, CreateSnapshotOptions... options);

   /**
    * Lists all available snapshots for the account, matching the query described by the options.
    *
    * @param options optional arguments
    * @return the snapshots matching the query
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = { "command", "listAll" }, values = { "listSnapshots", "true" })
   @SelectJson("snapshot")
   @Unwrap
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Snapshot>> listSnapshots(ListSnapshotsOptions... options);

   /**
    * Gets a snapshot by its ID.
    *
    * @param id the snapshot ID
    * @return the snapshot with the requested ID
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = { "command", "listAll" }, values = { "listSnapshots", "true" })
   @SelectJson("snapshot")
   @OnlyElement
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Snapshot> getSnapshot(@QueryParam("id") String id);

   /**
    * Deletes a snapshot of a disk volume.
    *
    * @param id The ID of the snapshot
    * @return an asynchronous job structure
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "deleteSnapshot")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteSnapshot(@QueryParam("id") String id);

   /**
    * Creates a snapshot policy for the account.
    *
    * @param schedule how to schedule snapshots
    * @param numberToRetain maximum number of snapshots to retain
    * @param timezone Specifies a timezone for this command. For more information on the timezone parameter, see Time Zone Format.
    * @param volumeId the ID of the disk volume
    * @return the newly-created snapshot policy
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Unwrap
   @QueryParams(keys = "command", values = "createSnapshotPolicy")
   ListenableFuture<SnapshotPolicy> createSnapshotPolicy(@BinderParam(BindSnapshotPolicyScheduleToQueryParam.class) SnapshotPolicySchedule schedule, @QueryParam("maxsnaps") String numberToRetain, @QueryParam("timezone") String timezone, @QueryParam("volumeid") String volumeId);

   /**
    * Deletes a snapshot policy for the account.
    *
    * @param id The ID of the snapshot policy
    * @return
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "deleteSnapshotPolicies")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteSnapshotPolicy(@QueryParam("id") String id);

   /**
    * Deletes snapshot policies for the account.
    *
    * @param id IDs of snapshot policies
    * @return
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "deleteSnapshotPolicies")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteSnapshotPolicies(@BinderParam(BindIdListToCommaDelimitedQueryParam.class) Iterable<String> id);

   /**
    * Lists snapshot policies.
    *
    * @param volumeId the ID of the disk volume
    * @param options optional arguments
    * @return the snapshot policies matching the query
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = { "command", "listAll" }, values = { "listSnapshotPolicies", "true" })
   @Unwrap
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<SnapshotPolicy>> listSnapshotPolicies(@QueryParam("volumeid") String volumeId, ListSnapshotPoliciesOptions... options);
}
