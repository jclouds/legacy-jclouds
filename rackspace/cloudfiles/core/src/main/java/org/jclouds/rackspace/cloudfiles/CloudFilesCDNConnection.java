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

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.functions.ParseCdnUriFromHeaders;
import org.jclouds.rackspace.cloudfiles.functions.ParseContainerCDNMetadataFromHeaders;
import org.jclouds.rackspace.cloudfiles.functions.ParseContainerCDNMetadataListFromGsonResponse;
import org.jclouds.rackspace.cloudfiles.functions.ReturnContainerCDNMetadataNotFoundOn404;
import org.jclouds.rackspace.cloudfiles.functions.ReturnTrueOn202FalseOtherwise;
import org.jclouds.rackspace.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesHeaders;
import org.jclouds.rackspace.filters.AuthenticateRequest;
import org.jclouds.rest.ExceptionParser;
import org.jclouds.rest.Header;
import org.jclouds.rest.Query;
import org.jclouds.rest.RequestFilters;
import org.jclouds.rest.ResponseParser;
import org.jclouds.rest.SkipEncoding;

/**
 * Provides access to the Cloud Files service's CDN offering via their REST API.
 * 
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090311.pdf" />
 * @author James Murty
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
public interface CloudFilesCDNConnection {

   @GET
   @ResponseParser(ParseContainerCDNMetadataListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/")
   List<ContainerCDNMetadata> listCDNContainers(ListCdnContainerOptions ... options);   

   // TODO: Container name is not included in CDN HEAD response headers, so we cannot populate it here.
   @HEAD
   @ResponseParser(ParseContainerCDNMetadataFromHeaders.class)
   @ExceptionParser(ReturnContainerCDNMetadataNotFoundOn404.class)
   @Path("{container}")
   ContainerCDNMetadata getCDNMetadata(@PathParam("container") String container);

   @PUT
   @Path("{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   String enableCDN(@PathParam("container") String container, 
         @HeaderParam(CloudFilesHeaders.CDN_TTL) Long ttl);

   @PUT
   @Path("{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   String enableCDN(@PathParam("container") String container);

   @POST
   @Path("{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   String updateCDN(@PathParam("container") String container, 
         @HeaderParam(CloudFilesHeaders.CDN_TTL) Long ttl);

   @POST
   @Path("{container}")
   @Header(key = CloudFilesHeaders.CDN_ENABLED, value = "False")
   @ResponseParser(ReturnTrueOn202FalseOtherwise.class)
   boolean disableCDN(@PathParam("container") String container);

}
