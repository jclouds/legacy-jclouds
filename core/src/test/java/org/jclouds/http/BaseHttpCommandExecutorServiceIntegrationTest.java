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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.encryption.internal.JCEEncryptionService;
import org.jclouds.http.options.GetOptions;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Closeables;

/**
 * Tests for functionality all HttpCommandExecutorServices must express. These tests will operate
 * against an in-memory http engine, so as to ensure end-to-end functionality works.
 * 
 * @author Adrian Cole
 */
@Test(threadPoolSize = 10, groups = "integration", sequential = true)
public abstract class BaseHttpCommandExecutorServiceIntegrationTest extends BaseJettyTest {

   @Test(invocationCount = 25, timeOut = 5000)
   public void testRequestFilter() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.downloadFilter("", "filterme").trim(), "test");
   }

   // TODO: filtering redirect test

   @Test(invocationCount = 25, timeOut = 5000)
   public void testGetStringWithHeader() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.download("", "test").trim(), "test");
   }

   @Test(invocationCount = 25, timeOut = 5000)
   public void testGetString() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.download("").trim(), XML);
   }

   @DataProvider(name = "gets")
   public Object[][] createData() {
      return new Object[][] { { "object" }, { "/path" }, { "sp ace" }, { "unic₪de" },
               { "qu?stion" } };
   }

   @Test(invocationCount = 25, timeOut = 5000, dataProvider = "gets")
   public void testGetStringSynch(String uri) throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      // TODO why need trim?
      assertEquals(client.synch(uri).trim(), XML);
   }

   @Test(invocationCount = 25, timeOut = 5000)
   public void testGetException() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.downloadException("", GetOptions.Builder.tail(1)).trim(), "foo");
   }

   @Test(invocationCount = 25, timeOut = 5000)
   public void testGetSynchException() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.synchException("", "").trim(), "foo");
   }

   @Test(invocationCount = 25, timeOut = 5000)
   public void testGetStringRedirect() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.download("redirect").trim(), XML2);
   }

   @Test(invocationCount = 100, timeOut = 5000)
   public void testGetBigFile() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(encryptionService.toBase64String(encryptionService.md5(client
               .downloadStream("101constitutions"))), md5);
   }

   @Test(enabled = false, invocationCount = 25, timeOut = 5000)
   public void testGetStringPermanentRedirect() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      // GetString get = factory.createGetString("permanentredirect");
      // assert get != null;
      // client.submit(get);
      // assertEquals(get.get(10, TimeUnit.SECONDS).trim(), XML2);
      // TODO assert misses are only one, as permanent redirects paths should be remembered.
   }

   @Test(invocationCount = 25, timeOut = 5000)
   public void testPost() throws MalformedURLException, ExecutionException, InterruptedException,
            TimeoutException {
      assertEquals(client.post("", "foo").trim(), "fooPOST");
   }

   @Test(invocationCount = 25, timeOut = 10000)
   public void testPostAsInputStream() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      try {
         assertEquals(client.postAsInputStream("", "foo").trim(), "fooPOST");
      } catch (Exception e) {
         postFailures.incrementAndGet();
      }
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

         MessageDigest eTag = JCEEncryptionService.getDigest();
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         try {
            for (long i = 0; i < length; i++) {
               eTag.update((byte) 'a');
               os.write((byte) 'a');
            }
            os.flush();
         } catch (IOException e) {
            throw new RuntimeException(e);
         } finally {
            Closeables.closeQuietly(out);
         }

         // upload and verify the response
         assertEquals(client.postWithMd5("fileso",
                  this.encryptionService.toBase64String(eTag.digest()), f).trim(), "created");

      } finally {
         if (os != null)
            os.close();
         if (f != null && f.exists())
            f.delete();
      }
   }

   protected AtomicInteger postFailures = new AtomicInteger();

   @BeforeTest
   void resetCounters() {
      postFailures.set(0);
   }

   @Test(dependsOnMethods = "testPostAsInputStream")
   public void testPostResults() {
      // failures happen when trying to replay inputstreams
      assert postFailures.get() > 0;
   }

   @Test(invocationCount = 25, timeOut = 5000)
   public void testPostBinder() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.postJson("", "foo").trim(), "{\"key\":\"foo\"}POST");
   }

   @Test(invocationCount = 25, timeOut = 5000)
   public void testPut() throws MalformedURLException, ExecutionException, InterruptedException,
            TimeoutException {
      assertEquals(client.upload("", "foo").trim(), "fooPUT");
   }

   @Test(invocationCount = 25, timeOut = 5000)
   public void testPutRedirect() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.upload("redirect", "foo").trim(), "fooPUTREDIRECT");
   }

   @Test(invocationCount = 25, timeOut = 5000)
   public void testKillRobotSlowly() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.action("robot", "kill", ImmutableMap.of("death", "slow")).trim(),
               "robot->kill:{death=slow}");
   }

   @Test(invocationCount = 25, timeOut = 5000)
   public void testHead() throws MalformedURLException, ExecutionException, InterruptedException,
            TimeoutException {
      assert client.exists("");
   }

   @Test(invocationCount = 25, timeOut = 5000)
   public void testGetAndParseSax() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.downloadAndParse(""), "whoppers");
   }
}
