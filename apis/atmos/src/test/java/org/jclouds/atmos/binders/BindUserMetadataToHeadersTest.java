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
package org.jclouds.atmos.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.atmos.domain.UserMetadata;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindUserMetadataToHeaders}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindUserMetadataToHeadersTest {
   Injector injector = Guice.createInjector();
   BindUserMetadataToHeaders binder = injector.getInstance(BindUserMetadataToHeaders.class);

   public void testMeta() {
      UserMetadata metadata = new UserMetadata();
      metadata.getMetadata().put("apple", "bear");
      metadata.getMetadata().put("sushi", "king");
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost"));
      request = binder.bindToRequest(request, metadata);
      assertEquals(request.getFirstHeaderOrNull("x-emc-meta"), "apple=bear,sushi=king");
   }

   public void testListableMeta() {
      UserMetadata metadata = new UserMetadata();
      metadata.getListableMetadata().put("apple", "bear");
      metadata.getListableMetadata().put("sushi", "king");
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost"));
      request = binder.bindToRequest(request, metadata);
      assertEquals(request.getFirstHeaderOrNull("x-emc-listable-meta"), "apple=bear,sushi=king");
   }

   public void testTags() {
      UserMetadata tagsdata = new UserMetadata();
      tagsdata.getTags().add("apple");
      tagsdata.getTags().add("sushi");
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost"));
      request = binder.bindToRequest(request, tagsdata);
      assertEquals(request.getFirstHeaderOrNull("x-emc-tags"), "apple,sushi");
   }

   public void testListableTags() {
      UserMetadata tagsdata = new UserMetadata();
      tagsdata.getListableTags().add("apple");
      tagsdata.getListableTags().add("sushi");
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost"));
      request = binder.bindToRequest(request, tagsdata);
      assertEquals(request.getFirstHeaderOrNull("x-emc-listable-tags"), "apple,sushi");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeUserMetadata() {
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      binder.bindToRequest(request, null);
   }
}
