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
package org.jclouds.openstack.glance.v1_0.features;

import java.io.InputStream;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;
import org.jclouds.openstack.glance.v1_0.functions.ParseImageDetailsFromHeaders;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Image Services
 * 
 * @see ImageClient
 * @author Adrian Cole
 * @see <a href="http://glance.openstack.org/glanceapi.html">api doc</a>
 * @see <a href="https://github.com/openstack/glance/blob/master/glance/api/v1/images.py">api src</a>
 */
@SkipEncoding( { '/', '=' })
@RequestFilters(AuthenticateRequest.class)
public interface ImageAsyncClient {

   /**
    * @see ImageClient#list
    */
   @GET
   @SelectJson("images")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Image>> list();
   
   /**
    * @see ImageClient#listInDetail
    */
   @GET
   @SelectJson("images")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/detail")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<ImageDetails>> listInDetail();
   
   /**
    * @see ImageClient#show
    */
   @HEAD
   @Path("/images/{id}")
   @ResponseParser(ParseImageDetailsFromHeaders.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ImageDetails> show(@PathParam("id") String id);
   
   /**
    * @see ImageClient#getAsStream
    */
   @GET
   @Path("/images/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<InputStream> getAsStream(@PathParam("id") String id);

//   POST /images -- Store image data and return metadata about the
//   newly-stored image
//   PUT /images/<ID> -- Update image metadata and/or upload image
//   data for a previously-reserved image
//   DELETE /images/<ID> -- Delete the image with id <ID>
}
