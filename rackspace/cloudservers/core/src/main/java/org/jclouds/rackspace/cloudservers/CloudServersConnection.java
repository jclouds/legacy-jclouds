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
package org.jclouds.rackspace.cloudservers;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerListFromGsonResponse;
import org.jclouds.rackspace.filters.AuthenticateRequest;
import org.jclouds.rest.Query;
import org.jclouds.rest.RequestFilters;
import org.jclouds.rest.ResponseParser;
import org.jclouds.rest.SkipEncoding;

/**
 * Provides access to Cloud Servers via their REST API.
 * <p/>
 * All commands return a Future of the result from Cloud Servers. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
public interface CloudServersConnection {

   /**
    * 
    * List all servers (IDs and names only)
    * 
    * @see #listServerDetails()
    */
   @GET
   @ResponseParser(ParseServerListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/servers")
   // TODO: Error Response Code(s): cloudServersFault (400, 500), serviceUnavailable (503),
   // unauthorized (401), badRequest (400), overLimit (413)
   List<Server> listServers();

   /**
    * This operation provides a list of servers associated with your account. Servers that have been
    * deleted are not included in this list. 
    */
   @GET
   @ResponseParser(ParseServerListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/servers/detail")
   // TODO: Error Response Code(s): cloudServersFault (400, 500), serviceUnavailable (503),
   // unauthorized (401), badRequest (400), overLimit (413)
   List<Server> listServerDetails();

   /**
    * 
    * List available flavors (IDs and names only)
    * 
    * @see #listFlavorDetails()
    */
   @GET
   @ResponseParser(ParseFlavorListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/flavors")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   List<Flavor> listFlavors();

   /**
    * 
    * List available flavors (all details)
    * 
    * @see Flavor
    */
   @GET
   @ResponseParser(ParseFlavorListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/flavors/detail")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   List<Flavor> listFlavorDetails();
   
   

   /**
    * 
    * List available images (IDs and names only)
    * 
    * @see #listImageDetails()
    */
   @GET
   @ResponseParser(ParseImageListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/images")
   // TODO:  cloudServersFault (400, 500),  serviceUnavailable (503), unauthorized (401), badRequest (400) 
   List<Image> listImages();

   /**
    * 
    * This operation will list all images visible by the account.
    * 
    * @see Image
    */
   @GET
   @ResponseParser(ParseImageListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/images/detail")
   // TODO:  cloudServersFault (400, 500),  serviceUnavailable (503), unauthorized (401), badRequest (400) 
   List<Image> listImageDetails();

}
