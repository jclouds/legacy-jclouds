/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudfiles;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.BlobStoreFallbacks.NullOnContainerNotFound;
import org.jclouds.cloudfiles.binders.BindIterableToHeadersWithPurgeCDNObjectEmail;
import org.jclouds.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.cloudfiles.functions.ParseCdnUriFromHeaders;
import org.jclouds.cloudfiles.functions.ParseContainerCDNMetadataFromHeaders;
import org.jclouds.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.cloudfiles.reference.CloudFilesHeaders;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.Storage;
import org.jclouds.openstack.swift.SwiftAsyncClient;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Cloud Files via their REST API.
 * <p/>
 * All commands return a ListenableFuture of the result from Cloud Files. Any exceptions incurred
 * during processing will be backend in an {@link ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 *
 * @see CloudFilesClient
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090812.pdf" />
 * @author Adrian Cole
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CloudFilesClient.class)} as
 *             {@link CloudFilesAsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
@RequestFilters(AuthenticateRequest.class)
@Endpoint(Storage.class)
public interface CloudFilesAsyncClient extends SwiftAsyncClient {

   /**
    * @see CloudFilesClient#listCDNContainers
    */
   @Named("ListCDNEnabledContainers")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/")
   @Endpoint(CDNManagement.class)
   ListenableFuture<? extends Set<ContainerCDNMetadata>> listCDNContainers(ListCdnContainerOptions... options);

   /**
    * @see CloudFilesClient#getCDNMetadata
    */
   @Named("ListCDNEnabledContainerMetadata")
   @HEAD
   @ResponseParser(ParseContainerCDNMetadataFromHeaders.class)
   @Fallback(NullOnContainerNotFound.class)
   @Path("/{container}")
   @Endpoint(CDNManagement.class)
   ListenableFuture<ContainerCDNMetadata> getCDNMetadata(@PathParam("container") String container);

   /**
    * @see CloudFilesClient#enableCDN(String, long, boolean);
    */
   @Named("CDNEnableContainer")
   @PUT
   @Path("/{container}")
   @Headers(keys = CloudFilesHeaders.CDN_ENABLED, values = "True")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CDNManagement.class)
   ListenableFuture<URI> enableCDN(@PathParam("container") String container,
                                   @HeaderParam(CloudFilesHeaders.CDN_TTL) long ttl,
                                   @HeaderParam(CloudFilesHeaders.CDN_LOG_RETENTION) boolean logRetention);

   /**
    * @see CloudFilesClient#enableCDN(String, long);
    */
   @Named("CDNEnableContainer")
   @PUT
   @Path("/{container}")
   @Headers(keys = CloudFilesHeaders.CDN_ENABLED, values = "True")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CDNManagement.class)
   ListenableFuture<URI> enableCDN(@PathParam("container") String container,
                                   @HeaderParam(CloudFilesHeaders.CDN_TTL) long ttl);

   /**
    * @see CloudFilesClient#enableCDN(String)
    */
   @Named("CDNEnableContainer")
   @PUT
   @Path("/{container}")
   @Headers(keys = CloudFilesHeaders.CDN_ENABLED, values = "True")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CDNManagement.class)
   ListenableFuture<URI> enableCDN(@PathParam("container") String container);

   /**
    * @see CloudFilesClient#updateCDN(long, boolean)
    */
   @Named("UpdateCDNEnabledContainerMetadata")
   @POST
   @Path("/{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CDNManagement.class)
   ListenableFuture<URI> updateCDN(@PathParam("container") String container,
                                   @HeaderParam(CloudFilesHeaders.CDN_TTL) long ttl,
                                   @HeaderParam(CloudFilesHeaders.CDN_LOG_RETENTION) boolean logRetention);

   /**
    * @see CloudFilesClient#updateCDN(boolean)
    */
   @Named("UpdateCDNEnabledContainerMetadata")
   @POST
   @Path("/{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CDNManagement.class)
   ListenableFuture<URI> updateCDN(@PathParam("container") String container,
                                   @HeaderParam(CloudFilesHeaders.CDN_LOG_RETENTION) boolean logRetention);

   /**
    * @see CloudFilesClient#updateCDN(long)
    */
   @Named("UpdateCDNEnabledContainerMetadata")
   @POST
   @Path("/{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CDNManagement.class)
   ListenableFuture<URI> updateCDN(@PathParam("container") String container,
                                   @HeaderParam(CloudFilesHeaders.CDN_TTL) long ttl);

   /**
    * @see CloudFilesClient#disableCDN
    */
   @Named("DisableCDNEnabledContainer")
   @POST
   @Path("/{container}")
   @Headers(keys = CloudFilesHeaders.CDN_ENABLED, values = "False")
   @Endpoint(CDNManagement.class)
   ListenableFuture<Boolean> disableCDN(@PathParam("container") String container);

   /**
    * @see CloudFilesClient#purgeCDNObject(String, String, Iterable)
    */
   @Named("PurgeCDNEnabledObject")
   @DELETE
   @Path("/{container}/{object}")
   @Headers(keys = CloudFilesHeaders.CDN_CONTAINER_PURGE_OBJECT_EMAIL, values = "{email}")
   @Endpoint(CDNManagement.class)
   ListenableFuture<Boolean> purgeCDNObject(@PathParam("container") String container, 
                                            @PathParam("object") String object,
                                            @BinderParam(BindIterableToHeadersWithPurgeCDNObjectEmail.class) Iterable<String> emails);

   /**
    * @see CloudFilesClient#purgeCDNObject(String, String)
    */
   @Named("PurgeCDNEnabledObject")
   @DELETE
   @Path("/{container}/{object}")
   @Endpoint(CDNManagement.class)
   ListenableFuture<Boolean> purgeCDNObject(@PathParam("container") String container, 
                                            @PathParam("object") String object);

   /**
    * @see CloudFilesClient#setCDNStaticWebsiteIndex
    */
   @Named("UpdateCDNEnabledContainerMetadata")
   @POST
   @Path("/{container}")
   @Headers(keys = CloudFilesHeaders.CDN_WEBSITE_INDEX, values = "{index}")
   ListenableFuture<Boolean> setCDNStaticWebsiteIndex(@PathParam("container") String container,
                                                      @PathParam("index") String index);

   /**
    * @see CloudFilesClient#setCDNStaticWebsiteError
    */
   @Named("UpdateCDNEnabledContainerMetadata")
   @POST
   @Path("/{container}")
   @Headers(keys = CloudFilesHeaders.CDN_WEBSITE_ERROR, values = "{error}")
   ListenableFuture<Boolean> setCDNStaticWebsiteError(@PathParam("container") String container,
                                                      @PathParam("error") String error);
}
