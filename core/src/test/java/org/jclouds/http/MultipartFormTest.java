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
package org.jclouds.http;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.MultipartForm.Part;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests parsing of a request
 * 
 * @author Adrian Cole
 */
@Test(testName = "http.MultipartFormTest")
public class MultipartFormTest {
   String boundary = "------------------------------c88555ffd14e";

   public void testSinglePart() throws IOException {

      StringBuilder builder = new StringBuilder();
      addData(boundary, "hello", builder);
      builder.append("--").append(boundary).append("--").append("\r\n");
      String expects = builder.toString();
      assertEquals(expects.length(), 199);

      MultipartForm multipartForm = new MultipartForm(boundary, newPart("hello"));

      assertEquals(Utils.toStringAndClose(multipartForm.getData()), expects);
      assertEquals(multipartForm.getSize(), 199);
   }

   private Part newPart(String data) {
      return new MultipartForm.Part(ImmutableMultimap.of("Content-Disposition",
               "form-data; name=\"file\"; filename=\"testfile.txt\"", HttpHeaders.CONTENT_TYPE,
               MediaType.TEXT_PLAIN), data);
   }

   private void addData(String boundary, String data, StringBuilder builder) {
      builder.append("--").append(boundary).append("\r\n");
      builder.append("Content-Disposition").append(": ").append(
               "form-data; name=\"file\"; filename=\"testfile.txt\"").append("\r\n");
      builder.append("Content-Type").append(": ").append("text/plain").append("\r\n");
      builder.append("\r\n");
      builder.append(data).append("\r\n");
   }

   public void testMultipleParts() throws IOException {

      StringBuilder builder = new StringBuilder();
      addData(boundary, "hello", builder);
      addData(boundary, "goodbye", builder);

      builder.append("--").append(boundary).append("--").append("\r\n");
      String expects = builder.toString();

      assertEquals(expects.length(), 352);

      MultipartForm multipartForm = new MultipartForm(boundary, newPart("hello"),
               newPart("goodbye"));

      assertEquals(Utils.toStringAndClose(multipartForm.getData()), expects);
      assertEquals(multipartForm.getSize(), 352);
   }

}
