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

package org.jclouds.savvis.vpdc;

import java.net.URI;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.savvis.vpdc.domain.SymphonyVPDCVDC;
import org.jclouds.savvis.vpdc.xml.SymphonyVPDCNetworkHandler;
import org.jclouds.savvis.vpdc.xml.SymphonyVPDCVAppHandler;
import org.jclouds.savvis.vpdc.xml.SymphonyVPDCVDCHandler;
import org.jclouds.vcloud.CommonVCloudClient;
import org.jclouds.vcloud.VCloudExpressAsyncClient;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.binders.BindCloneVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindInstantiateVCloudExpressVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TasksList;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;
import org.jclouds.vcloud.domain.network.OrgNetwork;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.functions.OrgNameAndCatalogNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameAndVDCNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameCatalogNameItemNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameCatalogNameVAppTemplateNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameToTasksListEndpoint;
import org.jclouds.vcloud.functions.OrgNameVDCNameResourceEntityNameToEndpoint;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.CatalogItemHandler;
import org.jclouds.vcloud.xml.OrgHandler;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.TasksListHandler;
import org.jclouds.vcloud.xml.VCloudExpressVAppHandler;
import org.jclouds.vcloud.xml.VCloudExpressVAppTemplateHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Symphony VPDC resources via their REST API.
 * <p/>
 * 
 * @see <a href="TODO PUBLIC DOC REF" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface SymphonyVPDCAsyncClient extends VCloudExpressAsyncClient {

   /**
    * {@inheritDoc}
    */
   @GET
   // no accept header
   @XMLResponseParser(OrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends Org> findOrgNamed(
         @Nullable @EndpointParam(parser = OrgNameToEndpoint.class) String orgName);

   /**
    * {@inheritDoc}
    */

   @GET
   // no accept header
   @XMLResponseParser(OrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends Org> getOrg(@EndpointParam URI orgId);

   /**
    * @see CommonVCloudClient#getVDC(URI)
    */
   @GET
   // no accept header
   @XMLResponseParser(SymphonyVPDCVDCHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends SymphonyVPDCVDC> getVDC(@EndpointParam URI vdc);

   /**
    * @see CommonVCloudClient#findVDCInOrgNamed(String, String)
    */
   @GET
   // no accept header
   @XMLResponseParser(SymphonyVPDCVDCHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends SymphonyVPDCVDC> findVDCInOrgNamed(
         @Nullable @EndpointParam(parser = OrgNameAndVDCNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameAndVDCNameToEndpoint.class) String vdcName);

   /**
    * @see CommonVCloudClient#findNetworkInOrgVDCNamed
    */
   @GET
   // no accept header
   @XMLResponseParser(SymphonyVPDCNetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends OrgNetwork> findNetworkInOrgVDCNamed(
         @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String catalogName,
         @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String networkName);

   /**
    * @see CommonVCloudClient#getNetwork
    */
   @GET
   // no accept header
   @XMLResponseParser(SymphonyVPDCNetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends OrgNetwork> getNetwork(@EndpointParam URI network);

   /**
    * @see VCloudClient#getVApp
    */
   @GET
   // no accept header
   @XMLResponseParser(SymphonyVPDCVAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends VCloudExpressVApp> getVApp(@EndpointParam URI vApp);

   /**
    * @see VCloudClient#getVAppTemplate
    */
   @GET
   // no accept header
   @XMLResponseParser(VCloudExpressVAppTemplateHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends VCloudExpressVAppTemplate> getVAppTemplate(@EndpointParam URI vAppTemplate);

   /**
    * @see VCloudClient#findVAppTemplateInOrgCatalogNamed
    */
   @GET
   // no accept header
   @XMLResponseParser(VCloudExpressVAppTemplateHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends VCloudExpressVAppTemplate> findVAppTemplateInOrgCatalogNamed(
         @Nullable @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String catalogName,
         @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String itemName);

   /**
    * @see VCloudExpressClient#instantiateVAppTemplateInVDC
    */
   @POST
   @Path("/action/instantiateVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   // no accept header
   @XMLResponseParser(VCloudExpressVAppHandler.class)
   @MapBinder(BindInstantiateVCloudExpressVAppTemplateParamsToXmlPayload.class)
   @Override
   ListenableFuture<? extends VCloudExpressVApp> instantiateVAppTemplateInVDC(@EndpointParam URI vdc,
         @PayloadParam("template") URI template,
         @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String appName,
         InstantiateVAppTemplateOptions... options);

   /**
    * @see VCloudExpressClient#cloneVAppInVDC
    */
   @POST
   @Path("/action/cloneVApp")
   @Produces("application/vnd.vmware.vcloud.cloneVAppParams+xml")
   // no accept header
   @XMLResponseParser(TaskHandler.class)
   @MapBinder(BindCloneVAppParamsToXmlPayload.class)
   @Override
   ListenableFuture<? extends Task> cloneVAppInVDC(@EndpointParam URI vdc, @PayloadParam("vApp") URI toClone,
         @PayloadParam("newName") @ParamValidators(DnsNameValidator.class) String newName, CloneVAppOptions... options);

   /**
    * @see VCloudClient#findVAppInOrgVDCNamed
    */
   @GET
   // no accept header
   @XMLResponseParser(VCloudExpressVAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends VCloudExpressVApp> findVAppInOrgVDCNamed(
         @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String catalogName,
         @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String vAppName);

   /**
    * @see CommonVCloudClient#deployVApp
    */
   @POST
   // no accept header
   @Path("/action/deploy")
   @XMLResponseParser(TaskHandler.class)
   @Override
   ListenableFuture<? extends Task> deployVApp(@EndpointParam URI vAppId);

   /**
    * @see CommonVCloudClient#undeployVApp
    */
   @POST
   // no accept header
   @Path("/action/undeploy")
   @XMLResponseParser(TaskHandler.class)
   @Override
   ListenableFuture<? extends Task> undeployVApp(@EndpointParam URI vAppId);

   /**
    * @see CommonVCloudClient#powerOnVApp
    */
   @POST
   // no accept header
   @Path("/power/action/powerOn")
   @XMLResponseParser(TaskHandler.class)
   @Override
   ListenableFuture<? extends Task> powerOnVApp(@EndpointParam URI vAppId);

   /**
    * @see CommonVCloudClient#powerOffVApp
    */
   @POST
   // no accept header
   @Path("/power/action/powerOff")
   @XMLResponseParser(TaskHandler.class)
   @Override
   ListenableFuture<? extends Task> powerOffVApp(@EndpointParam URI vAppId);

   /**
    * @see CommonVCloudClient#resetVApp
    */
   @POST
   // no accept header
   @Path("/power/action/reset")
   @XMLResponseParser(TaskHandler.class)
   @Override
   ListenableFuture<? extends Task> resetVApp(@EndpointParam URI vAppId);

   /**
    * @see CommonVCloudClient#suspendVApp
    */
   @POST
   // no accept header
   @Path("/power/action/suspend")
   @XMLResponseParser(TaskHandler.class)
   @Override
   ListenableFuture<? extends Task> suspendVApp(@EndpointParam URI vAppId);

   /**
    * @see CommonVCloudClient#getCatalog
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   // no accept header
   @Override
   ListenableFuture<? extends Catalog> getCatalog(@EndpointParam URI catalogId);

   /**
    * @see CommonVCloudClient#findCatalogInOrgNamed
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   // no accept header
   @Override
   ListenableFuture<? extends Catalog> findCatalogInOrgNamed(
         @Nullable @EndpointParam(parser = OrgNameAndCatalogNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameAndCatalogNameToEndpoint.class) String catalogName);

   /**
    * @see CommonVCloudClient#getCatalogItem
    */
   @GET
   // no accept header
   @XMLResponseParser(CatalogItemHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends CatalogItem> getCatalogItem(@EndpointParam URI catalogItem);

   /**
    * @see CommonVCloudClient#getCatalogItemInOrg
    */
   @GET
   // no accept header
   @XMLResponseParser(CatalogItemHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends CatalogItem> findCatalogItemInOrgCatalogNamed(
         @Nullable @EndpointParam(parser = OrgNameCatalogNameItemNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameCatalogNameItemNameToEndpoint.class) String catalogName,
         @EndpointParam(parser = OrgNameCatalogNameItemNameToEndpoint.class) String itemName);

   /**
    * @see CommonVCloudClient#getTasksList
    */
   @GET
   // no accept header
   @XMLResponseParser(TasksListHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends TasksList> getTasksList(@EndpointParam URI tasksListId);

   /**
    * @see CommonVCloudClient#findTasksListInOrgNamed
    */
   @GET
   // no accept header
   @XMLResponseParser(TasksListHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends TasksList> findTasksListInOrgNamed(
         @Nullable @EndpointParam(parser = OrgNameToTasksListEndpoint.class) String orgName);

   /**
    * @see CommonVCloudClient#getTask
    */
   @GET
   // no accept header
   @XMLResponseParser(TaskHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends Task> getTask(@EndpointParam URI taskId);

}
