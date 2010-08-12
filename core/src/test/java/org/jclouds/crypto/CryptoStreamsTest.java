/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.crypto;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "jclouds.CryptoStreamsTest")
public class CryptoStreamsTest {

   @Test
   public void testBase64Encode() throws IOException {

      String encoded = CryptoStreams.base64Encode(Payloads.newStringPayload("hello world"));
      assertEquals(encoded, "aGVsbG8gd29ybGQ=");
   }

   @Test
   public void testBase64Decode() throws IOException {

      byte[] decoded = CryptoStreams.base64Decode(Payloads.newStringPayload("aGVsbG8gd29ybGQ="));
      assertEquals(new String(decoded, Charsets.UTF_8), "hello world");
   }

   @Test
   public void testHexEncode() throws IOException {

      String encoded = CryptoStreams.hexEncode(Payloads.newStringPayload("hello world"));
      assertEquals(encoded, "68656c6c6f20776f726c64");
   }

   @Test
   public void testHexDecode() throws IOException {

      byte[] decoded = CryptoStreams.hexDecode(Payloads.newStringPayload("68656c6c6f20776f726c64"));
      assertEquals(new String(decoded, Charsets.UTF_8), "hello world");
   }

}
