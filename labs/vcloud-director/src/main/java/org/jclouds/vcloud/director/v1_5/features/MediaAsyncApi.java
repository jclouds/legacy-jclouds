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
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneMediaParams;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.href.MediaURNToHref;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see MediaApi
 * @author danikov, Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface MediaAsyncApi {

   /**
    * @see MediaApi#get(String)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Media> get(@EndpointParam(parser = MediaURNToHref.class) String mediaUrn);

   /**
    * @see MediaApi#add(URI, Media)
    */
   @POST
   @Consumes(VCloudDirectorMediaType.MEDIA)
   @Produces(VCloudDirectorMediaType.MEDIA)
   @JAXBResponseParser
   ListenableFuture<Media> add(@EndpointParam URI updateHref, @BinderParam(BindToXMLPayload.class) Media media);

   /**
    * @see MediaApi#clone(String, CloneMediaParams)
    */
   @POST
   @Path("/action/cloneMedia")
   @Consumes(VCloudDirectorMediaType.MEDIA)
   @Produces(VCloudDirectorMediaType.CLONE_MEDIA_PARAMS)
   @JAXBResponseParser
   ListenableFuture<Media> clone(@EndpointParam(parser = MediaURNToHref.class) String mediaUrn,
            @BinderParam(BindToXMLPayload.class) CloneMediaParams params);

   /**
    * @see MediaApi#editMedia(String, Media)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.MEDIA)
   @JAXBResponseParser
   ListenableFuture<Task> edit(@EndpointParam(parser = MediaURNToHref.class) String mediaUrn,
            @BinderParam(BindToXMLPayload.class) Media media);

   /**
    * @see MediaApi#removeMedia(String)
    */
   @DELETE
   @Consumes(VCloudDirectorMediaType.TASK)
   @JAXBResponseParser
   ListenableFuture<Task> remove(@EndpointParam(parser = MediaURNToHref.class) String mediaUrn);

   /**
    * @see MediaApi#getOwner(String)
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Owner> getOwner(@EndpointParam(parser = MediaURNToHref.class) String mediaUrn);

   /**
    * @see MediaApi#get(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Media> get(@EndpointParam URI mediaHref);

   /**
    * @see MediaApi#clone(URI, CloneMediaParams)
    */
   @POST
   @Path("/action/cloneMedia")
   @Consumes(VCloudDirectorMediaType.MEDIA)
   @Produces(VCloudDirectorMediaType.CLONE_MEDIA_PARAMS)
   @JAXBResponseParser
   ListenableFuture<Media> clone(@EndpointParam URI mediaHref,
            @BinderParam(BindToXMLPayload.class) CloneMediaParams params);

   /**
    * @see MediaApi#editMedia(URI, Media)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.MEDIA)
   @JAXBResponseParser
   ListenableFuture<Task> edit(@EndpointParam URI mediaHref, @BinderParam(BindToXMLPayload.class) Media media);

   /**
    * @see MediaApi#removeMedia(URI)
    */
   @DELETE
   @Consumes(VCloudDirectorMediaType.TASK)
   @JAXBResponseParser
   ListenableFuture<Task> remove(@EndpointParam URI mediaHref);

   /**
    * @see MediaApi#getOwner(URI)
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Owner> getOwner(@EndpointParam URI mediaHref);

   /**
    * @return asynchronous access to {@link Metadata.Writeable} features
    */
   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi(@EndpointParam(parser = MediaURNToHref.class) String mediaUrn);

   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi(@EndpointParam URI mediaHref);
}
