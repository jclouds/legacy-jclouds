/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.rackspace.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.HttpUtils.releasePayload;
import static org.jclouds.rackspace.reference.RackspaceHeaders.AUTH_TOKEN;
import static org.jclouds.rackspace.reference.RackspaceHeaders.CDN_MANAGEMENT_URL;
import static org.jclouds.rackspace.reference.RackspaceHeaders.SERVER_MANAGEMENT_URL;
import static org.jclouds.rackspace.reference.RackspaceHeaders.STORAGE_URL;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.RackspaceAuthAsyncClient.AuthenticationResponse;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;
import com.google.common.base.Objects;

/**
 * This parses {@link AuthenticationResponse} from HTTP headers.
 * 
 * @author Adrian Cole
 */

public class ParseAuthenticationResponseFromHeaders implements Function<HttpResponse, AuthenticationResponse>,
      InvocationContext {

   public static final class AuthenticationResponseImpl implements AuthenticationResponse {

      private final String authToken;
      private final URI CDNManagementUrl;
      private final URI serverManagementUrl;
      private final URI storageUrl;

      public AuthenticationResponseImpl(String authToken, URI CDNManagementUrl, URI serverManagementUrl, URI storageUrl) {
         this.authToken = authToken;
         this.CDNManagementUrl = CDNManagementUrl;
         this.serverManagementUrl = serverManagementUrl;
         this.storageUrl = storageUrl;
      }

      public String getAuthToken() {
         return authToken;
      }

      public URI getCDNManagementUrl() {
         return CDNManagementUrl;
      }

      public URI getServerManagementUrl() {
         return serverManagementUrl;
      }

      public URI getStorageUrl() {
         return storageUrl;
      }

      // performance isn't a concern on a infrequent object like this, so using shortcuts;

      @Override
      public int hashCode() {
         return Objects.hashCode(CDNManagementUrl, authToken, serverManagementUrl, storageUrl);
      }

      @Override
      public boolean equals(Object that) {
         if (that == null)
            return false;
         return Objects.equal(this.toString(), that.toString());
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this).add("CDNManagementUrl", CDNManagementUrl)
               .add("serverManagementUrl", serverManagementUrl).add("storageUrl", storageUrl).toString();
      }

   }

   @Resource
   protected Logger logger = Logger.NULL;

   private final Provider<UriBuilder> uriBuilderProvider;
   private String hostToReplace;

   @Inject
   public ParseAuthenticationResponseFromHeaders(Provider<UriBuilder> uriBuilderProvider) {
      this.uriBuilderProvider = uriBuilderProvider;
   }

   /**
    * parses the http response headers to create a new {@link AuthenticationResponse} object.
    */
   public AuthenticationResponse apply(HttpResponse from) {
      releasePayload(from);
      AuthenticationResponse response = new AuthenticationResponseImpl(checkNotNull(
            from.getFirstHeaderOrNull(AUTH_TOKEN), AUTH_TOKEN), getURI(from, CDN_MANAGEMENT_URL), getURI(from,
            SERVER_MANAGEMENT_URL), getURI(from, STORAGE_URL));
      logger.debug("will connect to: ", response);
      return response;
   }

   protected URI getURI(HttpResponse from, String header) {
      String headerValue = from.getFirstHeaderOrNull(header);
      if (headerValue == null)
         return null;
      URI toReturn = URI.create(headerValue);
      if (!"127.0.0.1".equals(toReturn.getHost()))
         return toReturn;
      return uriBuilderProvider.get().uri(toReturn).host(hostToReplace).build();
   }

   @Override
   public Object setContext(HttpRequest request) {
      hostToReplace = request.getEndpoint().getHost();
      return this;
   }
}