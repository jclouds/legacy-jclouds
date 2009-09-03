/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.azure.storage.blob;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.azure.storage.blob.domain.ContainerMetadataList;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.blob.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azure.storage.filters.SharedKeyAuthentication;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.rest.Header;
import org.jclouds.rest.Query;
import org.jclouds.rest.RequestFilters;
import org.jclouds.rest.SkipEncoding;
import org.jclouds.rest.XMLResponseParser;

/**
 * Provides access to Azure Blob via their REST API.
 * <p/>
 * All commands return a Future of the result from Azure Blob. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd135733.aspx" />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(SharedKeyAuthentication.class)
@Header(key = AzureStorageHeaders.VERSION, value = "2009-07-17")
public interface AzureBlobConnection {

   /**
    * The List Containers operation returns a list of the containers under the specified account.
    * <p />
    * The 2009-07-17 version of the List Containers operation times out after 30 seconds.
    * 
    * @param listOptions
    *           controls the number or type of results requested
    * @see ListOptions
    */
   @GET
   @XMLResponseParser(AccountNameEnumerationResultsHandler.class)
   @Path("/")
   @Query(key = "comp", value = "list")
   ContainerMetadataList listContainers(ListOptions... listOptions);

   /**
    * The Create Container operation creates a new container under the specified account. If the
    * container with the same name already exists, the operation fails.
    * <p/>
    * The container resource includes metadata and properties for that container. It does not
    * include a list of the blobs contained by the container.
    * 
    * @see CreateContainerOptions
    * 
    */
   @PUT
   @Path("{container}")
   @Query(key = "restype", value = "container")
   boolean createContainer(@PathParam("container") String container,
            CreateContainerOptions... options);

   /**
    * The Delete Container operation marks the specified container for deletion. The container and
    * any blobs contained within it are later deleted during garbage collection.
    * <p/>
    * When a container is deleted, a container with the same name cannot be created for at least 30
    * seconds; the container may not be available for more than 30 seconds if the service is still
    * processing the request. While the container is being deleted, attempts to create a container
    * of the same name will fail with status code 409 (Conflict), with the service returning
    * additional error information indicating that the container is being deleted. All other
    * operations, including operations on any blobs under the container, will fail with status code
    * 404 (Not Found) while the container is being deleted.
    * 
    */
   @DELETE
   @Path("{container}")
   @Query(key = "restype", value = "container")
   boolean deleteContainer(@PathParam("container") String container);

   /**
    * The root container is a default container that may be inferred from a URL requesting a blob
    * resource. The root container makes it possible to reference a blob from the top level of the
    * storage account hierarchy, without referencing the container name.
    * <p/>
    * The container resource includes metadata and properties for that container. It does not
    * include a list of the blobs contained by the container.
    * 
    * @see CreateContainerOptions
    * 
    */
   @PUT
   @Path("$root")
   @Query(key = "restype", value = "container")
   boolean createRootContainer(CreateContainerOptions... options);// TODO public is not supported!

   /**
    * 
    * @see deleteContainer(String)
    * @see createRootContainer(CreateContainerOptions)
    */
   @DELETE
   @Path("$root")
   @Query(key = "restype", value = "container")
   boolean deleteRootContainer();
}
