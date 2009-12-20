/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud;

import static org.jclouds.vcloud.VCloudMediaType.CATALOGITEM_XML;
import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASKSLIST_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASK_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPPTEMPLATE_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPP_XML;
import static org.jclouds.vcloud.VCloudMediaType.VDC_XML;

import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.jclouds.vcloud.binders.BindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TasksList;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.functions.VAppTemplateIdToUri;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.CatalogItemHandler;
import org.jclouds.vcloud.xml.OrgHandler;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.TasksListHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.VAppTemplateHandler;
import org.jclouds.vcloud.xml.VDCHandler;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface VCloudAsyncClient {

   @GET
   @Endpoint(Org.class)
   @Consumes(VCloudMediaType.ORG_XML)
   @XMLResponseParser(OrgHandler.class)
   Future<? extends Organization> getOrganization();

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.Catalog.class)
   @Consumes(CATALOG_XML)
   @XMLResponseParser(CatalogHandler.class)
   Future<? extends Catalog> getCatalog();

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vAppTemplate/{vAppTemplateId}")
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   Future<? extends VAppTemplate> getVAppTemplate(@PathParam("vAppTemplateId") String vAppTemplateId);

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/catalogItem/{catalogItemId}")
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   Future<? extends CatalogItem> getCatalogItem(@PathParam("catalogItemId") String catalogItemId);

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vdc/{vDCId}")
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   Future<? extends VDC> getVDC(@PathParam("vDCId") String vDCId);

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VDC.class)
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   Future<? extends VDC> getDefaultVDC();

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/tasksList/{tasksListId}")
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   Future<? extends TasksList> getTasksList(@PathParam("tasksListId") String tasksListId);

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.TasksList.class)
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   Future<? extends TasksList> getDefaultTasksList();

   @POST
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/action/deploy")
   @XMLResponseParser(TaskHandler.class)
   Future<? extends Task> deployVApp(@PathParam("vAppId") String vAppId);

   @DELETE
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}")
   Future<Void> deleteVApp(@PathParam("vAppId") String vAppId);

   @POST
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/action/undeploy")
   @XMLResponseParser(TaskHandler.class)
   Future<? extends Task> undeployVApp(@PathParam("vAppId") String vAppId);

   /**
    * This call powers on the vApp, as specified in the vApp's ovf:Startup element.
    */
   @POST
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/power/action/powerOn")
   @XMLResponseParser(TaskHandler.class)
   Future<? extends Task> powerOnVApp(@PathParam("vAppId") String vAppId);

   /**
    * This call powers off the vApp, as specified in the vApp's ovf:Startup element.
    */
   @POST
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/power/action/powerOff")
   @XMLResponseParser(TaskHandler.class)
   Future<? extends Task> powerOffVApp(@PathParam("vAppId") String vAppId);

   /**
    * This call shuts down the vApp.
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/power/action/shutdown")
   Future<Void> shutdownVApp(@PathParam("vAppId") String vAppId);

   /**
    * This call resets the vApp.
    */
   @POST
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/power/action/reset")
   @XMLResponseParser(TaskHandler.class)
   Future<? extends Task> resetVApp(@PathParam("vAppId") String vAppId);

   /**
    * This call suspends the vApp.
    */
   @POST
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/power/action/suspend")
   @XMLResponseParser(TaskHandler.class)
   Future<? extends Task> suspendVApp(@PathParam("vAppId") String vAppId);

   @GET
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/task/{taskId}")
   @XMLResponseParser(TaskHandler.class)
   Future<? extends Task> getTask(@PathParam("taskId") String taskId);

   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/task/{taskId}/action/cancel")
   Future<Void> cancelTask(@PathParam("taskId") String taskId);

   @GET
   @Consumes(VAPP_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}")
   @XMLResponseParser(VAppHandler.class)
   Future<? extends VApp> getVApp(@PathParam("vAppId") String appId);

   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vdc/{vDCId}/action/instantiateVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @MapBinder(BindInstantiateVAppTemplateParamsToXmlPayload.class)
   Future<? extends VApp> instantiateVAppTemplateInVDC(@PathParam("vDCId") String vDCId,
            @MapPayloadParam("name") String appName,
            @MapPayloadParam("template") @ParamParser(VAppTemplateIdToUri.class) String templateId,
            InstantiateVAppTemplateOptions... options);
}
