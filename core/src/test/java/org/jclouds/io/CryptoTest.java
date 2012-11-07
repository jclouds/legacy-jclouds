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
package org.jclouds.io;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

import org.jclouds.PerformanceTest;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * This tests the performance of Digest commands.
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "performance", singleThreaded = true, timeOut = 2 * 60 * 1000, testName = "CryptoTest")
public class CryptoTest extends PerformanceTest {

   protected Crypto crypto;

   @BeforeTest
   protected void createCrypto() {
      Injector i = Guice.createInjector();
      crypto = i.getInstance(Crypto.class);
   }

   public static final Object[][] base64KeyMessageDigest = {
            { parseBase64Binary("CwsLCwsLCwsLCwsLCwsLCwsLCws="), "Hi There", "thcxhlUFcmTii8C2+zeMjvFGvgA=" },
            { parseBase64Binary("SmVmZQ=="), "what do ya want for nothing?", "7/zfauXrL6LSdBbV8YTfnCWafHk=" },
            { parseBase64Binary("DAwMDAwMDAwMDAwMDAwMDAwMDAw="), "Test With Truncation", "TBoDQktV4H/n8nvh1Yu5MkqaWgQ=" },
            { parseBase64Binary("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqo="),
                     "Test Using Larger Than Block-Size Key - Hash Key First", "qkrl4VJy0A6VcFY3zoo7Ve1AIRI=" },
            { parseBase64Binary("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqo="),
                     "Test Using Larger Than Block-Size Key and Larger Than One Block-Size Data",
                     "6OmdD0UjfXhta7qnllx4CLv/GpE=" } };

   @DataProvider(name = "hmacsha1")
   public Object[][] createData1() {
      return base64KeyMessageDigest;
   }

   @Test(dataProvider = "hmacsha1")
   public void testHmacSha1Base64(byte[] key, String message, String base64Digest) throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidKeyException, IOException {
      String b64 = CryptoStreams.macBase64(InputSuppliers.of(message), crypto.hmacSHA1(key));
      assertEquals(b64, base64Digest);
   }

   @Test(dataProvider = "hmacsha1")
   void testDigestSerialResponseTime(byte[] key, String message, String base64Digest) throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidKeyException, IOException {
      for (int i = 0; i < 10000; i++)
         testHmacSha1Base64(key, message, base64Digest);
   }

   @Test(dataProvider = "hmacsha1")
   void testDigestParallelResponseTime(final byte[] key, final String message, final String base64Digest)
            throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException,
            ExecutionException {
      CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(exec);
      for (int i = 0; i < 10000; i++)
         completer.submit(new Callable<Boolean>() {
            public Boolean call() throws Exception {
               testHmacSha1Base64(key, message, base64Digest);
               return true;
            }
         });
      for (int i = 0; i < 10000; i++)
         assert completer.take().get();
   }

   @DataProvider(name = "eTag")
   public Object[][] createMD5Data() {
      return hexMD5MessageDigest;
   }

   public static final Object[][] hexMD5MessageDigest = { { "apple", "1f3870be274f6c49b3e31a0c6728957f" },
            { "bear", "893b56e3cfe153fb770a120b83bac20c" }, { "candy", "c48ba993d35c3abe0380f91738fe2a34" },
            { "dogma", "95eb470e4faee302e9cd3063b1923dab" }, { "emma", "00a809937eddc44521da9521269e75c6" } };

   @Test(dataProvider = "eTag")
   public void testMD5Digest(String message, String hexMD5Digest) throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidKeyException, IOException {
      String b64 = CryptoStreams.md5Hex(InputSuppliers.of(message));
      assertEquals(hexMD5Digest, b64);
   }

   public void testSHA1() {
      crypto.sha1();
   }

   public void testSHA256() {
      crypto.sha256();
   }

   public void testSHA512() {
      crypto.sha512();
   }
}
