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

import static org.jclouds.vcloud.VCloudMediaType.CATALOGITEM_XML;
import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.vcloud.binders.BindCatalogItemToXmlPayload;
import org.jclouds.vcloud.binders.OrgNameAndCatalogNameToEndpoint;
import org.jclouds.vcloud.binders.OrgNameCatalogNameItemNameToEndpoint;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.options.CatalogItemOptions;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.CatalogItemHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Catalog functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface CatalogAsyncClient {

   /**
    * @see CatalogClient#getCatalog
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   ListenableFuture<Catalog> getCatalog(@EndpointParam URI catalogId);

   /**
    * @see CatalogClient#findCatalogInOrgNamed
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   @MapBinder(OrgNameAndCatalogNameToEndpoint.class)
   ListenableFuture<Catalog> findCatalogInOrgNamed(@Nullable @PayloadParam("orgName") String orgName,
         @Nullable @PayloadParam("catalogName") String catalogName);

   /**
    * @see CatalogClient#getCatalogItem
    */
   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<CatalogItem> getCatalogItem(@EndpointParam URI catalogItem);

   /**
    * @see CatalogClient#getCatalogItemInOrg
    */
   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(OrgNameCatalogNameItemNameToEndpoint.class)
   ListenableFuture<CatalogItem> findCatalogItemInOrgCatalogNamed(@Nullable @PayloadParam("orgName") String orgName,
         @Nullable @PayloadParam("catalogName") String catalogName, @PayloadParam("itemName") String itemName);

   /**
    * @see CatalogClient#addVAppTemplateOrMediaImageToCatalog
    */
   @POST
   @Path("/catalogItems")
   @Consumes(CATALOGITEM_XML)
   @Produces(CATALOGITEM_XML)
   @MapBinder(BindCatalogItemToXmlPayload.class)
   @XMLResponseParser(CatalogItemHandler.class)
   ListenableFuture<CatalogItem> addVAppTemplateOrMediaImageToCatalogAndNameItem(@PayloadParam("Entity") URI entity,
            @EndpointParam URI catalog, @PayloadParam("name") String name, CatalogItemOptions... options);


   /**
    * @see CatalogClient#deleteCatalogItem
    */
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteCatalogItem(@EndpointParam URI href);

}
