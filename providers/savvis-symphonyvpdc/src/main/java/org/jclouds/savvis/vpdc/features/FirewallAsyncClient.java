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

import javax.annotation.Nullable;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.savvis.vpdc.binders.BindFirewallRuleToXmlPayload;
import org.jclouds.savvis.vpdc.domain.FirewallRule;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.filters.SetVCloudTokenCookie;
import org.jclouds.savvis.vpdc.functions.DefaultOrgIfNull;
import org.jclouds.savvis.vpdc.xml.TaskHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Symphony VPDC resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://api.sandbox.symphonyvpdc.savvis.net/doc/spec/api/index.html" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface FirewallAsyncClient {

	/**
    * @see FirewallClient#addFirewallRule
    */
   @PUT
   @XMLResponseParser(TaskHandler.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}/vdc/{vpdcId}/FirewallService")
   @MapBinder(BindFirewallRuleToXmlPayload.class)
   ListenableFuture<Task> addFirewallRule(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId, @PayloadParam("firewallRule") FirewallRule firewallRule);
	   
   /**
    * @see FirewallClient#deleteFirewallRule
    */
   @DELETE
   @XMLResponseParser(TaskHandler.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}/vdc/{vpdcId}/FirewallService")
   @MapBinder(BindFirewallRuleToXmlPayload.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Task> deleteFirewallRule(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId, @PayloadParam("firewallRule") FirewallRule firewallRule);

}
