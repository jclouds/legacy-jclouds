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

import org.jclouds.Fallbacks;
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
 * @see <a href="http://communities.vmware.com/community/developer/forums/vcloudapi" />
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface CatalogApi {

   @GET
   @XMLResponseParser(CatalogHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   Catalog getCatalog(@EndpointParam URI catalogId);

   /**
    * returns the catalog in the organization associated with the specified name. Note that both
    * parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @throws NoSuchElementException
    *            if you specified an org or catalog name that isn't present
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   @MapBinder(OrgNameAndCatalogNameToEndpoint.class)
   Catalog findCatalogInOrgNamed(@Nullable @PayloadParam("orgName") String orgName,
                                 @Nullable @PayloadParam("catalogName") String catalogName);

   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   CatalogItem getCatalogItem(@EndpointParam URI catalogItem);

   /**
    * returns the catalog item in the catalog associated with the specified name. Note that the org
    * and catalog parameters can be null to choose default.
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
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(OrgNameCatalogNameItemNameToEndpoint.class)
   CatalogItem findCatalogItemInOrgCatalogNamed(@Nullable @PayloadParam("orgName") String orgName,
                                                @Nullable @PayloadParam("catalogName") String catalogName,
                                                @PayloadParam("itemName") String itemName);

   /**
    * A catalog can contain references to vApp templates and media images that have been uploaded to
    * any vDC in an organization. A vApp template or media image can be listed in at most one
    * catalog.
    * 
    * @param entity
    *           the reference to the vApp templates and media image
    * @param catalog
    *           URI of the catalog to add the resourceEntity from
    * @param name
    *           name of the entry in the catalog
    * 
    * @param options
    *           options such as description or properties
    * @return the new catalog item
    */
   @POST
   @Path("/catalogItems")
   @Consumes(CATALOGITEM_XML)
   @Produces(CATALOGITEM_XML)
   @MapBinder(BindCatalogItemToXmlPayload.class)
   @XMLResponseParser(CatalogItemHandler.class)
   CatalogItem addVAppTemplateOrMediaImageToCatalogAndNameItem(@PayloadParam("Entity") URI entity,
                                                               @EndpointParam URI catalog,
                                                               @PayloadParam("name") String name,
                                                               CatalogItemOptions... options);

   @DELETE
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deleteCatalogItem(@EndpointParam URI href);
}
