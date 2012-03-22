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
package org.jclouds.hpcloud.objectstorage;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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

import org.jclouds.blobstore.functions.ReturnNullOnContainerNotFound;
import org.jclouds.hpcloud.objectstorage.domain.ContainerCDNMetadata;
import org.jclouds.hpcloud.objectstorage.functions.ParseCDNUriFromHeaders;
import org.jclouds.hpcloud.objectstorage.functions.ParseContainerCDNMetadataFromHeaders;
import org.jclouds.hpcloud.objectstorage.functions.ParseContainerMetadataFromHeaders;
import org.jclouds.hpcloud.objectstorage.options.CreateContainerOptions;
import org.jclouds.hpcloud.objectstorage.options.ListCDNContainerOptions;
import org.jclouds.hpcloud.objectstorage.reference.HPCloudObjectStorageHeaders;
import org.jclouds.hpcloud.services.HPExtensionCDN;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.services.ObjectStore;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.Storage;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.functions.ReturnTrueOn404FalseOn409;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to HP Cloud Object Storage via the REST API.
 * 
 * <p/>All commands return a ListenableFuture of the result. Any exceptions incurred
 * during processing will be wrapped in an {@link java.util.concurrent.ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 * 
 * @see HPCloudObjectStorageClient
 * @see <a href="https://manage.hpcloud.com/pages/build/docs/objectstorage-lvs/api">HP Cloud Object Storage API</a>
 * @author Jeremy Daggett
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
@Endpoint(ObjectStore.class)
public interface HPCloudObjectStorageAsyncClient extends CommonSwiftAsyncClient {

   /**
    * @see HPCloudObjectStorageClient#getCDNMetadata(String)
    */ 
   @Beta
   @HEAD
   @ResponseParser(ParseContainerMetadataFromHeaders.class)
   @ExceptionParser(ReturnNullOnContainerNotFound.class)
   @Path("/{container}")
   ListenableFuture<ContainerMetadata> getContainerMetadata(@PathParam("container") String container);
   
   /**
    * @see HPCloudObjectStorageClient#createContainer
    */
   @PUT
   @Path("/{container}")
   ListenableFuture<Boolean> createContainer(@PathParam("container") String container,
                                             CreateContainerOptions... options);

    /**
     * @see org.jclouds.openstack.swift.CommonSwiftClient#listContainers
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @QueryParams(keys = "format", values = "json")
    @Path("/")
    ListenableFuture<? extends Set<ContainerMetadata>> listContainers(ListContainerOptions... options);

  /**
     * @see org.jclouds.openstack.swift.CommonSwiftClient#deleteContainerIfEmpty
     */
    @DELETE
    @ExceptionParser(ReturnTrueOn404FalseOn409.class)
    @Path("/{container}")
    ListenableFuture<Boolean> deleteContainerIfEmpty(@PathParam("container") String container);

   /**
    * @see HPCloudObjectStorageClient#listCDNContainers(ListCDNContainerOptions...)
    */
   @Beta
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/")
   @Endpoint(HPExtensionCDN.class)
   ListenableFuture<? extends Set<ContainerCDNMetadata>> listCDNContainers(ListCDNContainerOptions... options);
   
   /**
    * @see HPCloudObjectStorageClient#getCDNMetadata(String)
    */
   @Beta
   @HEAD
   @ResponseParser(ParseContainerCDNMetadataFromHeaders.class)
   @ExceptionParser(ReturnNullOnContainerNotFound.class)
   @Path("/{container}")
   @Endpoint(HPExtensionCDN.class)
   ListenableFuture<ContainerCDNMetadata> getCDNMetadata(@PathParam("container") String container);

   /**
    * @see HPCloudObjectStorageClient#enableCDN(String, long)
    */
   @Beta
   @PUT
   @Path("/{container}")
   @Headers(keys = HPCloudObjectStorageHeaders.CDN_ENABLED, values = "True")
   @ResponseParser(ParseCDNUriFromHeaders.class)
   @Endpoint(HPExtensionCDN.class)
   ListenableFuture<URI> enableCDN(@PathParam("container") String container,
                                   @HeaderParam(HPCloudObjectStorageHeaders.CDN_TTL) long ttl);

   /**
    * @see HPCloudObjectStorageClient#enableCDN(String)
    */
   @Beta
   @PUT
   @Path("/{container}")
   @Headers(keys = HPCloudObjectStorageHeaders.CDN_ENABLED, values = "True")
   @ResponseParser(ParseCDNUriFromHeaders.class)
   @Endpoint(HPExtensionCDN.class)
   ListenableFuture<URI> enableCDN(@PathParam("container") String container);

   /**
    * @see HPCloudObjectStorageClient#updateCDN(String, long)
    */
   @Beta
   @POST
   @Path("/{container}")
   @ResponseParser(ParseCDNUriFromHeaders.class)
   @Endpoint(HPExtensionCDN.class)
   ListenableFuture<URI> updateCDN(@PathParam("container") String container,
                                   @HeaderParam(HPCloudObjectStorageHeaders.CDN_TTL) long ttl);

   /**
    * @see HPCloudObjectStorageClient#disableCDN(String)
    */
   @Beta
   @PUT
   @Path("/{container}")
   @Headers(keys = HPCloudObjectStorageHeaders.CDN_ENABLED, values = "False")
   @Endpoint(HPExtensionCDN.class)
   ListenableFuture<Boolean> disableCDN(@PathParam("container") String container);

}
