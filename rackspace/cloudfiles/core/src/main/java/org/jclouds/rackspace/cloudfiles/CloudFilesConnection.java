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
package org.jclouds.rackspace.cloudfiles;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.functions.ParseAccountMetadataResponseFromHeaders;
import org.jclouds.rackspace.cloudfiles.functions.ParseContainerListFromGsonResponse;
import org.jclouds.rackspace.cloudfiles.functions.ReturnTrueOn204FalseOtherwise;
import org.jclouds.rackspace.cloudfiles.functions.ReturnTrueOn404FalseOtherwise;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.jclouds.rackspace.filters.AuthenticateRequest;
import org.jclouds.rest.ExceptionParser;
import org.jclouds.rest.Query;
import org.jclouds.rest.RequestFilters;
import org.jclouds.rest.ResponseParser;
import org.jclouds.rest.SkipEncoding;

/**
 * Provides access to Cloud Files via their REST API.
 * <p/>
 * All commands return a Future of the result from Cloud Files. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090311.pdf" />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
public interface CloudFilesConnection {

   @HEAD
   @ResponseParser(ParseAccountMetadataResponseFromHeaders.class)
   @Path("/")
   AccountMetadata getAccountMetadata();

   // TODO: Should this method automatically retrieve paged results, i.e. for > 10,000 containers?
   @GET
   @ResponseParser(ParseContainerListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/")
   List<ContainerMetadata> listOwnedContainers();

   @GET
   @ResponseParser(ParseContainerListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/")
   List<ContainerMetadata> listOwnedContainers(ListContainerOptions options);

   @PUT
   @Path("{container}")
   boolean putContainer(@PathParam("container") String container);

   @DELETE
   @ResponseParser(ReturnTrueOn204FalseOtherwise.class)
   @ExceptionParser(ReturnTrueOn404FalseOtherwise.class)
   @Path("{container}")
   boolean deleteContainerIfEmpty(@PathParam("container") String container);

}
