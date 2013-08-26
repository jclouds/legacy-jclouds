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
package org.jclouds.vcloud.features;

import static org.jclouds.vcloud.VCloudMediaType.TASK_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPPTEMPLATE_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPP_XML;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.ovf.Envelope;
import org.jclouds.ovf.xml.EnvelopeHandler;
import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.vcloud.binders.BindCaptureVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindCloneVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.binders.OrgNameCatalogNameVAppTemplateNameToEndpoint;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.options.CaptureVAppOptions;
import org.jclouds.vcloud.options.CloneVAppTemplateOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.VAppTemplateHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VApp Template functionality in vCloud
 * <p/>
 * 
 * @see <a href="http://communities.vmware.com/community/developer/forums/vcloudapi" />
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VAppTemplateApi {
   /**
    * returns the vapp template corresponding to a catalog item in the catalog associated with the
    * specified name. Note that the org and catalog parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @param itemName
    *           item you wish to lookup
    * 
    * @throws NoSuchElementException
    *            if you specified an org, catalog, or catalog item name that isn't present
    */
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(OrgNameCatalogNameVAppTemplateNameToEndpoint.class)
   VAppTemplate findVAppTemplateInOrgCatalogNamed(@Nullable @PayloadParam("orgName") String orgName,
                                                  @Nullable @PayloadParam("catalogName") String catalogName,
                                                  @PayloadParam("itemName") String itemName);

   /**
    */
   @POST
   @Path("/action/instantiateVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @MapBinder(BindInstantiateVAppTemplateParamsToXmlPayload.class)
   VApp createVAppInVDCByInstantiatingTemplate(
           @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String appName, @EndpointParam URI vdc,
           @PayloadParam("template") URI template, InstantiateVAppTemplateOptions... options);

   @POST
   @Path("/action/cloneVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.cloneVAppTemplateParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @MapBinder(BindCloneVAppTemplateParamsToXmlPayload.class)
   Task copyVAppTemplateToVDCAndName(@PayloadParam("Source") URI sourceVAppTemplate,
                                     @EndpointParam URI vdc, @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String newName,
                                     CloneVAppTemplateOptions... options);

   @POST
   @Path("/action/cloneVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.cloneVAppTemplateParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @PayloadParams(keys = "IsSourceDelete", values = "true")
   @MapBinder(BindCloneVAppTemplateParamsToXmlPayload.class)
   Task moveVAppTemplateToVDCAndRename(@PayloadParam("Source") URI toClone,
                                       @EndpointParam URI vdc, @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String newName,
                                       CloneVAppTemplateOptions... options);

   /**
    * The captureVApp request creates a vApp template from an instantiated vApp. <h4>Note</h4>
    * Before it can be captured, a vApp must be undeployed
    * 
    * @param targetVdcHref
    * @param sourceVAppHref
    * @param newTemplateName
    * @param options
    * @return template in progress
    */
   @POST
   @Path("/action/captureVApp")
   @Produces("application/vnd.vmware.vcloud.captureVAppParams+xml")
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @MapBinder(BindCaptureVAppParamsToXmlPayload.class)
   VAppTemplate captureVAppAsTemplateInVDC(@PayloadParam("vApp") URI toCapture,
                                           @PayloadParam("templateName") @ParamValidators(DnsNameValidator.class) String templateName,
                                           @EndpointParam URI vdc, CaptureVAppOptions... options);

   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VAppTemplate getVAppTemplate(@EndpointParam URI vAppTemplate);

   @GET
   @Consumes(MediaType.TEXT_XML)
   @Path("/ovf")
   @XMLResponseParser(EnvelopeHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Envelope getOvfEnvelopeForVAppTemplate(@EndpointParam URI href);

   /**
    * delete a vAppTemplate, vApp, or media image. You cannot delete an object if it is in use. Any
    * object that is being copied or moved is in use. Other criteria that determine whether an
    * object is in use depend on the object type.
    * <ul>
    * <li>A vApptemplate is in use if it is being instantiated. After instantiation is complete, the
    * template is no longer in use.</li>
    * <li>A vApp is in use if it is deployed.</li>
    * <li>A media image is in use if it is inserted in a Vm.</li>
    * </ul>
    * 
    * @param id
    *           href of the vApp
    * @return task of the operation in progress
    */
   @DELETE
   @Consumes(TASK_XML)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   @XMLResponseParser(TaskHandler.class)
   Task deleteVAppTemplate(@EndpointParam URI href);
}
