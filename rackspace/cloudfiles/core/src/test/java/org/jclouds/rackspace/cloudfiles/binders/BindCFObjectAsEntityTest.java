/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rackspace.cloudfiles.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * Tests parsing of a request
 * 
 * @author Adrian Cole
 */
@Test(testName = "cloudfiles.BindCFObjectAsEntityTest")
public class BindCFObjectAsEntityTest {
   private Factory blobProvider;

   public BindCFObjectAsEntityTest() {
      blobProvider = Guice.createInjector(new BlobStoreObjectModule()).getInstance(
               Blob.Factory.class);
   }

   public Blob testBlob() {

      Blob TEST_BLOB = blobProvider.create(null);
      TEST_BLOB.getMetadata().setName("hello");
      TEST_BLOB.setData("hello");
      TEST_BLOB.getMetadata().setContentType(MediaType.TEXT_PLAIN);
      return TEST_BLOB;
   }

   public void testNormal() throws IOException {

      BindCFObjectAsEntity binder = new BindCFObjectAsEntity("test");

      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost:8001"));
      binder.bindToRequest(request, testBlob());

      assertEquals(request.getEntity(), "hello");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH), 5 + "");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE), MediaType.TEXT_PLAIN);
   }

   public void testMD5InHex() throws IOException {

      BindCFObjectAsEntity binder = new BindCFObjectAsEntity("test");

      Blob blob = testBlob();
      blob.generateMD5();
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost:8001"));
      binder.bindToRequest(request, blob);

      assertEquals(request.getEntity(), "hello");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH), 5 + "");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.ETAG),
               "5d41402abc4b2a76b9719d911017c592");
      assertEquals(request.getFirstHeaderOrNull("Content-MD5"), null);
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE), MediaType.TEXT_PLAIN);
   }
}
