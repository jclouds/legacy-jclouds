/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.chef.handlers;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.net.URI;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ChefClientErrorRetryHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "chef.ChefClientErrorRetryHandlerTest")
public class ChefClientErrorRetryHandlerTest {
   @Test
   public void test401DoesNotRetry() {

      HttpCommand command = createMock(HttpCommand.class);
      HttpResponse response = createMock(HttpResponse.class);
      BackoffLimitedRetryHandler retry = createMock(BackoffLimitedRetryHandler.class);

      expect(command.getFailureCount()).andReturn(0);
      expect(response.getStatusCode()).andReturn(401).atLeastOnce();

      replay(response);
      replay(retry);
      replay(command);

      ChefClientErrorRetryHandler handler = new ChefClientErrorRetryHandler(retry);

      assert !handler.shouldRetryRequest(command, response);

      verify(retry);
      verify(command);
      verify(response);

   }

   @Test
   public void test400DoesNotRetry() {

      HttpCommand command = createMock(HttpCommand.class);
      HttpResponse response = createMock(HttpResponse.class);
      BackoffLimitedRetryHandler retry = createMock(BackoffLimitedRetryHandler.class);

      expect(command.getFailureCount()).andReturn(0);
      expect(response.getStatusCode()).andReturn(401).atLeastOnce();

      replay(response);
      replay(retry);
      replay(command);

      ChefClientErrorRetryHandler handler = new ChefClientErrorRetryHandler(retry);

      assert !handler.shouldRetryRequest(command, response);

      verify(retry);
      verify(command);
      verify(response);

   }

   @Test
   public void testRetryOn400PutSandbox() {

      HttpCommand command = createMock(HttpCommand.class);
      BackoffLimitedRetryHandler retry = createMock(BackoffLimitedRetryHandler.class);

      HttpRequest request = new HttpRequest("PUT", URI
            .create("https://api.opscode.com/organizations/jclouds/sandboxes/bfd68d4052f44053b2e593a33b5e1cd5"));
      HttpResponse response = new HttpResponse(
            400,
            "400 Bad Request",
            Payloads
                  .newStringPayload("{\"error\":[\"Cannot update sandbox bfd68d4052f44053b2e593a33b5e1cd5: checksum 9b7c23369f4b576451216c39f214af6c was not uploaded\"]}"));

      expect(command.getFailureCount()).andReturn(0);
      expect(command.getRequest()).andReturn(request).atLeastOnce();
      expect(retry.shouldRetryRequest(command, response)).andReturn(true);

      replay(retry);
      replay(command);

      ChefClientErrorRetryHandler handler = new ChefClientErrorRetryHandler(retry);

      assert handler.shouldRetryRequest(command, response);

      verify(retry);
      verify(command);

   }
}
