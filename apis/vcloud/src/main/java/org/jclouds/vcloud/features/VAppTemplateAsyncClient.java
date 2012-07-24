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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.ovf.Envelope;
import org.jclouds.ovf.xml.EnvelopeHandler;
import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.vcloud.binders.BindCaptureVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindCloneVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.functions.OrgNameCatalogNameVAppTemplateNameToEndpoint;
import org.jclouds.vcloud.options.CaptureVAppOptions;
import org.jclouds.vcloud.options.CloneVAppTemplateOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.VAppTemplateHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VAppTemplate functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VAppTemplateAsyncClient {

   /**
    * @see VAppTemplateClient#createVAppInVDCByInstantiatingTemplate
    */
   @POST
   @Path("/action/instantiateVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @MapBinder(BindInstantiateVAppTemplateParamsToXmlPayload.class)
   ListenableFuture<VApp> createVAppInVDCByInstantiatingTemplate(
            @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String appName, @EndpointParam URI vdc,
            @PayloadParam("template") URI template, InstantiateVAppTemplateOptions... options);

   /**
    * @see VAppTemplateClient#getOvfEnvelopeForVAppTemplate
    */
   @GET
   @Consumes(MediaType.TEXT_XML)
   @Path("/ovf")
   @XMLResponseParser(EnvelopeHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Envelope> getOvfEnvelopeForVAppTemplate(@EndpointParam URI href);

   /**
    * @see VAppTemplateClient#captureVAppAsTemplateInVDC
    */
   @POST
   @Path("/action/captureVApp")
   @Produces("application/vnd.vmware.vcloud.captureVAppParams+xml")
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @MapBinder(BindCaptureVAppParamsToXmlPayload.class)
   ListenableFuture<VAppTemplate> captureVAppAsTemplateInVDC(@PayloadParam("vApp") URI toCapture,
            @PayloadParam("templateName") @ParamValidators(DnsNameValidator.class) String templateName,
            @EndpointParam URI vdc, CaptureVAppOptions... options);

   /**
    * @see VAppTemplateClient#copyVAppTemplateToVDCAndName
    */
   @POST
   @Path("/action/cloneVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.cloneVAppTemplateParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @MapBinder(BindCloneVAppTemplateParamsToXmlPayload.class)
   ListenableFuture<Task> copyVAppTemplateToVDCAndName(@PayloadParam("Source") URI sourceVAppTemplate,
            @EndpointParam URI vdc, @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String newName,
            CloneVAppTemplateOptions... options);

   /**
    * @see VAppTemplateClient#moveVAppTemplateToVDCAndRename
    */
   @POST
   @Path("/action/cloneVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.cloneVAppTemplateParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @PayloadParams(keys = "IsSourceDelete", values = "true")
   @MapBinder(BindCloneVAppTemplateParamsToXmlPayload.class)
   ListenableFuture<Task> moveVAppTemplateToVDCAndRename(@PayloadParam("Source") URI toClone,
            @EndpointParam URI vdc, @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String newName,
            CloneVAppTemplateOptions... options);

   /**
    * @see VAppTemplateClient#findVAppTemplateInOrgCatalogNamed
    */
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VAppTemplate> findVAppTemplateInOrgCatalogNamed(
            @Nullable @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String catalogName,
            @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String itemName);

   /**
    * @see VAppTemplateClient#getVAppTemplate
    */
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VAppTemplate> getVAppTemplate(@EndpointParam URI vAppTemplate);

   /**
    * @see VAppTemplateClient#deleteVAppTemplate
    */
   @DELETE
   @Consumes(TASK_XML)
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> deleteVAppTemplate(@EndpointParam URI href);

}
