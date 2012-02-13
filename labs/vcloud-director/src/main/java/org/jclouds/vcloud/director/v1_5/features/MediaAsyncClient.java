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

import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ReferenceToEndpoint;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xx;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see MediaClient
 * @author danikov
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface MediaAsyncClient {

   /**
    * @see MediaClient#getMedia(ReferenceType)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Media> getMedia(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> mediaRef);
   
   /**
    * @see MediaClient#updateMedia(ReferenceType, Media))
    */
   @PUT
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Task> updateMedia(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> mediaRef, 
         @PayloadParam("Media") Media media);
   
   /**
   * @see MediaClient#deleteMedia(ReferenceType))
   */
   @DELETE
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> deleteMedia(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> mediaRef);
   
   /**
    * @see MediaClient#getOwner(ReferenceType)
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Owner> getOwner(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> mediaRef);
   
   /**
    * @see MediaClient#getMetadata()
    */
   @GET
   @Path("/metadata")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Metadata> getMetadata(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> mediaRef);
   
   /**
    * @see MediaClient#updateMetadata()
    */
   @POST
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Task> updateMetadata(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> mediaRef,
            @PayloadParam("Metadata") Metadata metadata);

   /**
    * @see MediaClient#getMetadataEntry()
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<MetadataEntry> getMetadataEntry(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> mediaRef,
         @PathParam("key") String key);
   
   /**
    * @see MediaClient#updateMedia()
    */
   @PUT
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Task> updateMetadataEntry(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> metaDataRef,
         @PathParam("key") String key, 
         @PayloadParam("MetadataEntry") MetadataEntry metadataEntry);
   
   /**
    * @see MediaClient#deleteMetadataEntry()
    */
    @DELETE
    @Consumes
    @JAXBResponseParser
    ListenableFuture<Void> deleteMetadataEntry(@EndpointParam(parser = ReferenceToEndpoint.class) ReferenceType<?> metaDataRef,
          @PathParam("key") String key);
   
}
