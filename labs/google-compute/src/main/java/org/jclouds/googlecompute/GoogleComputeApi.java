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

import org.jclouds.concurrent.Timeout;
import org.jclouds.googlecompute.features.DiskApi;
import org.jclouds.googlecompute.features.FirewallApi;
import org.jclouds.googlecompute.features.ImageApi;
import org.jclouds.googlecompute.features.InstanceApi;
import org.jclouds.googlecompute.features.KernelApi;
import org.jclouds.googlecompute.features.MachineTypeApi;
import org.jclouds.googlecompute.features.NetworkApi;
import org.jclouds.googlecompute.features.OperationApi;
import org.jclouds.googlecompute.features.ProjectApi;
import org.jclouds.googlecompute.features.ZoneApi;
import org.jclouds.rest.annotations.Delegate;

import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to GoogleCompute.
 * <p/>
 *
 * @author David Alves
 * @see GoogleComputeAsyncApi
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13">api doc</a>
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface GoogleComputeApi {

   /**
    * Provides synchronous access to Disk features
    */
   @Delegate
   DiskApi getDiskApi();

   /**
    * Provides synchronous access to Firewall features
    */
   @Delegate
   FirewallApi getFirewallApi();

   /**
    * Provides synchronous access to Image features
    */
   @Delegate
   ImageApi getImageApi();

   /**
    * Provides synchronous access to Instance features
    */
   @Delegate
   InstanceApi getInstanceApi();

   /**
    * Provides synchronous access to MachineType features
    */
   @Delegate
   MachineTypeApi getMachineTypeApi();

   /**
    * Provides synchronous access to Network features
    */
   @Delegate
   NetworkApi getNetworkApi();

   /**
    * Provides synchronous access to Kernel features
    */
   @Delegate
   KernelApi getKernelApi();

   /**
    * Provides synchronous access to Operation features
    */
   @Delegate
   OperationApi getOperationApi();

   /**
    * Provides synchronous access to Project features
    */
   @Delegate
   ProjectApi getProjectApi();

   /**
    * Provides synchronous access to Snapshot features
    *
    *  TODO snapshots are unsupported by GCE as of 11/27/2012
    */
//   @Delegate
//   SnapshotApi getSnapshotApi();

   /**
    * Provides synchronous access to Zone features
    */
   @Delegate
   ZoneApi getZoneApi();

}
