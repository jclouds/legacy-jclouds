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
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see CatalogApi
 * @author grkvlt@apache.org
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface CatalogAsyncApi {

   /**
    * Retrieves a catalog.
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Catalog> getCatalog(@EndpointParam URI catalogUri);

   /**
    * Creates a catalog item in a catalog.
    */
   @POST
   @Path("/catalogItems")
   @Consumes(VCloudDirectorMediaType.CATALOG_ITEM)
   @Produces(VCloudDirectorMediaType.CATALOG_ITEM)
   @JAXBResponseParser
   ListenableFuture<CatalogItem> addCatalogItem(@EndpointParam URI catalogUri,
         @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);
   
   /**
    * Retrieves a catalog item.
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<CatalogItem> getCatalogItem(@EndpointParam URI catalogItemUri);

   /**
    * Modifies a catalog item.
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.CATALOG_ITEM)
   @Produces(VCloudDirectorMediaType.CATALOG_ITEM)
   @JAXBResponseParser
   ListenableFuture<CatalogItem> updateCatalogItem(@EndpointParam URI catalogItemUri,
         @BinderParam(BindToXMLPayload.class)  CatalogItem catalogItem);

   /**
    * Deletes a catalog item.
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> deleteCatalogItem(@EndpointParam URI catalogItemUri);

   /**
    * @return asynchronous access to {@link Metadata.Readable} features
    */
   @Delegate
   MetadataAsyncApi.Readable getMetadataApi();

   /**
    * @return asynchronous access to {@link Metadata.Writeable} features for CatalogItems
    */
   @Delegate
   MetadataAsyncApi.Writeable getCatalogItemMetadataApi();
}
