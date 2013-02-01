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
package org.jclouds.http;

import static org.testng.Assert.assertEquals;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.InputSuppliers;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.util.Strings2;
import org.jclouds.util.Throwables2;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;
import com.google.common.io.Closeables;

/**
 * Tests for functionality all HttpCommandExecutorServices must express. These tests will operate
 * against an in-memory http engine, so as to ensure end-to-end functionality works.
 * 
 * @author Adrian Cole
 */
@Test(threadPoolSize = 10, groups = "integration")
public abstract class BaseHttpCommandExecutorServiceIntegrationTest extends BaseJettyTest {

   @Test(invocationCount = 25, timeOut = 5000)
   public void testRequestFilter() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      assertEquals(client.downloadFilter("", "filterme").trim(), "test");
   }

   // TODO: filtering redirect test

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetStringWithHeader() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      assertEquals(client.download("", "test").trim(), "test");
   }

   @Test(invocationCount = 1, timeOut = 5000)
   public void testAlternateMethod() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      assertEquals(client.rowdy("").trim(), XML);
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetString() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
      assertEquals(client.download("").trim(), XML);
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetStringViaRequest() throws ExecutionException, InterruptedException, TimeoutException, IOException {
      assertEquals(
            Strings2.toStringAndClose(
                  client.invoke(
                        HttpRequest.builder().method("GET")
                              .endpoint("http://localhost:" + testPort + "/objects/").build()).getPayload()
                        .getInput()).trim(), XML);
   }

   @DataProvider(name = "gets")
   public Object[][] createData() {
      return new Object[][] { { "object" }, { "/path" }, { "sp ace" }, { "unic₪de" }, { "qu?stion" } };
   }

   @Test(invocationCount = 5, timeOut = 5000, dataProvider = "gets")
   public void testGetStringSynch(String uri) throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      // TODO why need trim?
      assertEquals(client.synch(uri).trim(), XML);
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetException() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      assertEquals(client.downloadException("", GetOptions.Builder.tail(1)).trim(), "foo");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetSynchException() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      assertEquals(client.synchException("", "").trim(), "foo");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetStringRedirect() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      assertEquals(client.download("redirect").trim(), XML2);
   }

   @Test(invocationCount = 100, timeOut = 5000)
   public void testGetBigFile() throws ExecutionException, InterruptedException, TimeoutException, IOException {
      InputStream input = getConsitution();
      try {
         assertEquals(CryptoStreams.md5Base64(InputSuppliers.of(input)), md5);
      } catch (RuntimeException e) {
         Closeables.closeQuietly(input);
         // since we are parsing client side, and not through a response
         // handler, the user
         // must retry directly. In this case, we are assuming lightning doesn't
         // strike
         // twice in the same spot.
         if (Throwables2.getFirstThrowableOfType(e, IOException.class) != null) {
            input = getConsitution();
            assertEquals(CryptoStreams.md5Base64(InputSuppliers.of(input)), md5);
         }
      }
   }

   private InputStream getConsitution() {
      InputStream input = context.utils().http()
            .get(URI.create(String.format("http://localhost:%d/%s", testPort, "101constitutions")));
      return input;
   }

   @Test(enabled = false, invocationCount = 5, timeOut = 5000)
   public void testGetStringPermanentRedirect() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      // GetString get = factory.createGetString("permanentredirect");
      // assert get != null;
      // client.submit(get);
      // assertEquals(get.get(10, TimeUnit.SECONDS).trim(), XML2);
      // TODO assert misses are only one, as permanent redirects paths should be
      // remembered.
   }

   /**
    * Tests sending a big file to the server. Note: this is a heavy test, takes several minutes to
    * finish.
    * 
    * @throws java.io.IOException
    */
   @Test(invocationCount = 1)
   public void testUploadBigFile() throws IOException {
      String filename = "jclouds";
      OutputStream os = null;
      File f = null;
      try {
         // create a file, twice big as free heap memory
         f = File.createTempFile(filename, "tmp");
         f.deleteOnExit();
         long length = (long) (Runtime.getRuntime().freeMemory() * 1.1);
         os = new BufferedOutputStream(new FileOutputStream(f.getAbsolutePath()));
         MessageDigest digester = context.utils().crypto().md5();

         ByteArrayOutputStream out = new ByteArrayOutputStream();
         try {
            for (long i = 0; i < length; i++) {
               digester.update((byte) 'a');
               os.write((byte) 'a');
            }
            os.flush();
         } catch (IOException e) {
            throw new RuntimeException(e);
         } finally {
            Closeables.closeQuietly(out);
         }

         Payload payload = Payloads.newFilePayload(f);
         byte[] digest = digester.digest();
         payload.getContentMetadata().setContentMD5(digest);
         Multimap<String, String> headers = client.postPayloadAndReturnHeaders("", payload);
         assertEquals(headers.get("x-Content-MD5"),
               Collections.singleton(CryptoStreams.base64Encode(InputSuppliers.of(digest))));
         payload.release();
      } finally {
         if (os != null)
            os.close();
         if (f != null && f.exists())
            f.delete();
      }
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPost() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
      assertEquals(client.post("", "foo").trim(), "fooPOST");
   }

   @Test(invocationCount = 1, timeOut = 5000)
   public void testPostAsInputStream() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      AtomicInteger postFailures = new AtomicInteger();
      for (int i = 0; i < 5; i++)
         try {
            assertEquals(client.postAsInputStream("", "foo").trim(), "fooPOST");
         } catch (Exception e) {
            postFailures.incrementAndGet();
         }
      assert postFailures.get() > 0;
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPostBinder() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      assertEquals(client.postJson("", "foo").trim(), "{\"key\":\"foo\"}POST");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPostContentDisposition() throws ExecutionException, InterruptedException, TimeoutException,
         IOException {
      Payload payload = Payloads.newStringPayload("foo");
      payload.getContentMetadata().setContentDisposition("attachment; filename=photo.jpg");
      Multimap<String, String> headers = client.postPayloadAndReturnHeaders("", payload);
      assertEquals(headers.get("x-Content-Disposition"), Collections.singleton("attachment; filename=photo.jpg"));
      payload.release();
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPostContentEncoding() throws ExecutionException, InterruptedException, TimeoutException, IOException {
      Payload payload = Payloads.newStringPayload("foo");
      payload.getContentMetadata().setContentEncoding("gzip");
      Multimap<String, String> headers = client.postPayloadAndReturnHeaders("", payload);
      assertEquals(headers.get("x-Content-Encoding"), Collections.singleton("gzip"));
      payload.release();
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPostContentLanguage() throws ExecutionException, InterruptedException, TimeoutException, IOException {
      Payload payload = Payloads.newStringPayload("foo");
      payload.getContentMetadata().setContentLanguage("mi, en");
      Multimap<String, String> headers = client.postPayloadAndReturnHeaders("", payload);
      assertEquals(headers.get("x-Content-Language"), Collections.singleton("mi, en"));
      payload.release();
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPut() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
      assertEquals(client.upload("", "foo").trim(), "fooPUT");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testPutRedirect() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      assertEquals(client.upload("redirect", "foo").trim(), "fooPUTREDIRECT");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testHead() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
      assert client.exists("");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testGetAndParseSax() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      assertEquals(client.downloadAndParse(""), "whoppers");
   }

   @Test(invocationCount = 5, timeOut = 5000)
   public void testZeroLengthPut() {
     client.putNothing("");
   }
}
