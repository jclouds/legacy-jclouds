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
package org.jclouds.vcloud.director.v1_5.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.director.v1_5.domain.SessionWithToken;
import org.jclouds.vcloud.director.v1_5.login.SessionApi;

import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * If the credentials supplied in the authentication header are invalid, or if the token has
 * expired, the server returns HTTP response code 401. The token expires after a configurable
 * interval of api inactivity. The default is 30 minutes after the token is created. After the
 * token expires, you must log in again to obtain a new token.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class InvalidateSessionAndRetryOn401AndLogoutOnClose extends BackoffLimitedRetryHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   private final LoadingCache<Credentials, SessionWithToken> authenticationResponseCache;
   private final SessionApi sessionApi;

   @Inject
   protected InvalidateSessionAndRetryOn401AndLogoutOnClose(
            LoadingCache<Credentials, SessionWithToken> authenticationResponseCache, SessionApi sessionApi) {
      this.authenticationResponseCache = authenticationResponseCache;
      this.sessionApi = sessionApi;
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      boolean retry = false; // default
      try {
         if (response.getStatusCode() == 401) {
            closeClientButKeepContentStream(response);
            logger.debug("invalidating session");
            authenticationResponseCache.invalidateAll();
            retry = super.shouldRetryRequest(command, response);
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
      for (SessionWithToken s : authenticationResponseCache.asMap().values()) {
         try {
            sessionApi.logoutSessionWithToken(s.getSession().getHref(), s.getToken());
         } catch (Exception e) {
            logger.error(e, "error logging out session %s", s.getSession());
         }
      }
   }
}
