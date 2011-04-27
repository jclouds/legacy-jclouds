/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.openstack.nova.options;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.net.URI;
import java.util.HashMap;

import static org.jclouds.openstack.nova.options.RebuildServerOptions.Builder.withImage;
import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code ParseFlavorFromJsonResponse}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class RebuildServerOptionsTest {

   Injector injector = Guice.createInjector(new GsonModule());

   @Test
   public void testAddPayloadToRequestMapOfStringStringHttpRequest() {
      RebuildServerOptions options = new RebuildServerOptions();
      HttpRequest request = buildRequest(options);
      assertEquals("{\"rebuild\":{}}", request.getPayload().getRawContent());
   }

   private HttpRequest buildRequest(RebuildServerOptions options) {
      injector.injectMembers(options);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      options.bindToRequest(request, new HashMap<String, String>());
      return request;
   }

   @Test
   public void testWithServer() {
      RebuildServerOptions options = new RebuildServerOptions();
      options.withImage("3");
      HttpRequest request = buildRequest(options);
      assertRebuild(request);
   }

   @Test
   public void testWithServerStatic() {
      RebuildServerOptions options = withImage("3");
      HttpRequest request = buildRequest(options);
      assertRebuild(request);
   }

   private void assertRebuild(HttpRequest request) {
      assertEquals("{\"rebuild\":{\"imageRef\":\"3\"}}", request.getPayload().getRawContent());
   }

}
