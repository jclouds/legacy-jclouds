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
package org.jclouds.deltacloud.handlers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.jclouds.ContextBuilder;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.inject.Injector;

/**
 * Tests behavior of {@code DeltacloudRedirectionRetry}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class DeltacloudRedirectionRetryHandlerTest {
   Injector injector = ContextBuilder.newBuilder("stub").buildInjector();

   @Test
   public void test302DoesNotRetryOnDelete() {

      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = HttpRequest.builder().method("DELETE").endpoint("http://localhost").build();
      HttpResponse response = HttpResponse.builder().statusCode(302).message("HTTP/1.1 302 Found").build();

      expect(command.getCurrentRequest()).andReturn(request).atLeastOnce();

      replay(command);

      DeltacloudRedirectionRetryHandler retry = injector.getInstance(
            DeltacloudRedirectionRetryHandler.class);

      assert !retry.shouldRetryRequest(command, response);

      verify(command);

   }

   @Test
   public void test302DoesRetryOnGET() {

      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://localhost").build();
      HttpResponse response = HttpResponse.builder().statusCode(302).message("HTTP/1.1 302 Found").build();

      expect(command.isReplayable()).andReturn(true);
      expect(command.getCurrentRequest()).andReturn(request).atLeastOnce();
      expect(command.incrementRedirectCount()).andReturn(1);

      replay(command);

      DeltacloudRedirectionRetryHandler retry = injector.getInstance(
            DeltacloudRedirectionRetryHandler.class);

      assert !retry.shouldRetryRequest(command, response);

      verify(command);

   }
}
