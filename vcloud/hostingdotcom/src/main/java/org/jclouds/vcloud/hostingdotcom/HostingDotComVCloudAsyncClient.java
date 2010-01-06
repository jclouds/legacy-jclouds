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
package org.jclouds.vcloud.hostingdotcom;

import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPP_XML;

import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.binders.BindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.functions.VAppTemplateIdToUri;
import org.jclouds.vcloud.hostingdotcom.domain.HostingDotComVApp;
import org.jclouds.vcloud.hostingdotcom.xml.HostingDotComVAppHandler;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.xml.CatalogHandler;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface HostingDotComVCloudAsyncClient extends VCloudAsyncClient {

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.Catalog.class)
   @Consumes(CATALOG_XML)
   @Produces(CATALOG_XML)
   // produces is incorrect, but required for hosting.com to operate
   @XMLResponseParser(CatalogHandler.class)
   @Override
   Future<? extends Catalog> getDefaultCatalog();

   @GET
   @Consumes(VAPP_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vapp/{vAppId}")
   @XMLResponseParser(HostingDotComVAppHandler.class)
   @Override
   Future<? extends HostingDotComVApp> getVApp(@PathParam("vAppId") String appId);

   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vdc/{vDCId}/action/instantiateVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @Consumes(VAPP_XML)
   // required for hosting.com to operate
   @XMLResponseParser(HostingDotComVAppHandler.class)
   @MapBinder(BindInstantiateVAppTemplateParamsToXmlPayload.class)
   @Override
   Future<? extends HostingDotComVApp> instantiateVAppTemplateInVDC(
            @PathParam("vDCId") String vDCId, @MapPayloadParam("name") String appName,
            @MapPayloadParam("template") @ParamParser(VAppTemplateIdToUri.class) String templateId,
            InstantiateVAppTemplateOptions... options);
}
