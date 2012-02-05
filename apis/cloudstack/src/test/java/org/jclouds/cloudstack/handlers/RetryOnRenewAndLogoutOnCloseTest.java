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

import com.google.common.cache.LoadingCache;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.features.SessionClient;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code RetryOnRenewAndLogoutOnClose} handler
 *
 * @author grkvlt@apache.org
 */
@Test(groups = "unit", testName = "RetryOnRenewAndLogoutOnCloseTest")
public class RetryOnRenewAndLogoutOnCloseTest {
   @SuppressWarnings("unchecked")
   @Test
   public void test401ShouldRetry() {
      HttpCommand command = createMock(HttpCommand.class);
      SessionClient sessionClient = createMock(SessionClient.class);
      LoadingCache<Credentials, LoginResponse> cache = createMock(LoadingCache.class);

      cache.invalidateAll();
      expectLastCall();

      replay(cache, command);

      HttpResponse response = HttpResponse.builder().payload(
         Payloads.newStringPayload("Not relevant")).statusCode(401).build();

      RetryOnRenewAndLogoutOnClose retry = new RetryOnRenewAndLogoutOnClose(cache, sessionClient);

      assertTrue(retry.shouldRetryRequest(command, response));

      verify(cache, command);
   }
}
