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
package org.jclouds.cloudstack.handlers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.easymock.IAnswer;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.features.SessionClient;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.common.cache.LoadingCache;

/**
 * Tests behavior of {@code InvalidateSessionAndRetryOn401AndLogoutOnClose} handler
 *
 * @author grkvlt@apache.org
 */
@Test(groups = "unit", testName = "InvalidateSessionAndRetryOn401AndLogoutOnCloseTest")
public class InvalidateSessionAndRetryOn401AndLogoutOnCloseTest {

   @SuppressWarnings("unchecked")
   @Test
   public void test401ShouldRetryAndFailAfterFiveAttempts() {
      HttpCommand command = createMock(HttpCommand.class);
      SessionClient sessionClient = createMock(SessionClient.class);
      LoadingCache<Credentials, LoginResponse> cache = createMock(LoadingCache.class);

      cache.invalidateAll();
      expectLastCall().anyTimes();

      final AtomicInteger counter = new AtomicInteger();
      expect(command.incrementFailureCount()).andAnswer(new IAnswer<Integer>() {
         @Override
         public Integer answer() throws Throwable {
            return counter.incrementAndGet();
         }
      }).anyTimes();
      expect(command.isReplayable()).andReturn(true).anyTimes();
      expect(command.getFailureCount()).andAnswer(new IAnswer<Integer>() {
         @Override
         public Integer answer() throws Throwable {
            return counter.get();
         }
      }).anyTimes();

      replay(cache, command);

      HttpResponse response = HttpResponse.builder().payload(
         Payloads.newStringPayload("Not relevant")).statusCode(401).build();

      InvalidateSessionAndRetryOn401AndLogoutOnClose retry =
         new InvalidateSessionAndRetryOn401AndLogoutOnClose(cache, sessionClient);

      for (int i = 0; i < 5; i++) {
         assertTrue(retry.shouldRetryRequest(command, response));
      }
      assertFalse(retry.shouldRetryRequest(command, response));

      verify(cache, command);
   }
}
