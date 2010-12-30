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

package org.jclouds.rackspace.cloudfiles.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code BindCFObjectMetadataToRequest}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindCFObjectMetadataToRequestTest")
public class BindCFObjectMetadataToRequestTest extends RestClientTest<CloudFilesAsyncClient> {

   @Test
   public void testPassWithMinimumDetailsAndPayload5GB() {
      CFObject object = injector.getInstance(CFObject.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5 * 1024 * 1024 * 1024l);
      object.setPayload(payload);
      object.getInfo().setName("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindCFObjectMetadataToRequest binder = injector.getInstance(BindCFObjectMetadataToRequest.class);

      assertEquals(binder.bindToRequest(request, object),
            HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build());
   }

   @Test
   public void testExtendedPropertiesBind() {
      CFObject object = injector.getInstance(CFObject.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5 * 1024 * 1024 * 1024l);
      object.setPayload(payload);
      object.getInfo().setName("foo");
      object.getInfo().getMetadata().putAll(ImmutableMap.of("foo", "bar"));

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindCFObjectMetadataToRequest binder = injector.getInstance(BindCFObjectMetadataToRequest.class);

      assertEquals(
            binder.bindToRequest(request, object),
            HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost"))
                  .headers(ImmutableMultimap.of("X-Object-Meta-foo", "bar")).build());
   }

   public void testNoContentLengthIsChunked() {
      CFObject object = injector.getInstance(CFObject.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(null);
      object.setPayload(payload);
      object.getInfo().setName("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindCFObjectMetadataToRequest binder = injector.getInstance(BindCFObjectMetadataToRequest.class);

      assertEquals(
            binder.bindToRequest(request, object),
            HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost"))
                  .headers(ImmutableMultimap.of("Transfer-Encoding", "chunked")).build());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNoNameIsBad() {
      CFObject object = injector.getInstance(CFObject.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5368709120000l);
      object.setPayload(payload);

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindCFObjectMetadataToRequest binder = injector.getInstance(BindCFObjectMetadataToRequest.class);
      binder.bindToRequest(request, object);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testOver5GBIsBad() {
      CFObject object = injector.getInstance(CFObject.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5 * 1024 * 1024 * 1024l + 1);
      object.setPayload(payload);
      object.getInfo().setName("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindCFObjectMetadataToRequest binder = injector.getInstance(BindCFObjectMetadataToRequest.class);
      binder.bindToRequest(request, object);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeCFObject() {
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      injector.getInstance(BindCFObjectMetadataToRequest.class).bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindCFObjectMetadataToRequest binder = injector.getInstance(BindCFObjectMetadataToRequest.class);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      binder.bindToRequest(request, null);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<CloudFilesAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<CloudFilesAsyncClient>>() {
      };
   }

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("cloudfiles", "identity", "credential", new Properties());
   }
}