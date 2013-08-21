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
package org.jclouds.openstack.swift;

import static com.google.common.net.HttpHeaders.EXPECT;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnContainerNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnKeyNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.NullOnContainerNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.NullOnKeyNotFound;
import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.options.GetOptions;
import org.jclouds.openstack.swift.SwiftFallbacks.TrueOn404FalseOn409;
import org.jclouds.openstack.swift.binders.BindIterableToHeadersWithContainerDeleteMetadataPrefix;
import org.jclouds.openstack.swift.binders.BindMapToHeadersWithContainerMetadataPrefix;
import org.jclouds.openstack.swift.binders.BindSwiftObjectMetadataToRequest;
import org.jclouds.openstack.swift.domain.AccountMetadata;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.openstack.swift.functions.ObjectName;
import org.jclouds.openstack.swift.functions.ParseAccountMetadataResponseFromHeaders;
import org.jclouds.openstack.swift.functions.ParseContainerMetadataFromHeaders;
import org.jclouds.openstack.swift.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.openstack.swift.functions.ParseObjectInfoFromHeaders;
import org.jclouds.openstack.swift.functions.ParseObjectInfoListFromJsonResponse;
import org.jclouds.openstack.swift.options.CreateContainerOptions;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.openstack.swift.reference.SwiftHeaders;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;

/**
 * Common features between OpenStack Swift and CloudFiles
 * 
 * @see CommonSwiftClient
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090812.pdf" />
 * @author Adrian Cole
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CommonSwiftClient.class)} as
 *             {@link CommonSwiftAsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface CommonSwiftAsyncClient extends Closeable {
   @Provides
   SwiftObject newSwiftObject();

   /**
    * @see CommonSwiftClient#getAccountStatistics
    */
   @Named("GetAccountMetadata")
   @HEAD
   @Path("/")
   @Consumes(MediaType.WILDCARD)
   @ResponseParser(ParseAccountMetadataResponseFromHeaders.class)
   ListenableFuture<AccountMetadata> getAccountStatistics();

   /**
    * @see CommonSwiftClient#listContainers
    */
   @Named("ListContainers")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/")
   ListenableFuture<? extends Set<ContainerMetadata>> listContainers(ListContainerOptions... options);

   /**
    * @see CommonSwiftClient#getContainerMetadata
    */
   @Named("GetContainerMetadata")
   @Beta
   @HEAD
   @Path("/{container}")
   @Consumes(MediaType.WILDCARD)
   @ResponseParser(ParseContainerMetadataFromHeaders.class)
   @Fallback(NullOnContainerNotFound.class)
   ListenableFuture<ContainerMetadata> getContainerMetadata(@PathParam("container") String container);

   /**
    * @see CommonSwiftClient#setContainerMetadata
    */
   @Named("UpdateContainerMetadata")
   @POST
   @Path("/{container}")
   @Fallback(FalseOnContainerNotFound.class)
   ListenableFuture<Boolean> setContainerMetadata(@PathParam("container") String container, 
                                                  @BinderParam(BindMapToHeadersWithContainerMetadataPrefix.class) Map<String, String> containerMetadata);

   /**
    * @see CommonSwiftClient#deleteContainerMetadata
    */
   @Named("UpdateContainerMetadata")
   @POST
   @Path("/{container}")
   @Fallback(FalseOnContainerNotFound.class)
   ListenableFuture<Boolean> deleteContainerMetadata(@PathParam("container") String container, 
                                                     @BinderParam(BindIterableToHeadersWithContainerDeleteMetadataPrefix.class) Iterable<String> metadataKeys);

   /**
    * @see CommonSwiftClient#createContainer
    */
   @Named("CreateContainer")
   @PUT
   @Path("/{container}")
   ListenableFuture<Boolean> createContainer(@PathParam("container") String container,
                                             CreateContainerOptions... options);

   /**
    * @see CommonSwiftClient#setObjectInfo
    */
   @Named("UpdateObjectMetadata")
   @POST
   @Path("/{container}/{name}")
   ListenableFuture<Boolean> setObjectInfo(@PathParam("container") String container, 
                                           @PathParam("name") String name,
                                           @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> userMetadata);

   /**
    * @see CommonSwiftClient#createContainer
    */
   @Named("CreateContainer")
   @PUT
   @Path("/{container}")
   ListenableFuture<Boolean> createContainer(@PathParam("container") String container);

   /**
    * @see CommonSwiftClient#deleteContainerIfEmpty
    */
   @Named("DeleteContainer")
   @DELETE
   @Fallback(TrueOn404FalseOn409.class)
   @Path("/{container}")
   ListenableFuture<Boolean> deleteContainerIfEmpty(@PathParam("container") String container);

   /**
    * @see CommonSwiftClient#listObjects
    */
   @Named("ListObjects")
   @GET
   @QueryParams(keys = "format", values = "json")
   @ResponseParser(ParseObjectInfoListFromJsonResponse.class)
   @Path("/{container}")
   ListenableFuture<PageSet<ObjectInfo>> listObjects(@PathParam("container") String container,
                                                     ListContainerOptions... options);

   /**
    * @see CommonSwiftClient#containerExists
    */
   @Named("GetContainerMetadata")
   @HEAD
   @Path("/{container}")
   @Consumes(MediaType.WILDCARD)
   @Fallback(FalseOnContainerNotFound.class)
   ListenableFuture<Boolean> containerExists(@PathParam("container") String container);

   /**
    * @see CommonSwiftClient#putObject
    */
   @Named("PutObject")
   @PUT
   @Path("/{container}/{name}")
   @Headers(keys = EXPECT, values = "100-continue")
   @ResponseParser(ParseETagHeader.class)
   ListenableFuture<String> putObject(@PathParam("container") String container,
                                      @PathParam("name") @ParamParser(ObjectName.class) @BinderParam(BindSwiftObjectMetadataToRequest.class) SwiftObject object);

   /**
    * @see CommonSwiftClient#copyObject
    */
   @Named("CopyObject")
   @PUT
   @Path("/{destinationContainer}/{destinationObject}")
   @Headers(keys = SwiftHeaders.OBJECT_COPY_FROM, values = "/{sourceContainer}/{sourceObject}")
   @Fallback(FalseOnContainerNotFound.class)
   ListenableFuture<Boolean> copyObject(@PathParam("sourceContainer") String sourceContainer,
                                        @PathParam("sourceObject") String sourceObject,
                                        @PathParam("destinationContainer") String destinationContainer,
                                        @PathParam("destinationObject") String destinationObject);

   /**
    * @see CommonSwiftClient#getObject
    */
   @Named("GetObject")
   @GET
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @Fallback(NullOnKeyNotFound.class)
   @Path("/{container}/{name}")
   ListenableFuture<SwiftObject> getObject(@PathParam("container") String container, 
                                           @PathParam("name") String name,
                                           GetOptions... options);

   /**
    * @see CommonSwiftClient#getObjectInfo
    */
   @Named("GetObjectMetadata")
   @HEAD
   @ResponseParser(ParseObjectInfoFromHeaders.class)
   @Fallback(NullOnKeyNotFound.class)
   @Path("/{container}/{name}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<MutableObjectInfoWithMetadata> getObjectInfo(@PathParam("container") String container,
                                                                 @PathParam("name") String name);

   /**
    * @see CommonSwiftClient#objectExists
    */
   @Named("GetObjectMetadata")
   @HEAD
   @Fallback(FalseOnKeyNotFound.class)
   @Path("/{container}/{name}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<Boolean> objectExists(@PathParam("container") String container, 
                                          @PathParam("name") String name);

   /**
    * @see CommonSwiftClient#removeObject
    */
   @Named("RemoveObject")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/{container}/{name}")
   ListenableFuture<Void> removeObject(@PathParam("container") String container, 
                                       @PathParam("name") String name);

   @Named("PutObjectManifest")
   @PUT
   @Path("/{container}/{name}")
   @ResponseParser(ParseETagHeader.class)
   @Headers(keys = "X-Object-Manifest", values="{container}/{name}/")
   ListenableFuture<String> putObjectManifest(@PathParam("container") String container,
                                              @PathParam("name") String name);
}
