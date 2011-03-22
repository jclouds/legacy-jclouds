/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.savvis.vpdc.features;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.savvis.vpdc.domain.FirewallService;
import org.jclouds.savvis.vpdc.domain.Network;
import org.jclouds.savvis.vpdc.domain.Org;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.domain.VApp;
import org.jclouds.savvis.vpdc.domain.VDC;
import org.jclouds.savvis.vpdc.filters.SetVCloudTokenCookie;
import org.jclouds.savvis.vpdc.functions.DefaultOrgIfNull;
import org.jclouds.savvis.vpdc.options.BindGetVAppOptions;
import org.jclouds.savvis.vpdc.options.GetVAppOptions;
import org.jclouds.savvis.vpdc.xml.FirewallServiceHandler;
import org.jclouds.savvis.vpdc.xml.NetworkHandler;
import org.jclouds.savvis.vpdc.xml.OrgHandler;
import org.jclouds.savvis.vpdc.xml.TaskHandler;
import org.jclouds.savvis.vpdc.xml.VAppHandler;
import org.jclouds.savvis.vpdc.xml.VDCHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Symphony VPDC resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://api.sandbox.symphonyvpdc.savvis.net/doc/spec/api/index.html" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
@Path("v{jclouds.api-version}")
public interface BrowsingAsyncClient {

   /**
    * @see BrowsingClient#getOrg
    */
   @GET
   @XMLResponseParser(OrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("org/{billingSiteId}")
   ListenableFuture<Org> getOrg(
         @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId);

   /**
    * @see BrowsingClient#getVDCInOrg
    */
   @GET
   @XMLResponseParser(VDCHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("org/{billingSiteId}/vdc/{vpdcId}")
   ListenableFuture<VDC> getVDCInOrg(
         @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
         @PathParam("vpdcId") String vpdcId);

   /**
    * @see BrowsingClient#getNetworkInOrgAndVDC
    */
   @GET
   @XMLResponseParser(NetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("org/{billingSiteId}/vdc/{vpdcId}/network/{network-tier-name}")
   ListenableFuture<Network> getNetworkInOrgAndVDC(
         @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
         @PathParam("vpdcId") String vpdcId, @PathParam("network-tier-name") String networkTierName);

   /**
    * @see BrowsingClient#getVAppInOrgAndVDC
    */
   @GET
   @XMLResponseParser(VAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("org/{billingSiteId}/vdc/{vpdcId}/vApp/{vAppId}")
   ListenableFuture<VApp> getVAppInOrgAndVDC(
         @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId, @PathParam("vAppId") String vAppId,
            @BinderParam(BindGetVAppOptions.class) GetVAppOptions... options);

   /**
    * @see BrowsingClient#getTask
    */
   @GET
   @XMLResponseParser(TaskHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("task/{taskId}")
   ListenableFuture<Task> getTask(@PathParam("taskId") String taskId);

   /**
    * @see BrowsingClient#getFirewallRules
    */
   @GET
   @XMLResponseParser(FirewallServiceHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("org/{billingSiteId}/vdc/{vpdcId}/FirewallService")
   ListenableFuture<FirewallService> getFirewallRules(@PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId, 
		   @PathParam("vpdcId") String vpdcId);
   
}
