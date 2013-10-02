/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rackspace.cloudidentity.v2_0;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.AuthenticationAsyncApi;
import org.jclouds.openstack.keystone.v2_0.binders.BindAuthToJsonPayload;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.rackspace.cloudidentity.v2_0.domain.ApiKeyCredentials;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Service via their REST API.
 * <p/>
 * 
 * @see AuthenticationApi
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Service_API_Api_Operations.html"
 *      />
 * @author Adrian Cole
 * @deprecated will be removed in jclouds 1.7, as async interfaces are no longer
 *             supported. please use {@link CloudIdentityAuthenticationAsyncApi}
 */
@Deprecated
public interface CloudIdentityAuthenticationAsyncApi extends AuthenticationAsyncApi {

   /**
    * @see CloudIdentityAuthenticationAsyncApi#authenticateWithTenantNameAndCredentials(String,ApiKeyCredentials)
    */
   @Named("auth:tenantnameandcreds")
   @POST
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tokens")
   @MapBinder(BindAuthToJsonPayload.class)
   ListenableFuture<Access> authenticateWithTenantNameAndCredentials(@Nullable @PayloadParam("tenantName") String tenantName,
            ApiKeyCredentials apiKeyCredentials);

   /**
    * @see CloudIdentityAuthenticationAsyncApi#authenticateWithTenantIdAndCredentials(String,ApiKeyCredentials)
    */
   @Named("auth:tenantidandcreds")
   @POST
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tokens")
   @MapBinder(BindAuthToJsonPayload.class)
   ListenableFuture<Access> authenticateWithTenantIdAndCredentials(@Nullable @PayloadParam("tenantId") String tenantId,
            ApiKeyCredentials apiKeyCredentials);

}
