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
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.savvis.vpdc.binders.BindCaptureVAppTemplateToXmlPayload;
import org.jclouds.savvis.vpdc.binders.BindCloneVMToXmlPayload;
import org.jclouds.savvis.vpdc.binders.BindVMSpecToXmlPayload;
import org.jclouds.savvis.vpdc.binders.BindVMSpecsToXmlPayload;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.domain.VMSpec;
import org.jclouds.savvis.vpdc.filters.SetVCloudTokenCookie;
import org.jclouds.savvis.vpdc.functions.DefaultOrgIfNull;
import org.jclouds.savvis.vpdc.xml.TaskHandler;
import org.jclouds.savvis.vpdc.xml.TasksListHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Symphony VPDC resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://api.sandbox.savvis.net/doc/spec/api/index.html" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface VMAsyncApi {

   /**
    * @see VMApi#addVMIntoVDC
    */
   @GET
   @XMLResponseParser(TaskHandler.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}/vdc/{vpdcId}/vApp/")
   @MapBinder(BindVMSpecToXmlPayload.class)
   ListenableFuture<Task> addVMIntoVDC(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId, VMSpec spec);

   /**
    * @see VMApi#addVMIntoVDC
    */
   @GET
   @XMLResponseParser(TaskHandler.class)
   @Path("vApp/")
   @MapBinder(BindVMSpecToXmlPayload.class)
   ListenableFuture<Task> addVMIntoVDC(@EndpointParam URI vpdc, VMSpec spec);

   /**
    * @see VMApi#addMultipleVMsIntoVDC
    */
   @GET
   @XMLResponseParser(TasksListHandler.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}/vdc/{vpdcId}/vApp/")
   @MapBinder(BindVMSpecsToXmlPayload.class)
   ListenableFuture<Set<Task>> addMultipleVMsIntoVDC(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId, Iterable<VMSpec> vmSpecs);
   
   /**
    * @see VMApi#addMultipleVMsIntoVDC
    */
   @GET
   @XMLResponseParser(TasksListHandler.class)
   @Path("vApp/")
   @MapBinder(BindVMSpecsToXmlPayload.class)
   ListenableFuture<Set<Task>> addMultipleVMsIntoVDC(@EndpointParam URI vpdc, Iterable<VMSpec> vmSpecs);

   /**
    * @see VMApi#captureVApp
    */
   @POST
   @XMLResponseParser(TaskHandler.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}/vdc/{vpdcId}/action/captureVApp")
   @MapBinder(BindCaptureVAppTemplateToXmlPayload.class)
   ListenableFuture<Task> captureVApp(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId, URI vAppUri);

   /**
    * @see VMApi#cloneVApp
    */
   @POST
   @XMLResponseParser(TaskHandler.class)
   @Path("action/cloneVApp")
   @MapBinder(BindCloneVMToXmlPayload.class)
   ListenableFuture<Task> cloneVApp(@EndpointParam URI vAppUri, @PayloadParam("name") String newVAppName,
            @PayloadParam("networkTierName") String networkTierName);

   /**
    * @see VMApi#removeVMFromVDC
    */
   @DELETE
   @XMLResponseParser(TaskHandler.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}/vdc/{vpdcId}/vApp/{vAppId}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Task> removeVMFromVDC(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId, @PathParam("vAppId") String vAppId);

   /**
    * @see VMApi#removeVM
    */
   @DELETE
   @XMLResponseParser(TaskHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Task> removeVM(@EndpointParam URI vm);

   /**
    * @see VMApi#powerOffVM
    */
   @POST
   @XMLResponseParser(TaskHandler.class)
   @Path("action/powerOff")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Task> powerOffVM(@EndpointParam URI vm);

   /**
    * @see VMApi#powerOnVM
    */
   @POST
   @XMLResponseParser(TaskHandler.class)
   @Path("action/powerOn")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Task> powerOnVM(@EndpointParam URI vm);
}
