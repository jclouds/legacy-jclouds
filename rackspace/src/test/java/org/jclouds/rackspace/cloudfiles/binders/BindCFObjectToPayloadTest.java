/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.http.HttpRequest;
import org.jclouds.rackspace.cloudfiles.CloudFilesContextBuilder;
import org.jclouds.rackspace.cloudfiles.CloudFilesPropertiesBuilder;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobToObject;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

/**
 * Tests parsing of a request
 * 
 * @author Adrian Cole
 */
@Test(testName = "cloudfiles.BindCFObjectToPayloadTest")
public class BindCFObjectToPayloadTest {
   private Factory blobProvider;
   private Provider<BindCFObjectToPayload> binderProvider;
   private BlobToObject blob2Object;

   public BindCFObjectToPayloadTest() {
      Injector injector = new CloudFilesContextBuilder(new CloudFilesPropertiesBuilder(
               new Properties()).provider("cloudfiles").credentials("id", "secret").build())
               .buildInjector();

      blobProvider = injector.getInstance(Blob.Factory.class);
      binderProvider = injector.getInstance(Key
               .get(new TypeLiteral<Provider<BindCFObjectToPayload>>() {
               }));
      blob2Object = injector.getInstance(BlobToObject.class);
   }

   public CFObject testBlob() {

      Blob TEST_BLOB = blobProvider.create(null);
      TEST_BLOB.getMetadata().setName("hello");
      TEST_BLOB.setPayload("hello");
      TEST_BLOB.getMetadata().setContentType(MediaType.TEXT_PLAIN);
      return blob2Object.apply(TEST_BLOB);
   }

   public void testNormal() throws IOException {

      BindCFObjectToPayload binder = binderProvider.get();

      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost:8001"));
      binder.bindToRequest(request, testBlob());

      assertEquals(Utils.toStringAndClose(request.getPayload().getInput()), "hello");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH), 5 + "");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE), MediaType.TEXT_PLAIN);
   }

   public void testMD5InHex() throws IOException {

      BindCFObjectToPayload binder = binderProvider.get();

      CFObject blob = testBlob();
      blob.generateMD5();
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost:8001"));
      binder.bindToRequest(request, blob);

      assertEquals(Utils.toStringAndClose(request.getPayload().getInput()), "hello");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH), 5 + "");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.ETAG),
               "5d41402abc4b2a76b9719d911017c592");
      assertEquals(request.getFirstHeaderOrNull("Content-MD5"), null);
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE), MediaType.TEXT_PLAIN);
   }
}
