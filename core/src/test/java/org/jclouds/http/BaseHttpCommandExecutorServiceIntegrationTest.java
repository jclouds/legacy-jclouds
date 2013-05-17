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
package org.jclouds.http;

import static com.google.common.hash.Hashing.md5;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.String.format;
import static org.jclouds.http.options.GetOptions.Builder.tail;
import static org.jclouds.io.Payloads.newFilePayload;
import static org.jclouds.io.Payloads.newStringPayload;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.io.Payload;
import org.jclouds.util.Strings2;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.Files;

/**
 * Tests for functionality all {@link HttpCommandExecutorService http executor
 * services} must express. These tests will operate against an in-memory http
 * engine, so as to ensure end-to-end functionality works.
 * 
 * @author Adrian Cole
 */
@Test(threadPoolSize = 10, groups = "integration")
public abstract class BaseHttpCommandExecutorServiceIntegrationTest extends BaseJettyTest {

   @Test(invocationCount = 25, timeOut = 5000)
   public void testRequestFilter() {
      assertEquals(client.downloadFilter("", "filterme").trim(), "test");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetStringWithHeader() {
      assertEquals(client.download("", "test").trim(), "test");
   }

   @Test(invocationCount = 1, timeOut = 5000)
   public void testAlternateMethod() {
      assertEquals(client.rowdy("").trim(), XML);
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetString() {
      assertEquals(client.download("").trim(), XML);
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetStringViaRequest() throws IOException {
      HttpResponse getStringResponse = client.invoke(HttpRequest.builder().method("GET")
            .endpoint(format("http://localhost:%d/objects/", testPort)).build());
      assertEquals(Strings2.toString(getStringResponse.getPayload()).trim(), XML);
   }

   @DataProvider(name = "gets")
   public Object[][] createData() {
      return new Object[][] { { "object" }, { "/path" }, { "sp ace" }, { "unic₪de" }, { "qu?stion" } };
   }

   @Test(invocationCount = 5, timeOut = 5000, dataProvider = "gets")
   public void testGetStringSynch(String uri) {
      assertEquals(client.synch(uri).trim(), XML);
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetException() {
      assertEquals(client.downloadException("", tail(1)).trim(), "foo");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetSynchException() {
      assertEquals(client.synchException("", "").trim(), "foo");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetStringRedirect() {
      assertEquals(client.download("redirect").trim(), XML2);
   }

   @Test(invocationCount = 100, timeOut = 5000)
   public void testGetBigFile() throws IOException {
      InputStream input = getConsitution();
      try {
         assertValidMd5(input);
      } catch (RuntimeException e) {
         closeQuietly(input);
         // since we are parsing client side, and not through a response
         // handler, the user must retry directly. In this case, we are assuming
         // lightning doesn't strike twice in the same spot.
         if (getFirstThrowableOfType(e, IOException.class) != null) {
            input = getConsitution();
            assertValidMd5(input);
         }
      } finally {
         closeQuietly(input);
      }
   }

   private void assertValidMd5(final InputStream input) throws IOException {
      assertEquals(base64().encode(new ByteSource() {
         @Override
         public InputStream openStream() {
            return input;
         }
      }.hash(md5()).asBytes()), md5);
   }

   private InputStream getConsitution() throws MalformedURLException, IOException {
      URI constitutionUri = URI.create(format("http://localhost:%d/101constitutions", testPort));
      return constitutionUri.toURL().openStream();
   }

   /**
    * Tests sending a big file to the server. Note: this is a heavy test, takes
    * several minutes to finish.
    */
   @Test(invocationCount = 1)
   public void testUploadBigFile() throws IOException {
      String filename = "jclouds";
      File f = null;
      try {
         // create a file, twice big as free heap memory
         f = File.createTempFile(filename, "tmp");
         f.deleteOnExit();
         long length = (long) (Runtime.getRuntime().freeMemory() * 1.1);
         
         MessageDigest digester = md5Digest();

         CharSink fileSink = Files.asCharSink(f, Charsets.UTF_8);
         Writer out = null;
         try {
            out = fileSink.openStream();
            for (long i = 0; i < length; i++) {
               out.append('a');
               digester.update((byte) 'a');
            }
            out.flush();
         } finally {
            closeQuietly(out);
         }

         Payload payload = newFilePayload(f);
         byte[] digest = digester.digest();
         payload.getContentMetadata().setContentMD5(digest);
         Multimap<String, String> headers = client.postPayloadAndReturnHeaders("", payload);
         assertEquals(headers.get("x-Content-MD5"), ImmutableList.of(base64().encode(digest)));
         payload.release();
      } finally {
         if (f != null && f.exists())
            f.delete();
      }
   }

   private MessageDigest md5Digest() throws AssertionError {
      try {
         return MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException e) {
         throw new AssertionError(e);
      }
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPost() {
      assertEquals(client.post("", "foo").trim(), "fooPOST");
   }

   @Test(invocationCount = 1, timeOut = 5000)
   public void testPostAsInputStream() {
      AtomicInteger postFailures = new AtomicInteger();
      for (int i = 0; i < 5; i++)
         try {
            assertEquals(client.postAsInputStream("", "foo").trim(), "fooPOST");
         } catch (Exception e) {
            postFailures.incrementAndGet();
         }
      assertTrue(postFailures.get() > 0, "expected failures");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPostBinder() {
      assertEquals(client.postJson("", "foo").trim(), "{\"key\":\"foo\"}POST");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPostContentDisposition() {
      Payload payload = newStringPayload("foo");
      payload.getContentMetadata().setContentDisposition("attachment; filename=photo.jpg");
      Multimap<String, String> headers = client.postPayloadAndReturnHeaders("", payload);
      assertEquals(headers.get("x-Content-Disposition"), ImmutableList.of("attachment; filename=photo.jpg"));
      payload.release();
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPostContentEncoding() {
      Payload payload = newStringPayload("foo");
      payload.getContentMetadata().setContentEncoding("gzip");
      Multimap<String, String> headers = client.postPayloadAndReturnHeaders("", payload);
      assertEquals(headers.get("x-Content-Encoding"), ImmutableList.of("gzip"));
      payload.release();
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPostContentLanguage() {
      Payload payload = newStringPayload("foo");
      payload.getContentMetadata().setContentLanguage("mi, en");
      Multimap<String, String> headers = client.postPayloadAndReturnHeaders("", payload);
      assertEquals(headers.get("x-Content-Language"), ImmutableList.of("mi, en"));
      payload.release();
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPut() {
      assertEquals(client.upload("", "foo").trim(), "fooPUT");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPutRedirect() {
      assertEquals(client.upload("redirect", "foo").trim(), "fooPUTREDIRECT");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testHead() {
      assertTrue(client.exists(""), "head returned false");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetAndParseSax() {
      assertEquals(client.downloadAndParse(""), "whoppers");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testZeroLengthPut() {
     client.putNothing("");
   }
}
