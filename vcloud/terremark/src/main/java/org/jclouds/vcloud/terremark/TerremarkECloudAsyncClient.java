/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark;

import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.options.AddInternetServiceOptions;
import org.jclouds.vcloud.terremark.xml.InternetServiceHandler;
import org.jclouds.vcloud.terremark.xml.InternetServicesHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to eCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="http://support.theenterprisecloud.com/kb/default.asp?id=645&Lang=1&SID=" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface TerremarkECloudAsyncClient extends TerremarkAsyncClient {

   /**
    * @see TerremarkVCloudClient#addInternetService
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/extensions/vdc/{vDCId}/internetServices")
   @Produces("application/vnd.tmrk.ecloud.internetService+xml")
   @Consumes("application/vnd.tmrk.ecloud.internetService+xml")
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   @Override
   ListenableFuture<? extends InternetService> addInternetServiceToVDC(
            @PathParam("vDCId") String vDCId, @MapPayloadParam("name") String serviceName,
            @MapPayloadParam("protocol") Protocol protocol, @MapPayloadParam("port") int port,
            AddInternetServiceOptions... options);

   /**
    * @see TerremarkVCloudClient#getAllInternetServices
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/extensions/vdc/{vDCId}/internetServices")
   @Consumes("application/vnd.tmrk.ecloud.internetServicesList+xml")
   @XMLResponseParser(InternetServicesHandler.class)
   @Override
   ListenableFuture<? extends SortedSet<InternetService>> getAllInternetServicesInVDC(
            @PathParam("vDCId") String vDCId);

   /**
    * @see TerremarkVCloudClient#addInternetServiceToExistingIp
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/extensions/publicIp/{ipId}/internetServices")
   @Produces("application/vnd.tmrk.ecloud.internetService+xml")
   @Consumes("application/vnd.tmrk.ecloud.internetService+xml")
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   @Override
   ListenableFuture<? extends InternetService> addInternetServiceToExistingIp(
            @PathParam("ipId") int existingIpId, @MapPayloadParam("name") String serviceName,
            @MapPayloadParam("protocol") Protocol protocol, @MapPayloadParam("port") int port,
            AddInternetServiceOptions... options);

   /**
    * @see TerremarkVCloudClient#getInternetServicesOnPublicIP
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/extensions/publicIp/{ipId}/internetServices")
   @Consumes("application/vnd.tmrk.ecloud.internetServicesList+xml")
   @XMLResponseParser(InternetServicesHandler.class)
   @Override
   ListenableFuture<? extends SortedSet<InternetService>> getInternetServicesOnPublicIp(
            @PathParam("ipId") int ipId);

   /**
    * @see TerremarkVCloudClient#getInternetService
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/extensions/internetService/{internetServiceId}")
   @Consumes("application/vnd.tmrk.ecloud.internetServicesList+xml")
   @XMLResponseParser(InternetServiceHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends InternetService> getInternetService(
            @PathParam("internetServiceId") int internetServiceId);

}
