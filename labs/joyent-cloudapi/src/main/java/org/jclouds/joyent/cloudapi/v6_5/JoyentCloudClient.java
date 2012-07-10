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
package org.jclouds.joyent.cloudapi.v6_5;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.joyent.cloudapi.v6_5.features.DatacenterClient;
import org.jclouds.joyent.cloudapi.v6_5.features.DatasetClient;
import org.jclouds.joyent.cloudapi.v6_5.features.KeyClient;
import org.jclouds.joyent.cloudapi.v6_5.features.MachineClient;
import org.jclouds.joyent.cloudapi.v6_5.features.PackageClient;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides synchronous access to JoyentCloud.
 * <p/>
 * 
 * @see JoyentCloudAsyncClient
 * @see <a href="http://cloudApi.joyent.org/cloudApiapi.html">api doc</a>
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface JoyentCloudClient {

   /**
    * 
    * @return the datacenter codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredDatacenters();

   /**
    * Provides synchronous access to Datacenter features.
    */
   @Delegate
   DatacenterClient getDatacenterClient();
   
   /**
    * Provides synchronous access to Key features.
    */
   @Delegate
   KeyClient getKeyClient();
   
   /**
    * Provides synchronous access to Machine features.
    */
   @Delegate
   MachineClient getMachineClientForDatacenter(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String datacenter);

   /**
    * Provides synchronous access to Dataset features.
    */
   @Delegate
   DatasetClient getDatasetClientForDatacenter(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String datacenter);

   /**
    * Provides synchronous access to Package features.
    */
   @Delegate
   PackageClient getPackageClientForDatacenter(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String datacenter);
}
