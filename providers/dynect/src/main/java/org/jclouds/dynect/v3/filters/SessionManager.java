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
package org.jclouds.dynect.v3.filters;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.jclouds.domain.Credentials;
import org.jclouds.dynect.v3.domain.Session;
import org.jclouds.dynect.v3.domain.SessionCredentials;
import org.jclouds.dynect.v3.features.SessionApi;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequest.Builder;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * This class manages session interactions, including grabbing latest from the
 * cache, and invalidating upon 401 If the credentials supplied in the
 * authentication header are invalid, or if the token has expired, the server
 * returns HTTP response code 401. After the token expires, you must log in
 * again to obtain a new token.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public final class SessionManager extends BackoffLimitedRetryHandler implements HttpRequestFilter {

   @Resource
   private Logger logger = Logger.NULL;

   private final Supplier<Credentials> creds;
   private final SessionApi sessionApi;
   private final LoadingCache<Credentials, Session> sessionCache;

   @Inject
   SessionManager(@Provider Supplier<Credentials> creds, SessionApi sessionApi) {
      this(creds, buildCache(sessionApi), sessionApi);
   }

   SessionManager(@Provider Supplier<Credentials> creds, LoadingCache<Credentials, Session> sessionCache,
         SessionApi sessionApi) {
      this.creds = creds;
      this.sessionCache = sessionCache;
      this.sessionApi = sessionApi;
   }

   static LoadingCache<Credentials, Session> buildCache(final SessionApi sessionApi) {
      return CacheBuilder.newBuilder().build(new CacheLoader<Credentials, Session>() {
         public Session load(Credentials key) {
            return sessionApi.login(convert(key));
         }
      });
   }

   static SessionCredentials convert(Credentials key) {
      if (key instanceof SessionCredentials)
         return SessionCredentials.class.cast(key);
      return SessionCredentials.builder().customerName(key.identity.substring(0, key.identity.indexOf(':')))
            .userName(key.identity.substring(key.identity.indexOf(':') + 1)).password(key.credential).build();
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      Session session = sessionCache.getUnchecked(creds.get());
      Builder<?> builder = request.toBuilder();
      builder.replaceHeader("Auth-Token", session.getToken());
      return builder.build();
   }

   private static final String IP_MISMATCH = "IP address does not match current session";

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      boolean retry = false; // default
      try {
         byte[] data = closeClientButKeepContentStream(response);
         String message = data != null ? new String(data) : null;
         if (response.getStatusCode() == 401 || (message != null && message.indexOf(IP_MISMATCH) != -1)) {
            logger.debug("invalidating session");
            sessionCache.invalidateAll();
            retry = super.shouldRetryRequest(command, response);
         }
         return retry;
      } finally {
         releasePayload(response);
      }
   }

   /**
    * it is important that we close any sessions on close to help the server not
    * become overloaded.
    */
   @PreDestroy
   public void logoutOnClose() {
      for (Session s : sessionCache.asMap().values()) {
         try {
            sessionApi.logout(s.getToken());
         } catch (Exception e) {
            logger.error(e, "error logging out session %s", s);
         }
      }
   }
}
