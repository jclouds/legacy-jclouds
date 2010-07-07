/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.chef.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "chef.BindHexEncodedMD5sToJsonPayloadTest")
public class BindHexEncodedMD5sToJsonPayloadTest {

   Injector injector = Guice.createInjector(new ParserModule());

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeIterable() {
      BindHexEncodedMD5sToJsonPayload binder = new BindHexEncodedMD5sToJsonPayload();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, new File("foo"));
   }

   @Test
   public void testCorrect() {
      BindHexEncodedMD5sToJsonPayload binder = new BindHexEncodedMD5sToJsonPayload();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, ImmutableSet.of("abddef", "1234"));
      assertEquals(request.getPayload().getRawContent(),
               "{\"checksums\":{\"abddef\":null,\"1234\":null}}");
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindHexEncodedMD5sToJsonPayload binder = new BindHexEncodedMD5sToJsonPayload();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, null);
   }

}
