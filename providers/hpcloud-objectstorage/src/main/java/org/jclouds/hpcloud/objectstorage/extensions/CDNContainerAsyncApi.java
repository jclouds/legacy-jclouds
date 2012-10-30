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
package org.jclouds.hpcloud.objectstorage.extensions;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.functions.ReturnNullOnContainerNotFound;
import org.jclouds.hpcloud.objectstorage.domain.CDNContainer;
import org.jclouds.hpcloud.objectstorage.functions.ParseCDNContainerFromHeaders;
import org.jclouds.hpcloud.objectstorage.functions.ParseCDNUriFromHeaders;
import org.jclouds.hpcloud.objectstorage.options.ListCDNContainerOptions;
import org.jclouds.hpcloud.objectstorage.reference.HPCloudObjectStorageHeaders;
import org.jclouds.hpcloud.services.HPExtensionCDN;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptyFluentIterableOnNotFoundOr404;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to HP Cloud Object Storage via the REST API.
 * 
 * <p/>
 * All commands return a ListenableFuture of the result. Any exceptions incurred during processing
 * will be backend in an {@link java.util.concurrent.ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 * 
 * @see HPCloudObjectStorageApi
 * @see <a href="https://manage.hpcloud.com/pages/build/docs/objectstorage-lvs/api">HP Cloud Object
 *      Storage API</a>
 * @author Jeremy Daggett
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
@Endpoint(HPExtensionCDN.class)
public interface CDNContainerAsyncApi {
   /**
    * @see HPCloudObjectStorageApi#list()
    */
   @Beta
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnEmptyFluentIterableOnNotFoundOr404.class)
   @Path("/")
   ListenableFuture<FluentIterable<CDNContainer>> list();

   /**
    * @see HPCloudObjectStorageApi#list(ListCDNContainerOptions)
    */
   @Beta
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnEmptyFluentIterableOnNotFoundOr404.class)
   @Path("/")
   ListenableFuture<FluentIterable<CDNContainer>> list(ListCDNContainerOptions options);

   /**
    * @see HPCloudObjectStorageApi#get(String)
    */
   @Beta
   @HEAD
   @ResponseParser(ParseCDNContainerFromHeaders.class)
   @ExceptionParser(ReturnNullOnContainerNotFound.class)
   @Path("/{container}")
   ListenableFuture<CDNContainer> get(@PathParam("container") String container);

   /**
    * @see HPCloudObjectStorageApi#enable(String, long)
    */
   @Beta
   @PUT
   @Path("/{container}")
   @Headers(keys = HPCloudObjectStorageHeaders.CDN_ENABLED, values = "True")
   @ResponseParser(ParseCDNUriFromHeaders.class)
   ListenableFuture<URI> enable(@PathParam("container") String container,
            @HeaderParam(HPCloudObjectStorageHeaders.CDN_TTL) long ttl);

   /**
    * @see HPCloudObjectStorageApi#enable(String)
    */
   @Beta
   @PUT
   @Path("/{container}")
   @Headers(keys = HPCloudObjectStorageHeaders.CDN_ENABLED, values = "True")
   @ResponseParser(ParseCDNUriFromHeaders.class)
   ListenableFuture<URI> enable(@PathParam("container") String container);

   /**
    * @see HPCloudObjectStorageApi#update(String, long)
    */
   @Beta
   @POST
   @Path("/{container}")
   @ResponseParser(ParseCDNUriFromHeaders.class)
   ListenableFuture<URI> update(@PathParam("container") String container,
            @HeaderParam(HPCloudObjectStorageHeaders.CDN_TTL) long ttl);

   /**
    * @see HPCloudObjectStorageApi#disable(String)
    */
   @Beta
   @PUT
   @Path("/{container}")
   @Headers(keys = HPCloudObjectStorageHeaders.CDN_ENABLED, values = "False")
   ListenableFuture<Boolean> disable(@PathParam("container") String container);

}
