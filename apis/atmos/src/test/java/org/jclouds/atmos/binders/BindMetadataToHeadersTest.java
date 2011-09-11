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
package org.jclouds.atmos.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindMetadataToHeaders}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindMetadataToHeadersTest {
   Injector injector = Guice.createInjector();
   BindMetadataToHeaders binder = injector.getInstance(BindMetadataToHeaders.class);

   public void testGood() {
      AtmosObject object = injector.getInstance(AtmosObject.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      object.setPayload(payload);
      object.getUserMetadata().getListableMetadata().put("apple", "bear");
      object.getUserMetadata().getListableMetadata().put("sushi", "king");
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost"));
      request = binder.bindToRequest(request, object);
      assertEquals(request.getFirstHeaderOrNull("x-emc-listable-meta"), "apple=bear,sushi=king");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeAtmosObject() {
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      binder.bindToRequest(request, null);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullPayloadIsBad() {
      AtmosObject object = injector.getInstance(AtmosObject.Factory.class).create(null);
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost"));
      binder.bindToRequest(request, object);
   }
   
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNullContentLengthIllegal() {
      AtmosObject object = injector.getInstance(AtmosObject.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(null);
      object.setPayload(payload);
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost"));
      binder.bindToRequest(request, object);
   }

 
}
