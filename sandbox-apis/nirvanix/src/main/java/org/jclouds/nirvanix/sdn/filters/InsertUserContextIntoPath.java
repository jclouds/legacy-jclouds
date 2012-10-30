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
package org.jclouds.nirvanix.sdn.filters;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;

/**
 * Adds the Session Token to the request. This will update the Session Token before 20 minutes is
 * up.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class InsertUserContextIntoPath implements HttpRequestFilter {

   private final AddSessionTokenToRequest sessionManager;
   private final String pathPrefix;
   private final Provider<UriBuilder> builder;

   @Inject
   public InsertUserContextIntoPath(AddSessionTokenToRequest sessionManager,
         @Named(SDNConstants.PROPERTY_SDN_APPNAME) String appname,
         @Named(SDNConstants.PROPERTY_SDN_USERNAME) String username, Provider<UriBuilder> builder) {
      this.builder = builder;
      this.sessionManager = sessionManager;
      this.pathPrefix = String.format("/%s/%s/", appname, username);
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      String sessionToken = sessionManager.getSessionToken();
      int prefixIndex = request.getEndpoint().getPath().indexOf(pathPrefix);
      String path;
      if (prefixIndex == -1) { // addToken
         path = "/" + sessionToken + pathPrefix + request.getEndpoint().getPath().substring(1);
      } else { // replace token
         path = "/" + sessionToken + request.getEndpoint().getPath().substring(prefixIndex);
      }
      URI newEndpoint = builder.get().uri(request.getEndpoint()).replacePath(path).build();
      return request.toBuilder().endpoint(newEndpoint).build();
   }

}
