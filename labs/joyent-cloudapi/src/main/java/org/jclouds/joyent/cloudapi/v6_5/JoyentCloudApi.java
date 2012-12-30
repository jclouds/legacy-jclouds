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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.joyent.cloudapi.v6_5.features.DatacenterApi;
import org.jclouds.joyent.cloudapi.v6_5.features.DatasetApi;
import org.jclouds.joyent.cloudapi.v6_5.features.KeyApi;
import org.jclouds.joyent.cloudapi.v6_5.features.MachineApi;
import org.jclouds.joyent.cloudapi.v6_5.features.PackageApi;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides synchronous access to JoyentCloud.
 * <p/>
 * 
 * @see JoyentCloudAsyncApi
 * @see <a href="http://cloudApi.joyent.org/cloudApiapi.html">api doc</a>
 * @author Adrian Cole
 */
public interface JoyentCloudApi {

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
   DatacenterApi getDatacenterApi();
   
   /**
    * Provides synchronous access to Key features.
    */
   @Delegate
   KeyApi getKeyApi();
   
   /**
    * Provides synchronous access to Machine features.
    */
   @Delegate
   MachineApi getMachineApiForDatacenter(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String datacenter);

   /**
    * Provides synchronous access to Dataset features.
    */
   @Delegate
   DatasetApi getDatasetApiForDatacenter(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String datacenter);

   /**
    * Provides synchronous access to Package features.
    */
   @Delegate
   PackageApi getPackageApiForDatacenter(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String datacenter);
}
