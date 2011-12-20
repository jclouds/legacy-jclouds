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
package org.jclouds.hpcloud.objectstorage.lvs;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.blobstore.functions.ReturnNullOnContainerNotFound;
import org.jclouds.hpcloud.objectstorage.lvs.functions.ParseContainerMetadataFromHeaders;
import org.jclouds.hpcloud.objectstorage.lvs.options.CreateContainerOptions;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.Storage;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to HP Cloud Object StorageLasVegas via the REST API.
 * 
 * <p/>All commands return a ListenableFuture of the result. Any exceptions incurred
 * during processing will be wrapped in an {@link ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 * 
 * @see HPCloudObjectStorageLasVegasClient
 * @see <a href="https://manage.hpcloud.com/pages/build/docs/objectstorage-lvs/api">HP Cloud Object StorageLasVegas API</a>
 * @author Jeremy Daggett
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
@Endpoint(Storage.class)
public interface HPCloudObjectStorageLasVegasAsyncClient extends CommonSwiftAsyncClient {

   /**
    * @see HPCloudObjectStorageLasVegasClient#getCDNMetadata(String)
    */ 
   @Beta
   @HEAD
   @ResponseParser(ParseContainerMetadataFromHeaders.class)
   @ExceptionParser(ReturnNullOnContainerNotFound.class)
   @Path("/{container}")
   ListenableFuture<ContainerMetadata> getContainerMetadata(@PathParam("container") String container);
   
   /**
    * @see HPCloudObjectStorageLasVegasClient#createContainer
    */
   @PUT
   @Path("/{container}")
   ListenableFuture<Boolean> createContainer(@PathParam("container") String container, 
		    CreateContainerOptions... options);


}
