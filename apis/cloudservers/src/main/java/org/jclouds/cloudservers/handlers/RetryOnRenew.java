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
package org.jclouds.cloudservers.handlers;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import org.jclouds.http.*;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.OpenStackAuthAsyncClient.AuthenticationResponse;
import org.jclouds.openstack.reference.AuthHeaders;
import org.jclouds.util.Suppliers2.InvalidatableExpiringMemoizingSupplier;

import javax.annotation.Resource;

import static org.jclouds.http.HttpUtils.releasePayload;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
public class RetryOnRenew implements HttpRetryHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   // This doesn't work yet
   @Inject
   Supplier<AuthenticationResponse> providedAuthenticationResponseCache;

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      boolean retry = false; // default
      try {
         switch (response.getStatusCode()) {
            case 401:
               // Do not retry on 401 from authentication request
               Multimap<String, String> headers = command.getCurrentRequest().getHeaders();
               if (headers != null && headers.containsKey(AuthHeaders.AUTH_USER) && headers.containsKey(AuthHeaders.AUTH_KEY) &&
                     !headers.containsKey(AuthHeaders.AUTH_TOKEN)) {
                  retry = false;
               } else {
                  // Otherwise invalidate the token cache, to force reauthentication
                  ((InvalidatableExpiringMemoizingSupplier) providedAuthenticationResponseCache).invalidate();
                  retry = true;
               }
               break;
         }
      } finally {
         releasePayload(response);
         return retry;
      }
   }
}
