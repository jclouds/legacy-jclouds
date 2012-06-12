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
package org.jclouds.openstack.keystone.v2_0.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;

import javax.annotation.Resource;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.keystone.v2_0.domain.Access;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class RetryOnRenew implements HttpRetryHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   private final LoadingCache<Credentials, Access> authenticationResponseCache;

   @Inject
   protected RetryOnRenew(LoadingCache<Credentials, Access> authenticationResponseCache) {
      this.authenticationResponseCache = authenticationResponseCache;
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      boolean retry = false; // default
      try {
         switch (response.getStatusCode()) {
            case 401:
               // Do not retry on 401 from authentication request
               Multimap<String, String> headers = command.getCurrentRequest().getHeaders();
               if (headers != null && headers.containsKey("X-Auth-User")
                        && headers.containsKey("X-Auth-Key") && !headers.containsKey("X-Auth-Token")) {
                  retry = false;
               } else {
                  byte[] content = closeClientButKeepContentStream(response);
                  //TODO: what is the error when the session token expires??
                  if (content != null && new String(content).contains("lease renew")) {
                     logger.debug("invalidating authentication token");
                     authenticationResponseCache.invalidateAll();
                     retry = true;
                  } else {
                     retry = false;
                  }
               }
               break;
         }
         return retry;

      } finally {
         releasePayload(response);
      }
   }

}
