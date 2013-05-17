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
package org.jclouds.io.payloads;

import static com.google.common.net.MediaType.PLAIN_TEXT_UTF_8;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.Part.PartOptions;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

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

      assertEquals(Strings2.toString(multipartForm), expects);
      assertEquals(multipartForm.getContentMetadata().getContentLength(), Long.valueOf(199));
   }

   public static class MockFilePayload extends FilePayload {

      private final StringPayload realPayload;

      public MockFilePayload(String content) {
         super(createMockFile(content));
         this.realPayload = Payloads.newStringPayload(content);
      }

      private static File createMockFile(String content) {
         File file = createMock(File.class);
         expect(file.length()).andReturn((long) content.length());
         expect(file.exists()).andReturn(true);
         expect(file.getName()).andReturn("testfile.txt");
         replay(file);
         return file;
      }

      @Override
      public InputStream getInput() {
         return realPayload.getInput();
      }

      @Override
      public boolean isRepeatable() {
         return realPayload.isRepeatable();
      }

      @Override
      public void writeTo(OutputStream outstream) throws IOException {
         realPayload.writeTo(outstream);
      }

   }

   private Part newPart(String data) {
      return Part.create("file", new MockFilePayload(data),
            new PartOptions().contentType(PLAIN_TEXT_UTF_8.withoutParameters().toString()));
   }

   private void addData(String boundary, String data, StringBuilder builder) {
      builder.append("--").append(boundary).append("\r\n");
      builder.append("Content-Disposition").append(": ").append("form-data; name=\"file\"; filename=\"testfile.txt\"")
               .append("\r\n");
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

      MultipartForm multipartForm = new MultipartForm(boundary, newPart("hello"), newPart("goodbye"));

      assertEquals(Strings2.toString(multipartForm), expects);

      // test repeatable
      assert multipartForm.isRepeatable();
      assertEquals(Strings2.toString(multipartForm), expects);
      assertEquals(multipartForm.getContentMetadata().getContentLength(), Long.valueOf(352));
   }

}
