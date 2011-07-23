/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.trmk.vcloud_0_8;

import static org.jclouds.trmk.vcloud_0_8.VCloudMediaType.CATALOGITEM_XML;
import static org.jclouds.trmk.vcloud_0_8.VCloudMediaType.CATALOG_XML;
import static org.jclouds.trmk.vcloud_0_8.VCloudMediaType.NETWORK_XML;
import static org.jclouds.trmk.vcloud_0_8.VCloudMediaType.ORG_XML;
import static org.jclouds.trmk.vcloud_0_8.VCloudMediaType.TASKSLIST_XML;
import static org.jclouds.trmk.vcloud_0_8.VCloudMediaType.TASK_XML;
import static org.jclouds.trmk.vcloud_0_8.VCloudMediaType.VDC_XML;

import java.net.URI;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.trmk.vcloud_0_8.domain.Catalog;
import org.jclouds.trmk.vcloud_0_8.domain.CatalogItem;
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.domain.Task;
import org.jclouds.trmk.vcloud_0_8.domain.TasksList;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.domain.network.OrgNetwork;
import org.jclouds.trmk.vcloud_0_8.filters.SetVCloudTokenCookie;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameAndCatalogNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameAndVDCNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameCatalogNameItemNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameToTasksListEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameVDCNameNetworkNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.xml.CatalogHandler;
import org.jclouds.trmk.vcloud_0_8.xml.CatalogItemHandler;
import org.jclouds.trmk.vcloud_0_8.xml.OrgHandler;
import org.jclouds.trmk.vcloud_0_8.xml.OrgNetworkHandler;
import org.jclouds.trmk.vcloud_0_8.xml.TaskHandler;
import org.jclouds.trmk.vcloud_0_8.xml.TasksListHandler;
import org.jclouds.trmk.vcloud_0_8.xml.VDCHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface CommonVCloudAsyncClient {

   /**
    * @see CommonVCloudClient#getOrg
    */
   @GET
   @XMLResponseParser(OrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends Org> getOrg(@EndpointParam URI orgId);

   /**
    * @see CommonVCloudClient#getOrgNamed
    */
   @GET
   @XMLResponseParser(OrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends Org> findOrgNamed(
            @Nullable @EndpointParam(parser = OrgNameToEndpoint.class) String orgName);

   /**
    * @see CommonVCloudClient#getCatalog
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   ListenableFuture<? extends Catalog> getCatalog(@EndpointParam URI catalogId);

   /**
    * @see CommonVCloudClient#findCatalogInOrgNamed
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   ListenableFuture<? extends Catalog> findCatalogInOrgNamed(
            @Nullable @EndpointParam(parser = OrgNameAndCatalogNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameAndCatalogNameToEndpoint.class) String catalogName);

   /**
    * @see CommonVCloudClient#getCatalogItem
    */
   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends CatalogItem> getCatalogItem(@EndpointParam URI catalogItem);

   /**
    * @see CommonVCloudClient#getCatalogItemInOrg
    */
   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends CatalogItem> findCatalogItemInOrgCatalogNamed(
            @Nullable @EndpointParam(parser = OrgNameCatalogNameItemNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameCatalogNameItemNameToEndpoint.class) String catalogName,
            @EndpointParam(parser = OrgNameCatalogNameItemNameToEndpoint.class) String itemName);

   /**
    * @see CommonVCloudClient#findNetworkInOrgVDCNamed
    */
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(OrgNetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends OrgNetwork> findNetworkInOrgVDCNamed(
            @Nullable @EndpointParam(parser = OrgNameVDCNameNetworkNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameVDCNameNetworkNameToEndpoint.class) String catalogName,
            @EndpointParam(parser = OrgNameVDCNameNetworkNameToEndpoint.class) String networkName);

   /**
    * @see CommonVCloudClient#getNetwork
    */
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(OrgNetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends OrgNetwork> getNetwork(@EndpointParam URI network);

   /**
    * @see CommonVCloudClient#getVDC(URI)
    */
   @GET
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VDC> getVDC(@EndpointParam URI vdc);

   /**
    * @see CommonVCloudClient#findVDCInOrgNamed(String, String)
    */
   @GET
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VDC> findVDCInOrgNamed(
            @Nullable @EndpointParam(parser = OrgNameAndVDCNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameAndVDCNameToEndpoint.class) String vdcName);

   /**
    * @see CommonVCloudClient#getTasksList
    */
   @GET
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends TasksList> getTasksList(@EndpointParam URI tasksListId);

   /**
    * @see CommonVCloudClient#findTasksListInOrgNamed
    */
   @GET
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends TasksList> findTasksListInOrgNamed(
            @Nullable @EndpointParam(parser = OrgNameToTasksListEndpoint.class) String orgName);

   /**
    * @see CommonVCloudClient#getTask
    */
   @GET
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Task> getTask(@EndpointParam URI taskId);

   /**
    * @see CommonVCloudClient#cancelTask
    */
   @POST
   @Path("/action/cancel")
   ListenableFuture<Void> cancelTask(@EndpointParam URI taskId);

}
