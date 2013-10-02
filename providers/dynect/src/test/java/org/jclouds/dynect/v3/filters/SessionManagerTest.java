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

import static com.google.common.io.Resources.getResource;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.jclouds.domain.Credentials;
import org.jclouds.dynect.v3.domain.Session;
import org.jclouds.dynect.v3.domain.SessionCredentials;
import org.jclouds.dynect.v3.features.SessionApi;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SessionManagerTest")
public class SessionManagerTest {
   SessionCredentials creds = SessionCredentials.builder()
                                                .customerName("customer")
                                                .userName("robbie")
                                                .password("password").build();

   Session session = Session.forTokenAndVersion("token", "version");

   public void testAlreadySessionCredentials() {
      assertSame(SessionManager.convert(creds), creds);
   }

   public void testConvertCredentialsParsesCustomer() {
      assertEquals(SessionManager.convert(new Credentials("customer:robbie", "password")), creds);
   }

   public void testCacheLoadLogsIn() {
      SessionApi sessionApi = createMock(SessionApi.class);
      expect(sessionApi.login(creds)).andReturn(session);
      replay(sessionApi);

      assertSame(SessionManager.buildCache(sessionApi).apply(creds), session);

      verify(sessionApi);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testUnauthorizedShouldInvalidateSessionAndRetry() {
      HttpCommand command = createMock(HttpCommand.class);
      Supplier<Credentials> creds = createMock(Supplier.class);
      LoadingCache<Credentials, Session> sessionCache = createMock(LoadingCache.class);
      SessionApi sessionApi = createMock(SessionApi.class);

      sessionCache.invalidateAll();
      expectLastCall();
      expect(command.incrementFailureCount()).andReturn(1);
      expect(command.isReplayable()).andReturn(true);
      expect(command.getFailureCount()).andReturn(1).atLeastOnce();

      replay(creds, sessionCache, sessionApi, command);

      HttpResponse response = HttpResponse.builder().statusCode(UNAUTHORIZED.getStatusCode()).build();

      SessionManager retry = new SessionManager(creds, sessionCache, sessionApi);

      assertTrue(retry.shouldRetryRequest(command, response));

      verify(creds, sessionCache, sessionApi, command);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testIPMismatchShouldInvalidateSessionAndRetry() throws IOException {
      HttpCommand command = createMock(HttpCommand.class);
      Supplier<Credentials> creds = createMock(Supplier.class);
      LoadingCache<Credentials, Session> sessionCache = createMock(LoadingCache.class);
      SessionApi sessionApi = createMock(SessionApi.class);

      sessionCache.invalidateAll();
      expectLastCall();
      expect(command.incrementFailureCount()).andReturn(1);
      expect(command.isReplayable()).andReturn(true);
      expect(command.getFailureCount()).andReturn(1).atLeastOnce();

      replay(creds, sessionCache, sessionApi, command);

      HttpResponse response = HttpResponse.builder()
                                          .statusCode(BAD_REQUEST.getStatusCode())
                                          .payload(getResource("ip_mismatch.json").openStream())
                                          .build();

      SessionManager retry = new SessionManager(creds, sessionCache, sessionApi);

      assertTrue(retry.shouldRetryRequest(command, response));

      verify(creds, sessionCache, sessionApi, command);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testForbiddenShouldNotInvalidateSessionOrRetry() {
      HttpCommand command = createMock(HttpCommand.class);
      Supplier<Credentials> creds = createMock(Supplier.class);
      LoadingCache<Credentials, Session> sessionCache = createMock(LoadingCache.class);
      SessionApi sessionApi = createMock(SessionApi.class);

      replay(creds, sessionCache, sessionApi, command);

      HttpResponse response = HttpResponse.builder().statusCode(FORBIDDEN.getStatusCode()).build();

      SessionManager retry = new SessionManager(creds, sessionCache, sessionApi);

      assertFalse(retry.shouldRetryRequest(command, response));

      verify(creds, sessionCache, sessionApi, command);
   }

}
