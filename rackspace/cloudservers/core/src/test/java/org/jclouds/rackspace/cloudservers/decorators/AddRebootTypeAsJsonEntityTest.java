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
package org.jclouds.rackspace.cloudservers.decorators;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.RebootType;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code AddRebootTypeAsJsonEntity}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.AddRebootTypeAsJsonEntityTest")
public class AddRebootTypeAsJsonEntityTest {

   Injector injector = Guice.createInjector(new ParserModule());

   @Test(expectedExceptions = IllegalStateException.class)
   public void testPostIsIncorrect() {
      AddRebootTypeAsJsonEntity binder = new AddRebootTypeAsJsonEntity();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.decorateRequest(request, ImmutableMap.of("adminPass", "foo"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeRebootType() {
      AddRebootTypeAsJsonEntity binder = new AddRebootTypeAsJsonEntity();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.decorateRequest(request, new File("foo"));
   }

   @Test
   public void testHard() {
      AddRebootTypeAsJsonEntity binder = new AddRebootTypeAsJsonEntity();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.decorateRequest(request, RebootType.HARD);
      assertEquals("{\"reboot\":{\"type\":\"HARD\"}}", request.getEntity());
   }

   @Test
   public void testSoft() {
      AddRebootTypeAsJsonEntity binder = new AddRebootTypeAsJsonEntity();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.decorateRequest(request, RebootType.SOFT);
      assertEquals("{\"reboot\":{\"type\":\"SOFT\"}}", request.getEntity());
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      AddRebootTypeAsJsonEntity binder = new AddRebootTypeAsJsonEntity();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.decorateRequest(request, null);
   }
}
