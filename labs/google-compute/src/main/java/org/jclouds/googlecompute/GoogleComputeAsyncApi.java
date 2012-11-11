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
package org.jclouds.googlecompute;

import org.jclouds.googlecompute.features.DiskAsyncApi;
import org.jclouds.googlecompute.features.FirewallAsyncApi;
import org.jclouds.googlecompute.features.ImageAsyncApi;
import org.jclouds.googlecompute.features.InstanceAsyncApi;
import org.jclouds.googlecompute.features.KernelAsyncApi;
import org.jclouds.googlecompute.features.MachineTypeAsyncApi;
import org.jclouds.googlecompute.features.NetworkAsyncApi;
import org.jclouds.googlecompute.features.OperationAsyncApi;
import org.jclouds.googlecompute.features.ProjectAsyncApi;
import org.jclouds.googlecompute.features.ZoneAsyncApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to GoogleCompute via their REST API.
 * <p/>
 *
 * @author David Alves
 * @see GoogleComputeApi
 */
public interface GoogleComputeAsyncApi {

   /**
    * Provides asynchronous access to Disk features
    */
   @Delegate
   DiskAsyncApi getDiskApi();

   /**
    * Provides asynchronous access to Firewall features
    */
   @Delegate
   FirewallAsyncApi getFirewallApi();

   /**
    * Provides asynchronous access to Image features
    */
   @Delegate
   ImageAsyncApi getImageApi();

   /**
    * Provides asynchronous access to Instance features
    */
   @Delegate
   InstanceAsyncApi getInstanceApi();

   /**
    * Provides asynchronous access to Kernel features
    */
   @Delegate
   KernelAsyncApi getKernelApi();

   /**
    * Provides asynchronous access to MachineType features
    */
   @Delegate
   MachineTypeAsyncApi getMachineTypeApi();

   /**
    * Provides asynchronous access to Network features
    */
   @Delegate
   NetworkAsyncApi getNetworkApi();

   /**
    * Provides asynchronous access to Operation features
    */
   @Delegate
   OperationAsyncApi getOperationApi();

   /**
    * Provides asynchronous access to Project features
    */
   @Delegate
   ProjectAsyncApi getProjectApi();

   /**
    * Provides asynchronous access to Snapshot features
    *
    * TODO snapshots are unsupported by GCE as of 11/27/2012
    */
//   @Delegate
//   SnapshotAsyncApi getSnapshotApi();

   /**
    * Provides asynchronous access to Zone features
    */
   @Delegate
   ZoneAsyncApi getZoneApi();
}
