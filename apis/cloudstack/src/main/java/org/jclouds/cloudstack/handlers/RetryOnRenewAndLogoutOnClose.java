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
package org.jclouds.cloudstack.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.features.SessionClient;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class RetryOnRenewAndLogoutOnClose implements HttpRetryHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   private final LoadingCache<Credentials, LoginResponse> authenticationResponseCache;
   private final SessionClient sessionClient;

   @Inject
   protected RetryOnRenewAndLogoutOnClose(LoadingCache<Credentials, LoginResponse> authenticationResponseCache,
            SessionClient sessionClient) {
      this.authenticationResponseCache = authenticationResponseCache;
      this.sessionClient = sessionClient;
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      boolean retry = false; // default
      try {
         switch (response.getStatusCode()) {
            case 401:
               byte[] content = closeClientButKeepContentStream(response);
               if (new String(content).equals("TODO: What state can we retry?")) {
                  logger.debug("invalidating session");
                  authenticationResponseCache.invalidateAll();
                  retry = true;
               }
         }
         return retry;
      } finally {
         releasePayload(response);
      }
   }

   /**
    * it is important that we close any sessions on close to help the server not become overloaded.
    */
   @PreDestroy
   public void logoutOnClose() {
      for (LoginResponse s : authenticationResponseCache.asMap().values()) {
         try {
            sessionClient.logoutUser(s.getSessionKey());
         } catch (Exception e) {
            logger.error(e, "error logging out session %s", s.getSessionKey());
         }
      }
   }
}
