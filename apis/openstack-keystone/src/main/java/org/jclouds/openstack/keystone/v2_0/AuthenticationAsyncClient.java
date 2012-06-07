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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.binders.BindAuthToJsonPayload;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.ApiAccessKeyCredentials;
import org.jclouds.openstack.keystone.v2_0.domain.PasswordCredentials;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Service via their REST API.
 * <p/>
 * 
 * @see AuthenticationClient
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Service_API_Client_Operations.html"
 *      />
 * @author Adrian Cole
 */
@Path("/v2.0")
public interface AuthenticationAsyncClient {

   /**
    * @see AuthenticationClient#authenticateWithTenantNameAndCredentials(String,PasswordCredentials)
    */
   @POST
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tokens")
   @MapBinder(BindAuthToJsonPayload.class)
   ListenableFuture<Access> authenticateWithTenantNameAndCredentials(@Nullable @PayloadParam("tenantName") String tenantName,
            PasswordCredentials passwordCredentials);
   
   /**
    * @see AuthenticationClient#authenticateWithTenantIdAndCredentials(String,PasswordCredentials)
    */
   @POST
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tokens")
   @MapBinder(BindAuthToJsonPayload.class)
   ListenableFuture<Access> authenticateWithTenantIdAndCredentials(@Nullable @PayloadParam("tenantId") String tenantId,
            PasswordCredentials passwordCredentials);

   /**
    * @see AuthenticationClient#authenticateWithTenantNameAndCredentials(String,ApiAccessKeyCredentials)
    */
   @POST
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tokens")
   @MapBinder(BindAuthToJsonPayload.class)
   // TODO: is tenantName permanent? or should we switch to tenantId at some point. seems most tools
   // still use tenantName
   ListenableFuture<Access> authenticateWithTenantNameAndCredentials(@Nullable @PayloadParam("tenantName") String tenantName,
            ApiAccessKeyCredentials apiAccessKeyCredentials);
   
   /**
    * @see AuthenticationClient#authenticateWithTenantIdAndCredentials(String,ApiAccessKeyCredentials)
    */
   @POST
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tokens")
   @MapBinder(BindAuthToJsonPayload.class)
   ListenableFuture<Access> authenticateWithTenantIdAndCredentials(@Nullable @PayloadParam("tenantId") String tenantId,
            ApiAccessKeyCredentials apiAccessKeyCredentials);
}
