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
package org.jclouds.openstack.swift;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.functions.ReturnFalseOnContainerNotFound;
import org.jclouds.blobstore.functions.ReturnFalseOnKeyNotFound;
import org.jclouds.blobstore.functions.ReturnNullOnKeyNotFound;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.options.GetOptions;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.binders.BindSwiftObjectMetadataToRequest;
import org.jclouds.openstack.swift.domain.AccountMetadata;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.openstack.swift.functions.ObjectName;
import org.jclouds.openstack.swift.functions.ParseAccountMetadataResponseFromHeaders;
import org.jclouds.openstack.swift.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.openstack.swift.functions.ParseObjectInfoFromHeaders;
import org.jclouds.openstack.swift.functions.ParseObjectInfoListFromJsonResponse;
import org.jclouds.openstack.swift.functions.ReturnTrueOn404FalseOn409;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;

/**
 * Common features between OpenStack Swift and CloudFiles
 * 
 * @see CommonSwiftClient
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090812.pdf" />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
@Endpoint(Storage.class)
public interface CommonSwiftAsyncClient {
   @Provides
   SwiftObject newSwiftObject();

   /**
    * @see CommonSwiftClient#getAccountStatistics
    */
   @HEAD
   @ResponseParser(ParseAccountMetadataResponseFromHeaders.class)
   @Path("/")
   ListenableFuture<AccountMetadata> getAccountStatistics();

   /**
    * @see CommonSwiftClient#listContainers
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/")
   ListenableFuture<? extends Set<ContainerMetadata>> listContainers(ListContainerOptions... options);

   /**
    * @see CommonSwiftClient#setObjectInfo
    */
   @POST
   @Path("/{container}/{name}")
   ListenableFuture<Boolean> setObjectInfo(@PathParam("container") String container, @PathParam("name") String name,
            @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> userMetadata);

   /**
    * @see CommonSwiftClient#createContainer
    */
   @PUT
   @Path("/{container}")
   ListenableFuture<Boolean> createContainer(@PathParam("container") String container);

   /**
    * @see CommonSwiftClient#deleteContainerIfEmpty
    */
   @DELETE
   @ExceptionParser(ReturnTrueOn404FalseOn409.class)
   @Path("/{container}")
   ListenableFuture<Boolean> deleteContainerIfEmpty(@PathParam("container") String container);

   /**
    * @see CommonSwiftClient#listObjects
    */
   @GET
   @QueryParams(keys = "format", values = "json")
   @ResponseParser(ParseObjectInfoListFromJsonResponse.class)
   @Path("/{container}")
   ListenableFuture<PageSet<ObjectInfo>> listObjects(@PathParam("container") String container,
            ListContainerOptions... options);

   /**
    * @see CommonSwiftClient#containerExists
    */
   @HEAD
   @Path("/{container}")
   @ExceptionParser(ReturnFalseOnContainerNotFound.class)
   ListenableFuture<Boolean> containerExists(@PathParam("container") String container);

   /**
    * @see CommonSwiftClient#putObject
    */
   @PUT
   @Path("/{container}/{name}")
   @ResponseParser(ParseETagHeader.class)
   ListenableFuture<String> putObject(
            @PathParam("container") String container,
            @PathParam("name") @ParamParser(ObjectName.class) @BinderParam(BindSwiftObjectMetadataToRequest.class) SwiftObject object);

   /**
    * @see CommonSwiftClient#getObject
    */
   @GET
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @ExceptionParser(ReturnNullOnKeyNotFound.class)
   @Path("/{container}/{name}")
   ListenableFuture<SwiftObject> getObject(@PathParam("container") String container, @PathParam("name") String name,
            GetOptions... options);

   /**
    * @see CommonSwiftClient#getObjectInfo
    */
   @HEAD
   @ResponseParser(ParseObjectInfoFromHeaders.class)
   @ExceptionParser(ReturnNullOnKeyNotFound.class)
   @Path("/{container}/{name}")
   ListenableFuture<MutableObjectInfoWithMetadata> getObjectInfo(@PathParam("container") String container,
            @PathParam("name") String name);

   /**
    * @see CommonSwiftClient#objectExists
    */
   @HEAD
   @ExceptionParser(ReturnFalseOnKeyNotFound.class)
   @Path("/{container}/{name}")
   ListenableFuture<Boolean> objectExists(@PathParam("container") String container, @PathParam("name") String name);

   /**
    * @see CommonSwiftClient#removeObject
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("/{container}/{name}")
   ListenableFuture<Void> removeObject(@PathParam("container") String container, @PathParam("name") String name);

   @PUT
   @Path("/{container}/{name}")
   @ResponseParser(ParseETagHeader.class)
   @Headers(keys = "X-Object-Manifest", values="{container}/{name}")
   ListenableFuture<String> putObjectManifest(@PathParam("container") String container,
                                            @PathParam("name") String name);

}
