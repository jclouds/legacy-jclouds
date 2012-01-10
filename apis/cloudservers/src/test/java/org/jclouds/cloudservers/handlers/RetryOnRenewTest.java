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

import com.google.common.base.Suppliers;
import com.google.common.collect.*;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;

/**
 * Tests behavior of {@code RetryOnRenew} handler
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "unit")
public class RetryOnRenewTest {
   @Test
   public void test401ShouldRetry() {
      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = createMock(HttpRequest.class);
      HttpResponse response = createMock(HttpResponse.class);

      expect(command.getFailureCount()).andReturn(0);
      expect(command.getCurrentRequest()).andReturn(request);

      expect(response.getPayload()).andReturn(null).anyTimes();
      expect(response.getStatusCode()).andReturn(401).atLeastOnce();

      replay(command);
      replay(response);

      RetryOnRenew retry = new RetryOnRenew();

      assertTrue(retry.shouldRetryRequest(command, response));

      // verify(command);
      // verify(response);
   }
}
