/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.RackspaceAuthAsyncClient.AuthenticationResponse;

import com.google.common.base.Function;

/**
 * This parses {@link AuthenticationResponse} from HTTP headers.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseAuthenticationResponseFromHeaders implements
         Function<HttpResponse, AuthenticationResponse> {

   public static final class AuthenticationResponseImpl implements AuthenticationResponse {

      private final String authToken;
      private final String CDNManagementUrl;
      private final String serverManagementUrl;
      private final String storageUrl;

      public AuthenticationResponseImpl(String authToken, String CDNManagementUrl,
               String serverManagementUrl, String storageUrl) {
         this.authToken = authToken;
         this.CDNManagementUrl = CDNManagementUrl;
         this.serverManagementUrl = serverManagementUrl;
         this.storageUrl = storageUrl;
      }

      public String getAuthToken() {
         return authToken;
      }

      public URI getCDNManagementUrl() {
         return URI.create(CDNManagementUrl);
      }

      public URI getServerManagementUrl() {
         return URI.create(serverManagementUrl);
      }

      public URI getStorageUrl() {
         return URI.create(storageUrl);
      }
   }

   /**
    * parses the http response headers to create a new {@link AuthenticationResponse} object.
    */
   public AuthenticationResponse apply(HttpResponse from) {
      releasePayload(from);
      return new AuthenticationResponseImpl(checkNotNull(from.getFirstHeaderOrNull(AUTH_TOKEN),
               AUTH_TOKEN), checkNotNull(from.getFirstHeaderOrNull(CDN_MANAGEMENT_URL),
               CDN_MANAGEMENT_URL), checkNotNull(from.getFirstHeaderOrNull(SERVER_MANAGEMENT_URL),
               SERVER_MANAGEMENT_URL), checkNotNull(from.getFirstHeaderOrNull(STORAGE_URL),
               STORAGE_URL + " not found in headers:" + from.getStatusLine() + " - "
                        + from.getHeaders()));

   }
}