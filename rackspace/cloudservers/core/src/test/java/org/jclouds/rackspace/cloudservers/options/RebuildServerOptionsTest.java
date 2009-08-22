/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudservers.options;

import static org.jclouds.rackspace.cloudservers.options.RebuildServerOptions.Builder.withImage;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.HashMap;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseFlavorFromGsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudsharedIpGroups.RebuildServerOptionsTest")
public class RebuildServerOptionsTest {

   Injector injector = Guice.createInjector(new ParserModule());

   @Test
   public void testAddEntityToRequestMapOfStringStringHttpRequest() {
      RebuildServerOptions options = new RebuildServerOptions();
      HttpRequest request = buildRequest(options);
      assertEquals("{\"rebuild\":{}}", request.getEntity());
   }

   private HttpRequest buildRequest(RebuildServerOptions options) {
      injector.injectMembers(options);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("/"));
      options.addEntityToRequest(new HashMap<String, String>(), request);
      return request;
   }

   @Test
   public void testWithServer() {
      RebuildServerOptions options = new RebuildServerOptions();
      options.withImage(3);
      HttpRequest request = buildRequest(options);
      assertRebuild(request);
   }

   @Test
   public void testWithServerStatic() {
      RebuildServerOptions options = withImage(3);
      HttpRequest request = buildRequest(options);
      assertRebuild(request);
   }

   private void assertRebuild(HttpRequest request) {
      assertEquals("{\"rebuild\":{\"imageId\":3}}", request.getEntity());
   }

}
