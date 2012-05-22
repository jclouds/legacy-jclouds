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
package org.jclouds.joyent.sdc.v6_5;

import org.jclouds.joyent.sdc.v6_5.features.DatacenterAsyncClient;
import org.jclouds.joyent.sdc.v6_5.features.DatasetAsyncClient;
import org.jclouds.joyent.sdc.v6_5.features.MachineAsyncClient;
import org.jclouds.joyent.sdc.v6_5.features.PackageAsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to SDC via their REST API.
 * <p/>
 * 
 * @see SDCClient
 * @see <a href="http://sdc.joyent.org/sdcapi.html">api doc</a>
 * @author Adrian Cole
 */
public interface SDCAsyncClient {

   /**
    * Provides asynchronous access to Datacenter features.
    */
   @Delegate
   DatacenterAsyncClient getDatacenterClient();

   /**
    * Provides asynchronous access to Machine features.
    */
   @Delegate
   MachineAsyncClient getMachineClient();

   /**
    * Provides asynchronous access to Dataset features.
    */
   @Delegate
   DatasetAsyncClient getDatasetClient();

   /**
    * Provides asynchronous access to Package features.
    */
   @Delegate
   PackageAsyncClient getPackageClient();
}
