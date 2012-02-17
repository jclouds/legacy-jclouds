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
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
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
    * @see MediaClient#getMedia(Reference)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Media> getMedia(@EndpointParam(parser = ReferenceToEndpoint.class) Reference mediaRef);
   
   /**
    * @see MediaClient#updateMedia(Reference, Media))
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.MEDIA)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Task> updateMedia(@EndpointParam(parser = ReferenceToEndpoint.class) Reference mediaRef, 
         @BinderParam(BindToXMLPayload.class) Media media);
   
   /**
   * @see MediaClient#deleteMedia(Reference))
   */
   @DELETE
   @Consumes(VCloudDirectorMediaType.TASK)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Task> deleteMedia(@EndpointParam(parser = ReferenceToEndpoint.class) Reference mediaRef);
   
   /**
    * @see MediaClient#getOwner(Reference)
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Owner> getOwner(@EndpointParam(parser = ReferenceToEndpoint.class) Reference mediaRef);
   
   /**
    * @see MediaClient#getMetadata(Reference))
    */
   @GET
   @Path("/metadata")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Metadata> getMetadata(@EndpointParam(parser = ReferenceToEndpoint.class) Reference mediaRef);
   
   /**
    * @see MediaClient#mergeMetadata(Reference, Metadata))
    */
   @POST
   @Path("/metadata")
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.METADATA)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Task> mergeMetadata(@EndpointParam(parser = ReferenceToEndpoint.class) Reference mediaRef,
         @BinderParam(BindToXMLPayload.class) Metadata metadata);

   /**
    * @see MediaClient#getMetadataEntry(Reference, String))
    */
   @GET
   @Path("/metadata/{key}")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<MetadataValue> getMetadataValue(@EndpointParam(parser = ReferenceToEndpoint.class) Reference mediaRef,
         @PathParam("key") String key);
   
   /**
    * @see MediaClient#setMetadata(Reference, String, MetadataEntry))
    */
   @PUT
   @Path("/metadata/{key}")
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.METADATA_VALUE)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Task> setMetadata(@EndpointParam(parser = ReferenceToEndpoint.class) Reference metaDataRef,
         @PathParam("key") String key, 
         @BinderParam(BindToXMLPayload.class) MetadataValue metadataValue);
   
   /**
    * @see MediaClient#deleteMetadataEntry(Reference, String))
    */
    @DELETE
    @Path("/metadata/{key}")
    @Consumes(VCloudDirectorMediaType.TASK)
    @JAXBResponseParser
    @ExceptionParser(ThrowVCloudErrorOn4xx.class)
    ListenableFuture<Task> deleteMetadataEntry(@EndpointParam(parser = ReferenceToEndpoint.class) Reference metaDataRef,
          @PathParam("key") String key);
   
}
