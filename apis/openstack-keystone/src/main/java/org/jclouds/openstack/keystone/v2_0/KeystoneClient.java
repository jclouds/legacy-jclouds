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
package org.jclouds.openstack.keystone.v2_0;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.features.ServiceClient;
import org.jclouds.openstack.keystone.v2_0.features.TenantClient;
import org.jclouds.openstack.keystone.v2_0.features.TokenClient;
import org.jclouds.openstack.keystone.v2_0.features.UserClient;
import org.jclouds.rest.annotations.Delegate;

import com.google.common.base.Optional;

/**
 * Provides access to Openstack keystone resources via their REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see <a href="http://keystone.openstack.org/" />
 * @see KeystoneAsyncClient
 */
@Timeout(duration = 10, timeUnit = TimeUnit.SECONDS)
public interface KeystoneClient {

   /**
    * Discover API version information, links to documentation (PDF, HTML, WADL), and supported media types
    *
    * @return the requested information
    */
   ApiMetadata getApiMetadata();
   
   /** 
    * Provides synchronous access to Token features 
    */
   @Delegate
   ServiceClient getServiceClient();

   /** 
    * Provides synchronous access to Token features 
    */
   @Delegate
   Optional<TokenClient> getTokenClient();

   /** 
    * Provides synchronous access to User features 
    */
   @Delegate
   Optional<UserClient> getUserClient();
   

   /** 
    * Provides synchronous access to Tenant features 
    */
   @Delegate
   Optional<TenantClient> getTenantClient();
}