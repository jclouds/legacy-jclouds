/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.blobstore.binders;

import static org.jclouds.io.payloads.MultipartForm.BOUNDARY;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.http.HttpRequest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * Tests parsing of a request
 * 
 * @author Adrian Cole
 */
@Test(testName = "blobstore.BindBlobToMultipartFormTest")
public class BindBlobToMultipartFormTest {
   private static Factory blobProvider;
   public static final String EXPECTS;
   public static final Blob TEST_BLOB;

   static {
      blobProvider = ContextBuilder.newBuilder("transient").buildInjector().getInstance(Blob.Factory.class);
      StringBuilder builder = new StringBuilder("--");
      addData(BOUNDARY, "hello", builder);
      builder.append("--").append(BOUNDARY).append("--").append("\r\n");
      EXPECTS = builder.toString();
      TEST_BLOB = blobProvider.create(null);
      TEST_BLOB.getMetadata().setName("hello");
      TEST_BLOB.setPayload("hello");
      TEST_BLOB.getMetadata().getContentMetadata().setContentType(MediaType.TEXT_PLAIN);
   }

   public void testSinglePart() throws IOException {

      assertEquals(EXPECTS.length(), 113);

      BindBlobToMultipartForm binder = new BindBlobToMultipartForm();

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://localhost:8001").build();
      binder.bindToRequest(request, TEST_BLOB);

      assertEquals(Strings2.toString(request.getPayload()), EXPECTS);
      assertEquals(request.getPayload().getContentMetadata().getContentLength(), Long.valueOf(113));

      assertEquals(request.getPayload().getContentMetadata().getContentType(), "multipart/form-data; boundary="
            + BOUNDARY);
   }

   private static void addData(String boundary, String data, StringBuilder builder) {
      builder.append(boundary).append("\r\n");
      builder.append("Content-Disposition").append(": ").append("form-data; name=\"hello\"").append("\r\n");
      builder.append("Content-Type").append(": ").append("text/plain").append("\r\n");
      builder.append("\r\n");
      builder.append(data).append("\r\n");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeBlob() {
      BindBlobToMultipartForm binder = new BindBlobToMultipartForm();
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost").build();;
      binder.bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindBlobToMultipartForm binder = new BindBlobToMultipartForm();
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      binder.bindToRequest(request, null);
   }
}
