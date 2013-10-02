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
package org.jclouds.openstack.internal;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

import org.jclouds.Constants;
import org.jclouds.openstack.domain.AuthenticationResponse;
import org.jclouds.openstack.functions.ParseAuthenticationResponseFromHeaders;
import org.jclouds.openstack.reference.AuthHeaders;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.VirtualHost;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Rackspace resources via their REST API.
 * <p/>
 * 
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @author Adrian Cole
 */
@Path("/v{" + Constants.PROPERTY_API_VERSION + "}")
@VirtualHost
public interface OpenStackAuthAsyncClient {

   @GET
   @Consumes
   @ResponseParser(ParseAuthenticationResponseFromHeaders.class)
   ListenableFuture<AuthenticationResponse> authenticate(@HeaderParam(AuthHeaders.AUTH_USER) String user,
            @HeaderParam(AuthHeaders.AUTH_KEY) String key);
   

   @GET
   @Consumes
   @ResponseParser(ParseAuthenticationResponseFromHeaders.class)
   ListenableFuture<AuthenticationResponse> authenticateStorage(@HeaderParam(AuthHeaders.STORAGE_USER) String user,
            @HeaderParam(AuthHeaders.STORAGE_PASS) String key);
}
