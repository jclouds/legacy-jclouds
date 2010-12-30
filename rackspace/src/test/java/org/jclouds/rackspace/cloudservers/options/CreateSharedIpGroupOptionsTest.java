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

package org.jclouds.rackspace.cloudservers.options;

import static org.jclouds.rackspace.cloudservers.options.CreateSharedIpGroupOptions.Builder.withServer;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseFlavorFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CreateSharedIpGroupOptionsTest {

   Injector injector = Guice.createInjector(new GsonModule());

   @Test
   public void testAddPayloadToRequestMapOfStringStringHttpRequest() {
      CreateSharedIpGroupOptions options = new CreateSharedIpGroupOptions();
      HttpRequest request = buildRequest(options);
      assertEquals("{\"sharedIpGroup\":{\"name\":\"foo\"}}", request.getPayload().getRawContent());
   }

   private HttpRequest buildRequest(CreateSharedIpGroupOptions options) {
      injector.injectMembers(options);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      options.bindToRequest(request, ImmutableMap.of("name", "foo"));
      return request;
   }

   @Test
   public void testWithServer() {
      CreateSharedIpGroupOptions options = new CreateSharedIpGroupOptions();
      options.withServer(3);
      HttpRequest request = buildRequest(options);
      assertSharedIpGroup(request);
   }

   @Test
   public void testWithServerStatic() {
      CreateSharedIpGroupOptions options = withServer(3);
      HttpRequest request = buildRequest(options);
      assertSharedIpGroup(request);
   }

   private void assertSharedIpGroup(HttpRequest request) {
      assertEquals("{\"sharedIpGroup\":{\"name\":\"foo\",\"server\":3}}", request.getPayload().getRawContent());
   }

}
