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
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.URISupplier;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xx;
import org.jclouds.vcloud.director.v1_5.functions.URISupplierToEndpoint;

import com.google.common.util.concurrent.ListenableFuture;

/**

 * @see NetworkClient
 * @author danikov
 */
public interface MetadataAsyncClient {
   @RequestFilters(AddVCloudAuthorizationToRequest.class)
   public static interface Readable extends MetadataAsyncClient {
      /**
       * @see MetadataClient.Readable#getMetadata(URISupplier)
       */
      @GET
      @Path("/metadata")
      @Consumes
      @JAXBResponseParser
      @ExceptionParser(ThrowVCloudErrorOn4xx.class)
      ListenableFuture<Metadata> getMetadata(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier parentRef);
      
      /**
       * @see MetadataClient.Readable#getMetadataEntry(URISupplier, String)
       */
      @GET
      @Path("/metadata/{key}")
      @Consumes
      @JAXBResponseParser
      @ExceptionParser(ThrowVCloudErrorOn4xx.class)
      ListenableFuture<MetadataValue> getMetadataValue(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier parentRef ,
            @PathParam("key") String key);
   }
   
   @RequestFilters(AddVCloudAuthorizationToRequest.class)
   public static interface Writable extends Readable {
      /**
       * @see MetadataClient.Writable#mergeMetadata(URISupplier, Metadata))
       */
      @POST
      @Path("/metadata")
      @Consumes(VCloudDirectorMediaType.TASK)
      @Produces(VCloudDirectorMediaType.METADATA)
      @JAXBResponseParser
      @ExceptionParser(ThrowVCloudErrorOn4xx.class)
      ListenableFuture<Task> mergeMetadata(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier parentRef,
            @BinderParam(BindToXMLPayload.class) Metadata metadata);

      
      /**
       * @see MetadataClient.Writable#setMetadata(URISupplier, String, MetadataEntry))
       */
      @PUT
      @Path("/metadata/{key}")
      @Consumes(VCloudDirectorMediaType.TASK)
      @Produces(VCloudDirectorMediaType.METADATA_VALUE)
      @JAXBResponseParser
      @ExceptionParser(ThrowVCloudErrorOn4xx.class)
      ListenableFuture<Task> setMetadata(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier metaDataRef,
            @PathParam("key") String key, 
            @BinderParam(BindToXMLPayload.class) MetadataValue metadataValue);
      
      /**
       * @see MetadataClient.Writable#deleteMetadataEntry(URISupplier, String))
       */
       @DELETE
       @Path("/metadata/{key}")
       @Consumes(VCloudDirectorMediaType.TASK)
       @JAXBResponseParser
       @ExceptionParser(ThrowVCloudErrorOn4xx.class)
       ListenableFuture<Task> deleteMetadataEntry(@EndpointParam(parser = URISupplierToEndpoint.class) URISupplier metaDataRef,
             @PathParam("key") String key);
   }
}
