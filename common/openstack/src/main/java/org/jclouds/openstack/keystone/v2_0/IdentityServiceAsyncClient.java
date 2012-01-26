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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Constants;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.binders.BindAuthToJsonPayload;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.ApiAccessKeyCredentials;
import org.jclouds.openstack.keystone.v2_0.domain.PasswordCredentials;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Service via their REST API.
 * <p/>
 * 
 * @see IdentityServiceClient
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Service_API_Client_Operations.html"
 *      />
 * @author Adrian Cole
 */
@Path("/v{" + Constants.PROPERTY_API_VERSION + "}")
public interface IdentityServiceAsyncClient {

   /**
    * @see IdentityServiceClient#authenticateTenantWithCredentials(String,PasswordCredentials)
    */
   @POST
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tokens")
   @MapBinder(BindAuthToJsonPayload.class)
   ListenableFuture<Access> authenticateTenantWithCredentials(@PayloadParam("tenantId") String tenantId,
            PasswordCredentials passwordCredentials);

   /**
    * @see IdentityServiceClient#authenticateTenantWithCredentials(String,ApiAccessKeyCredentials)
    */
   @POST
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tokens")
   @MapBinder(BindAuthToJsonPayload.class)
   ListenableFuture<Access> authenticateTenantWithCredentials(@PayloadParam("tenantId") String tenantId,
            ApiAccessKeyCredentials apiAccessKeyCredentials);

   /**
    * @see IdentityServiceClient#getTenants()
    */
   @GET
   @SelectJson("tenants")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tenants")
   @RequestFilters(AuthenticateRequest.class)
   ListenableFuture<? extends Set<Tenant>> getTenants();


}
