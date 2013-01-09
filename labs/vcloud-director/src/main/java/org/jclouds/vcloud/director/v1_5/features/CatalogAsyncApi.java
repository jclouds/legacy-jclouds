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

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.URNToHref;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see CatalogApi
 * @author grkvlt@apache.org, Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface CatalogAsyncApi {

   /**
    * @see CatalogApi#get(String)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Catalog> get(@EndpointParam(parser = URNToHref.class) String catalogUrn);

   /**
    * @see CatalogApi#addItem(String, CatalogItem)
    */
   @POST
   @Path("/catalogItems")
   @Consumes(VCloudDirectorMediaType.CATALOG_ITEM)
   @Produces(VCloudDirectorMediaType.CATALOG_ITEM)
   @JAXBResponseParser
   ListenableFuture<CatalogItem> addItem(@EndpointParam(parser = URNToHref.class) String catalogUrn,
            @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);

   /**
    * @see CatalogApi#getItem(String)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<CatalogItem> getItem(@EndpointParam(parser = URNToHref.class) String catalogItemUrn);

   /**
    * @see CatalogApi#editItem(String, CatalogItem)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.CATALOG_ITEM)
   @Produces(VCloudDirectorMediaType.CATALOG_ITEM)
   @JAXBResponseParser
   ListenableFuture<CatalogItem> editItem(@EndpointParam(parser = URNToHref.class) String catalogItemUrn,
            @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);

   /**
    * @see CatalogApi#removeItem(String)
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> removeItem(@EndpointParam(parser = URNToHref.class) String catalogItemUrn);

   /**
    * @see CatalogApi#get(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Catalog> get(@EndpointParam URI catalogHref);

   /**
    * @see CatalogApi#addItem(URI, CatalogItem)
    */
   @POST
   @Path("/catalogItems")
   @Consumes(VCloudDirectorMediaType.CATALOG_ITEM)
   @Produces(VCloudDirectorMediaType.CATALOG_ITEM)
   @JAXBResponseParser
   ListenableFuture<CatalogItem> addItem(@EndpointParam URI catalogHref,
            @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);

   /**
    * @see CatalogApi#getItem(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<CatalogItem> getItem(@EndpointParam URI catalogItemHref);

   /**
    * @see CatalogApi#editItem(URI, CatalogItem)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.CATALOG_ITEM)
   @Produces(VCloudDirectorMediaType.CATALOG_ITEM)
   @JAXBResponseParser
   ListenableFuture<CatalogItem> editItem(@EndpointParam URI catalogItemHref,
            @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);

   /**
    * @see CatalogApi#removeItem(URI)
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> removeItem(@EndpointParam URI catalogItemHref);
}
