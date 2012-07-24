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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see MetadataApi
 * @author danikov
 */
public interface MetadataAsyncApi {

   @RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
   public static interface Readable extends MetadataAsyncApi {

      /**
       * @see MetadataApi.Readable#getMetadata(URISupplier)
       */
      @GET
      @Path("/metadata")
      @Consumes
      @JAXBResponseParser
      @ExceptionParser(ReturnNullOnNotFoundOr404.class)
      ListenableFuture<Metadata> getMetadata(@EndpointParam URI metaDataUri);
      
      /**
       * @see MetadataApi.Readable#getMetadataValue(URI, String)
       */
      @GET
      @Path("/metadata/{key}")
      @Consumes
      @JAXBResponseParser
      @ExceptionParser(ReturnNullOnNotFoundOr404.class)
      ListenableFuture<MetadataValue> getMetadataValue(@EndpointParam URI metaDataUri, @PathParam("key") String key);
   }
   
   @RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
   public static interface Writeable extends Readable {

      /**
       * @see MetadataApi.Writable#mergeMetadata(URI, Metadata))
       */
      @POST
      @Path("/metadata")
      @Consumes(VCloudDirectorMediaType.TASK)
      @Produces(VCloudDirectorMediaType.METADATA)
      @JAXBResponseParser
      ListenableFuture<Task> mergeMetadata(@EndpointParam URI metaDataUri, @BinderParam(BindToXMLPayload.class) Metadata metadata);
      
      /**
       * @see MetadataApi.Writable#setMetadata(URI, String, MetadataEntry))
       */
      @PUT
      @Path("/metadata/{key}")
      @Consumes(VCloudDirectorMediaType.TASK)
      @Produces(VCloudDirectorMediaType.METADATA_VALUE)
      @JAXBResponseParser
      ListenableFuture<Task> setMetadata(@EndpointParam URI metaDataUri,
            @PathParam("key") String key, 
            @BinderParam(BindToXMLPayload.class) MetadataValue metadataValue);
      
      /**
       * @see MetadataApi.Writable#deleteMetadataEntry(URISupplier, String))
       */
       @DELETE
       @Path("/metadata/{key}")
       @Consumes(VCloudDirectorMediaType.TASK)
       @JAXBResponseParser
       ListenableFuture<Task> deleteMetadataEntry(@EndpointParam URI metaDataUri, @PathParam("key") String key);
   }
}
