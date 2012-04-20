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
package org.jclouds.openstack.swift.v1.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.v1.domain.AccountMetadata;
import org.jclouds.openstack.swift.v1.domain.ContainerMetadata;
import org.jclouds.openstack.swift.v1.functions.ParseAccountMetadataResponseFromHeaders;
import org.jclouds.openstack.swift.v1.options.ListContainersOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Storage Account Services
 * 
 * @see AccountClient
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/storage-account-services.html"
 *      >api doc</a>
 */
@SkipEncoding( { '/', '=' })
@RequestFilters(AuthenticateRequest.class)
public interface AccountAsyncClient {

   /**
    * @see AccountClient#getAccountMetadata
    */
   @HEAD
   @ResponseParser(ParseAccountMetadataResponseFromHeaders.class)
   @Path("/")
   ListenableFuture<AccountMetadata> getAccountMetadata();

   /**
    * @see AccountClient#listContainers()
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @Path("/")
   ListenableFuture<Set<ContainerMetadata>> listContainers();

   /**
    * @see AccountClient#listContainers(ListContainersOptions)
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @Path("/")
   ListenableFuture<Set<ContainerMetadata>> listContainers(ListContainersOptions options);
}
