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
package org.jclouds.openstack.nova.options;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.net.URI;

import static org.jclouds.openstack.nova.options.CreateServerOptions.Builder.withFile;
import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code ParseFlavorFromJsonResponse}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CreateServerOptionsTest {

   Injector injector = Guice.createInjector(new GsonModule());

   @Test
   public void testAddPayloadToRequestMapOfStringStringHttpRequest() {
      CreateServerOptions options = new CreateServerOptions();
      HttpRequest request = buildRequest(options);
      assertEquals("{\"server\":{\"name\":\"foo\",\"imageRef\":\"1\",\"flavorRef\":\"2\"}}", request.getPayload().getRawContent());
   }

   private HttpRequest buildRequest(CreateServerOptions options) {
      injector.injectMembers(options);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      options.bindToRequest(request, ImmutableMap.of("name", "foo", "imageRef", "1", "flavorRef", "2"));
      return request;
   }

   @Test
   public void testWithFile() {
      CreateServerOptions options = new CreateServerOptions();
      options.withFile("/tmp/rhubarb", "foo".getBytes());
      HttpRequest request = buildRequest(options);
      assertFile(request);
   }

   @Test
   public void testWithFileStatic() {
      CreateServerOptions options = withFile("/tmp/rhubarb", "foo".getBytes());
      HttpRequest request = buildRequest(options);
      assertFile(request);
   }

   private void assertFile(HttpRequest request) {
      assertEquals(request.getPayload().getRawContent(),
            "{\"server\":{\"name\":\"foo\",\"imageRef\":\"1\",\"flavorRef\":\"2\",\"personality\":[{\"path\":\"/tmp/rhubarb\",\"contents\":\"Zm9v\"}]}}");
   }

   @Test
   public void testWithMetadata() {
   }
}
