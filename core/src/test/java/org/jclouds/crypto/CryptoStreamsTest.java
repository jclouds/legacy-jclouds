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
@Test(groups = "unit", singleThreaded = true)
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
   public void testBase64DecodeUrlJson() throws IOException {
      byte[] decoded = CryptoStreams.base64("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9");
      assertEquals(new String(decoded, Charsets.UTF_8), "{\"alg\":\"RS256\",\"typ\":\"JWT\"}");
   }

   @Test
   public void testBase64DecodeUrlNoPadding() throws IOException {

      byte[] decoded = CryptoStreams
            .base64("eyJpc3MiOiI3NjEzMjY3OTgwNjktcjVtbGpsbG4xcmQ0bHJiaGc3NWVmZ2lncDM2bTc4ajVAZGV2ZWxvcGVyLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzY29wZSI6Imh0dHBzOi8vd3d3Lmdvb2dsZWFwaXMuY29tL2F1dGgvcHJlZGljdGlvbiIsImF1ZCI6Imh0dHBzOi8vYWNjb3VudHMuZ29vZ2xlLmNvbS9vL29hdXRoMi90b2tlbiIsImV4cCI6MTMyODU1NDM4NSwiaWF0IjoxMzI4NTUwNzg1fQ");

      assertEquals(new String(decoded, Charsets.UTF_8), "{"
            + "\"iss\":\"761326798069-r5mljlln1rd4lrbhg75efgigp36m78j5@developer.gserviceaccount.com\","
            + "\"scope\":\"https://www.googleapis.com/auth/prediction\","
            + "\"aud\":\"https://accounts.google.com/o/oauth2/token\"," + "\"exp\":1328554385," + "\"iat\":1328550785"
            + "}");
   }
   
   @Test
   public void testBase64EncodeUrlNoSinglePad() {
      assertEquals(CryptoStreams.base64("any carnal pleasu".getBytes(Charsets.UTF_8)), "YW55IGNhcm5hbCBwbGVhc3U=");
      assertEquals(CryptoStreams.base64Url("any carnal pleasu".getBytes(Charsets.UTF_8)), "YW55IGNhcm5hbCBwbGVhc3U");
   }

   @Test
   public void testBase64EncodeUrlNoDoublePad() {
      assertEquals(CryptoStreams.base64("any carnal pleas".getBytes(Charsets.UTF_8)), "YW55IGNhcm5hbCBwbGVhcw==");
      assertEquals(CryptoStreams.base64Url("any carnal pleas".getBytes(Charsets.UTF_8)), "YW55IGNhcm5hbCBwbGVhcw");
   }

   @Test
   public void testBase64EncodeUrlHyphenNotPlus() {
      assertEquals(CryptoStreams.base64("i?>".getBytes(Charsets.UTF_8)), "aT8+");
      assertEquals(CryptoStreams.base64Url("i?>".getBytes(Charsets.UTF_8)), "aT8-");
   }
   
   @Test
   public void testBase64EncodeUrlUnderscoreNotSlash() {
      assertEquals(CryptoStreams.base64("i??".getBytes(Charsets.UTF_8)), "aT8/");
      assertEquals(CryptoStreams.base64Url("i??".getBytes(Charsets.UTF_8)), "aT8_");
   }
   
   @Test
   public void testBase64DecodeWithoutSinglePad() {
      assertEquals(new String(CryptoStreams.base64("YW55IGNhcm5hbCBwbGVhc3U="), Charsets.UTF_8), "any carnal pleasu");
      assertEquals(new String(CryptoStreams.base64("YW55IGNhcm5hbCBwbGVhc3U"), Charsets.UTF_8), "any carnal pleasu");
   }
   
   @Test
   public void testBase64DecodeWithoutDoublePad() {
      assertEquals(new String(CryptoStreams.base64("YW55IGNhcm5hbCBwbGVhcw=="), Charsets.UTF_8), "any carnal pleas");
      assertEquals(new String(CryptoStreams.base64("YW55IGNhcm5hbCBwbGVhcw"), Charsets.UTF_8), "any carnal pleas");
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
