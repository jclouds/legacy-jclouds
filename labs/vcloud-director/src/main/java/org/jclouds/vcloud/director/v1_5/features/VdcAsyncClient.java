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
package org.jclouds.vcloud.director.v1_5.features;
 
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.CaptureVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.CloneMediaParams;
import org.jclouds.vcloud.director.v1_5.domain.CloneVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.CloneVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.ComposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.InstantiateVAppParamsType;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.URISupplier;
import org.jclouds.vcloud.director.v1_5.domain.UploadVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xx;
import org.jclouds.vcloud.director.v1_5.functions.URISupplierToEndpoint;

import com.google.common.util.concurrent.ListenableFuture;
 
/**
 
 * @see VdcClient
 * @author danikov
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface VdcAsyncClient {
 
   /**
    * @see VdcClient#getVdc(URISupplier)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Vdc> getVdc(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier vdcRef);
   
   /**
    * @see VdcClient#captureVApp(URISupplier, CaptureVAppParams)
    */
   @POST
   @Path("/action/captureVApp")
   @Consumes(VCloudDirectorMediaType.V_APP_TEMPLATE)
   @Produces(VCloudDirectorMediaType.CAPTURE_VAPP_PARAMS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<VAppTemplate> captureVApp(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier vdcRef,
         @BinderParam(BindToXMLPayload.class) CaptureVAppParams params);
   
   /**
    * @see VdcClient#cloneMedia(URISupplier, CloneMediaParams)
    */
   @POST
   @Path("/action/cloneMedia")
   @Consumes(VCloudDirectorMediaType.MEDIA)
   @Produces(VCloudDirectorMediaType.CLONE_MEDIA_PARAMS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Media> cloneMedia(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier vdcRef,
         @BinderParam(BindToXMLPayload.class) CloneMediaParams params);
   
   /**
    * @see VdcClient#cloneVApp(URISupplier, CloneVAppParams)
    */
   @POST
   @Path("/action/cloneVApp")
   @Consumes(VCloudDirectorMediaType.V_APP)
   @Produces(VCloudDirectorMediaType.CLONE_V_APP_PARAMS) //TODO fix these etc.
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<VApp> cloneVApp(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier vdcRef,
         @BinderParam(BindToXMLPayload.class) CloneVAppParams params);
   
   /**
    * @see VdcClient#cloneVAppTemplate(URISupplier, CloneVAppTemplateParams)
    */
   @POST
   @Path("/action/cloneVAppTemplate")
   @Consumes(VCloudDirectorMediaType.V_APP_TEMPLATE)
   @Produces(VCloudDirectorMediaType.CLONE_V_APP_TEMPLATE_PARAMS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<VAppTemplate> cloneVAppTemplate(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier vdcRef,
         @BinderParam(BindToXMLPayload.class) CloneVAppTemplateParams params);
   
   /**
    * @see VdcClient#composeVApp(URISupplier, ComposeVAppParams)
    */
   @POST
   @Path("/action/composeVApp")
   @Consumes(VCloudDirectorMediaType.V_APP)
   @Produces(VCloudDirectorMediaType.COMPOSE_VAPP_PARAMS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<VApp> composeVApp(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier vdcRef,
         @BinderParam(BindToXMLPayload.class) ComposeVAppParams params);
   
   /**
    * @see VdcClient#instantiateVApp(URISupplier, InstantiateVAppParamsType)
    */
   @POST
   @Path("/action/instantiateVApp")
   @Consumes(VCloudDirectorMediaType.V_APP)
   @Produces(VCloudDirectorMediaType.INSTANTIATE_VAPP_TEMPLATE_PARAMS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<VApp> instantiateVApp(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier vdcRef,
         @BinderParam(BindToXMLPayload.class) InstantiateVAppParamsType<?> params);
   
   /**
    * @see VdcClient#uploadVAppTemplate(URISupplier, UploadVAppTemplateParams)
    */
   @POST
   @Path("/action/uploadVAppTemplate")
   @Consumes(VCloudDirectorMediaType.V_APP_TEMPLATE)
   @Produces(VCloudDirectorMediaType.UPLOAD_VAPP_TEMPLATE_PARAMS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<VAppTemplate> uploadVAppTemplate(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier vdcRef,
         @BinderParam(BindToXMLPayload.class) UploadVAppTemplateParams params);
   
   /**
    * @see VdcClient#createMedia(URISupplier, Media)
    */
   @POST
   @Path("/media")
   @Consumes(VCloudDirectorMediaType.MEDIA)
   @Produces(VCloudDirectorMediaType.MEDIA)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Media> createMedia(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier vdcRef,
         @BinderParam(BindToXMLPayload.class) Media media);
    
   /**
    * @see VdcClient#getMetadata(URISupplier)
    */
   @GET
   @Path("/metadata")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Metadata> getMetadata(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier vdcRef);
 
   /**
    * @see VdcClient#getMetadataEntry(URISupplier, String)
    */
   @GET
   @Path("/metadata/{key}")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<MetadataValue> getMetadataValue(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier vdcRef ,
         @PathParam("key") String key);
    
}