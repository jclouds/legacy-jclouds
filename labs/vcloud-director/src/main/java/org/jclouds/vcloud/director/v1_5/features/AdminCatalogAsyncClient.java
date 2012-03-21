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
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.AdminCatalog;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.PublishCatalogParams;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xx;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see AdminCatalogClient
 * @author danikov
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface AdminCatalogAsyncClient extends CatalogAsyncClient {
   
   /**
    * @see AdminClient#createCatalog(URI, AdminCatalog)
    */
   @POST
   @Path("/catalogs")
   @Consumes(VCloudDirectorMediaType.ADMIN_CATALOG)
   @Produces(VCloudDirectorMediaType.ADMIN_CATALOG)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<AdminCatalog> createCatalog(@EndpointParam URI orgRef, 
         @BinderParam(BindToXMLPayload.class) AdminCatalog catalog);

   /**
    * @see AdminClient#getCatalog(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   @Override
   ListenableFuture<AdminCatalog> getCatalog(@EndpointParam URI catalogRef);

   /**
    * @see AdminClient#getCatalog(URI)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.ADMIN_CATALOG)
   @Produces(VCloudDirectorMediaType.ADMIN_CATALOG)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<AdminCatalog> updateCatalog(@EndpointParam URI catalogRef, 
         @BinderParam(BindToXMLPayload.class) AdminCatalog catalog);
   
   /**
    * @see AdminClient#deleteCatalog(URI)
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Void> deleteCatalog(@EndpointParam URI catalogRef);
   
   /**
    * @see AdminClient#getOwner(URI)
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Owner> getOwner(@EndpointParam URI catalogRef);
   
   /**
    * @see AdminClient#setOwner(URI, Owner)
    */
   @PUT
   @Path("/owner")
   @Consumes
   @Produces(VCloudDirectorMediaType.OWNER)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Void> setOwner(@EndpointParam URI catalogRef,
         @BinderParam(BindToXMLPayload.class) Owner newOwner);
   
   /**
    * @see AdminClient#AdminClient(URI, PublishCatalogParams)
    */
   @POST
   @Path("/action/publish")
   @Consumes
   @Produces(VCloudDirectorMediaType.PUBLISH_CATALOG_PARAMS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Void> publishCatalog(@EndpointParam URI catalogRef,
         @BinderParam(BindToXMLPayload.class) PublishCatalogParams params);

   /**
    * @return synchronous access to {@link Metadata.Writeable} features
    */
   @Override
   @Delegate
   MetadataAsyncClient.Writeable getMetadataClient();

}
