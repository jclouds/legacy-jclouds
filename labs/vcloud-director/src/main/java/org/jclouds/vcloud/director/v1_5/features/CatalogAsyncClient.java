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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ReferenceToEndpoint;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xx;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see CatalogClient
 * @author grkvlt@apache.org
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface CatalogAsyncClient {

   /**
    * Retrieves a catalog.
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Catalog> getCatalog(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> catalogRef);

   /**
    * Creates a catalog item in a catalog.
    */
   @POST
   @Path("/catalogItems")
   @Consumes(VCloudDirectorMediaType.CATALOG_ITEM)
   @Produces(VCloudDirectorMediaType.CATALOG_ITEM)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<CatalogItem> addCatalogItem(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> catalogRef,
         @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);
   
   /**
    * Returns the metadata associated with the catalog.
    */
   @GET
   @Path("/metadata")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Metadata> getCatalogMetadata(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> catalogRef);

   /**
    * Returns the metadata associated with the catalog for the specified key.
    */
   @GET
   @Path("/metadata/{key}")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<MetadataEntry> getCatalogMetadataEntry(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> catalogRef,
         @PathParam("key") String key);

   /**
    * Retrieves a catalog item.
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<CatalogItem> getCatalogItem(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> catalogItemRef);

   /**
    * Modifies a catalog item.
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.CATALOG_ITEM)
   @Produces(VCloudDirectorMediaType.CATALOG_ITEM)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<CatalogItem> updateCatalogItem(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> catalogItemRef,
         @BinderParam(BindToXMLPayload.class)  CatalogItem catalogItem);

   /**
    * Deletes a catalog item.
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Void> deleteCatalogItem(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> catalogItemRef);

   /**
    * Returns the metadata associated with the catalog item.
    */
   @GET
   @Path("/metadata")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Metadata> getCatalogItemMetadata(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> catalogItemRef);

   /**
    * Merges the metadata for a catalog item with the information provided.
    */
   @POST
   @Path("/metadata")
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.METADATA)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Task> mergeCatalogItemMetadata(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> catalogItemRef,
         @BinderParam(BindToXMLPayload.class)  Metadata catalogItemMetadata);

   /**
    * Returns the metadata associated with the catalog item for the specified key.
    */
   @GET
   @Path("/metadata/{key}")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<MetadataEntry> getCatalogItemMetadataEntry(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> catalogItemRef,
         @PathParam("key") String key);

   /**
    * Sets the metadata for the particular key for the catalog item to the value provided.
    */
   @PUT
   @Path("/metadata/{key}")
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.METADATA_VALUE)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Task> setCatalogItemMetadataEntry(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> catalogItemRef,
         @PathParam("key") String key, @BinderParam(BindToXMLPayload.class) MetadataValue metadataValue);

   /**
    * Deletes the metadata for the particular key for the catalog item.
    */
   @DELETE
   @Path("/metadata/{key}")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Task> deleteCatalogItemMetadataEntry(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> catalogItemRef,
         @PathParam("key") String key);
}
