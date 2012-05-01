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
package org.jclouds.openstack.swift.v1;

import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.openstack.swift.v1.features.AccountAsyncClient;
import org.jclouds.openstack.swift.v1.features.ContainerAsyncClient;
import org.jclouds.openstack.swift.v1.features.ObjectAsyncClient;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides asynchronous access to Swift via their REST API.
 * <p/>
 * 
 * @see SwiftClient
 * @see <a href="http://docs.openstack.org/api/openstack-object-storage/1.0/content">api doc</a>
 * @author Adrian Cole
 */
public interface SwiftAsyncClient {
   /**
    * 
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides asynchronous access to Account features.
    */
   @Delegate
   AccountAsyncClient getAccountClientForRegion(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides asynchronous access to Container features.
    */
   @Delegate
   ContainerAsyncClient getContainerClientForRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides asynchronous access to Object features.
    */
   @Delegate
   ObjectAsyncClient getObjectClientForRegion(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);
}
