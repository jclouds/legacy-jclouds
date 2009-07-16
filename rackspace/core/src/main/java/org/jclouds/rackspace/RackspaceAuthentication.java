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
package org.jclouds.rackspace;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

import org.jclouds.rackspace.functions.ParseAuthenticationResponseFromHeaders;
import org.jclouds.rackspace.reference.RackspaceHeaders;
import org.jclouds.rest.ResponseParser;

/**
 * Provides access to Rackspace resources via their REST API.
 * <p/>
 * 
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @author Adrian Cole
 */

public interface RackspaceAuthentication {

   public interface AuthenticationResponse {
      @Storage
      URI getStorageUrl();

      @CDN
      URI getCDNManagementUrl();

      @Server
      URI getServerManagementUrl();

      @Authentication
      String getAuthToken();
   }

   @GET
   @ResponseParser(ParseAuthenticationResponseFromHeaders.class)
   @Path("/auth")
   AuthenticationResponse authenticate(@HeaderParam(RackspaceHeaders.AUTH_USER) String user,
            @HeaderParam(RackspaceHeaders.AUTH_KEY) String key);
}
