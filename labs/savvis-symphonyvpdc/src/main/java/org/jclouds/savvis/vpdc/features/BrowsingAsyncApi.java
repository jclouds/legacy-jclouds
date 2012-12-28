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
package org.jclouds.savvis.vpdc.features;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.savvis.vpdc.domain.FirewallService;
import org.jclouds.savvis.vpdc.domain.Network;
import org.jclouds.savvis.vpdc.domain.Org;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.domain.VDC;
import org.jclouds.savvis.vpdc.domain.VM;
import org.jclouds.savvis.vpdc.filters.SetVCloudTokenCookie;
import org.jclouds.savvis.vpdc.functions.DefaultOrgIfNull;
import org.jclouds.savvis.vpdc.options.BindGetVMOptions;
import org.jclouds.savvis.vpdc.options.GetVMOptions;
import org.jclouds.savvis.vpdc.xml.FirewallServiceHandler;
import org.jclouds.savvis.vpdc.xml.NetworkHandler;
import org.jclouds.savvis.vpdc.xml.OrgHandler;
import org.jclouds.savvis.vpdc.xml.TaskHandler;
import org.jclouds.savvis.vpdc.xml.VDCHandler;
import org.jclouds.savvis.vpdc.xml.VMHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Symphony VPDC resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://api.sandbox.savvis.net/doc/spec/api/index.html" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface BrowsingAsyncApi {

   /**
    * @see BrowsingApi#getOrg
    */
   @GET
   @XMLResponseParser(OrgHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}")
   ListenableFuture<Org> getOrg(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId);

   /**
    * @see BrowsingApi#getVDCInOrg
    */
   @GET
   @XMLResponseParser(VDCHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}/vdc/{vpdcId}")
   ListenableFuture<VDC> getVDCInOrg(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId);

   /**
    * @see BrowsingApi#getNetworkInVDC
    */
   @GET
   @XMLResponseParser(NetworkHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}/vdc/{vpdcId}/network/{network-tier-name}")
   ListenableFuture<Network> getNetworkInVDC(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId, @PathParam("network-tier-name") String networkTierName);

   /**
    * @see BrowsingApi#getVMInVDC
    */
   @GET
   @XMLResponseParser(VMHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}/vdc/{vpdcId}/vApp/{vAppId}")
   ListenableFuture<VM> getVMInVDC(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId, @PathParam("vAppId") String vAppId,
            @BinderParam(BindGetVMOptions.class) GetVMOptions... options);

   /**
    * @see BrowsingApi#getVM
    */
   @GET
   @XMLResponseParser(VMHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<VM> getVM(@EndpointParam URI vm, @BinderParam(BindGetVMOptions.class) GetVMOptions... options);

   /**
    * @see BrowsingApi#getTask
    */
   @GET
   @XMLResponseParser(TaskHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("v{jclouds.api-version}/task/{taskId}")
   ListenableFuture<Task> getTask(@PathParam("taskId") String taskId);

   /**
    * @see BrowsingApi#listFirewallRules
    */
   @GET
   @XMLResponseParser(FirewallServiceHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}/vdc/{vpdcId}/FirewallService")
   ListenableFuture<FirewallService> listFirewallRules(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId);

}
