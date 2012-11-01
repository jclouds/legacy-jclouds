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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.features.ServiceAsyncApi;
import org.jclouds.openstack.keystone.v2_0.features.TenantAsyncApi;
import org.jclouds.openstack.keystone.v2_0.features.TokenAsyncApi;
import org.jclouds.openstack.keystone.v2_0.features.UserAsyncApi;
import org.jclouds.openstack.v2_0.features.ExtensionAsyncApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to OpenStack keystone resources via their REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see <a href="http://keystone.openstack.org/" />
 * @see KeystoneApi
 */
public interface KeystoneAsyncApi {

   /**
    * @see KeystoneApi#getApiMetadata()
    */
   @GET
   @SelectJson("version")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ApiMetadata> getApiMetadata();
   
   /**
    * @see KeystoneApi#getServiceApi()
    */
   @Delegate
   ServiceAsyncApi getServiceApi();

   /**
    * Provides asynchronous access to Extension features.
    */
   @Delegate
   ExtensionAsyncApi getExtensionApi();

   /**
    * @see KeystoneApi#getTokenApi()
    */
   @Delegate
   Optional<? extends TokenAsyncApi> getTokenApi();

   /**
    * @see KeystoneApi#getUserApi()
    */
   @Delegate
   Optional<? extends UserAsyncApi> getUserApi();

   /**
    * @see KeystoneApi#getTenantApi()
    */
   @Delegate
   Optional<? extends TenantAsyncApi> getTenantApi();
}
