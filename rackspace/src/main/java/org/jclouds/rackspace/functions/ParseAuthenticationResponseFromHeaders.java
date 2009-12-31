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
import static org.jclouds.rackspace.reference.RackspaceHeaders.AUTH_TOKEN;
import static org.jclouds.rackspace.reference.RackspaceHeaders.CDN_MANAGEMENT_URL;
import static org.jclouds.rackspace.reference.RackspaceHeaders.SERVER_MANAGEMENT_URL;
import static org.jclouds.rackspace.reference.RackspaceHeaders.STORAGE_URL;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.RackspaceAuthentication.AuthenticationResponse;

import com.google.common.base.Function;

/**
 * This parses {@link AuthenticationResponse} from HTTP headers.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseAuthenticationResponseFromHeaders implements
         Function<HttpResponse, AuthenticationResponse> {

   /**
    * parses the http response headers to create a new {@link AuthenticationResponse} object.
    */
   public AuthenticationResponse apply(final HttpResponse from) {
      return new AuthenticationResponse() {

         public String getAuthToken() {
            return checkNotNull(from.getFirstHeaderOrNull(AUTH_TOKEN), AUTH_TOKEN);
         }

         public URI getCDNManagementUrl() {
            String cdnManagementUrl = checkNotNull(from.getFirstHeaderOrNull(CDN_MANAGEMENT_URL),
                     CDN_MANAGEMENT_URL);
            return URI.create(cdnManagementUrl);
         }

         public URI getServerManagementUrl() {
            String serverManagementUrl = checkNotNull(from
                     .getFirstHeaderOrNull(SERVER_MANAGEMENT_URL), SERVER_MANAGEMENT_URL);
            return URI.create(serverManagementUrl);
         }

         public URI getStorageUrl() {
            String storageUrl = checkNotNull(from.getFirstHeaderOrNull(STORAGE_URL), STORAGE_URL
                     + " not found in headers:" + from.getStatusLine() + " - " + from.getHeaders());
            return URI.create(storageUrl);
         }

      };

   }
}