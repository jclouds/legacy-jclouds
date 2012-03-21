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
import org.jclouds.vcloud.director.v1_5.domain.CloneMediaParams;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xx;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see MediaClient
 * @author danikov
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface MediaAsyncClient {

   /**
    * @see MediaClient#getMedia(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Media> getMedia(@EndpointParam URI uri);
   
   /**
    * @see MediaClient#createMedia(URI, Media)
    */
   @POST
   @Consumes(VCloudDirectorMediaType.MEDIA)
   @Produces(VCloudDirectorMediaType.MEDIA)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Media> createMedia(@EndpointParam URI link,
         @BinderParam(BindToXMLPayload.class) Media media);
   
   
   /**
    * @see MediaClient#cloneMedia(URI, CloneMediaParams)
    */
   @POST
   @Path("/action/cloneMedia")
   @Consumes(VCloudDirectorMediaType.MEDIA)
   @Produces(VCloudDirectorMediaType.CLONE_MEDIA_PARAMS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Media> cloneMedia(@EndpointParam URI vdcRef,
         @BinderParam(BindToXMLPayload.class) CloneMediaParams params);
   
   /**
    * @see MediaClient#updateMedia(URI, Media))
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.MEDIA)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Task> updateMedia(@EndpointParam URI uri, @BinderParam(BindToXMLPayload.class) Media media);
   
   /**
   * @see MediaClient#deleteMedia(URI))
   */
   @DELETE
   @Consumes(VCloudDirectorMediaType.TASK)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Task> deleteMedia(@EndpointParam URI uri);
   
   /**
    * @see MediaClient#getOwner(URI)
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Owner> getOwner(@EndpointParam URI uri);
   
   /**
   * @return asynchronous access to {@link Metadata.Writeable} features
   */
   @Delegate
   MetadataAsyncClient.Writable getMetadataClient();
}
