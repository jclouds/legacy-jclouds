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

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.href.CatalogItemURNToHref;
import org.jclouds.vcloud.director.v1_5.functions.href.CatalogURNToHref;

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
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Catalog> get(@EndpointParam(parser = CatalogURNToHref.class) String catalogUrn);

   /**
    * @see CatalogApi#addItem(String, CatalogItem)
    */
   @POST
   @Path("/catalogItems")
   @Consumes(VCloudDirectorMediaType.CATALOG_ITEM)
   @Produces(VCloudDirectorMediaType.CATALOG_ITEM)
   @JAXBResponseParser
   ListenableFuture<CatalogItem> addItem(@EndpointParam(parser = CatalogURNToHref.class) String catalogUrn,
            @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);

   /**
    * @see CatalogApi#getItem(String)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<CatalogItem> getItem(@EndpointParam(parser = CatalogItemURNToHref.class) String catalogItemUrn);

   /**
    * @see CatalogApi#editItem(String, CatalogItem)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.CATALOG_ITEM)
   @Produces(VCloudDirectorMediaType.CATALOG_ITEM)
   @JAXBResponseParser
   ListenableFuture<CatalogItem> editItem(@EndpointParam(parser = CatalogItemURNToHref.class) String catalogItemUrn,
            @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);

   /**
    * @see CatalogApi#removeItem(String)
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> removeItem(@EndpointParam(parser = CatalogItemURNToHref.class) String catalogItemUrn);

   /**
    * @see CatalogApi#get(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
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
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
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

   /**
    * @return asynchronous access to {@link Metadata.Readable} features
    */
   @Delegate
   MetadataAsyncApi.Readable getMetadataApi(@EndpointParam(parser = CatalogURNToHref.class) String catalogUrn);
   
   @Delegate
   MetadataAsyncApi.Readable getMetadataApi(@EndpointParam URI catalogItemHref);

   /**
    * @see CatalogApi#getItemMetadataApi
    */
   @Delegate
   MetadataAsyncApi.Writeable getItemMetadataApi(@EndpointParam(parser = CatalogItemURNToHref.class) String catalogItemUrn);

   @Delegate
   MetadataAsyncApi.Writeable getItemMetadataApi(@EndpointParam URI catalogItemHref);

}
