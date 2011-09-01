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
package org.jclouds.cloudloadbalancers;

import java.util.Set;

import org.jclouds.cloudloadbalancers.features.LoadBalancerAsyncClient;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides asynchronous access to CloudLoadBalancers via their REST API.
 * <p/>
 * 
 * @see CloudLoadBalancersClient
 * @see <a
 *      href="http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch04s01.html"
 *      />
 * @author Adrian Cole
 */
public interface CloudLoadBalancersAsyncClient {

   /**
    * 
    * @return the region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides asynchronous access to LoadBalancer features.
    */
   @Delegate
   LoadBalancerAsyncClient getLoadBalancerClient(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) String region);

}
