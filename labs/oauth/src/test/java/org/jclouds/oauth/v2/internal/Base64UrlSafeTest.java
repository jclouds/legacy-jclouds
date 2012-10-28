/*
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

package org.jclouds.oauth.v2.internal;

import com.google.common.base.Charsets;
import com.sun.jersey.core.util.Base64;
import org.jclouds.crypto.CryptoStreams;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Tests that the Base64 implementations used to Base64 encode the tokens are Url safe.
 *
 * @author David Alves
 */
@Test(groups = "unit")
public class Base64UrlSafeTest {

   public static final String STRING_THAT_GENERATES_URL_UNSAFE_BASE64_ENCODING = "§1234567890'+±!\"#$%&/()" +
           "=?*qwertyuiopº´WERTYUIOPªàsdfghjklç~ASDFGHJKLÇ^<zxcvbnm," +
           ".->ZXCVBNM;:_@€";


   public void testJcloudsCoreBase64IsNotUrlSafe() {
      String encoded = new String(Base64.encode(STRING_THAT_GENERATES_URL_UNSAFE_BASE64_ENCODING.getBytes(Charsets
              .UTF_8)), Charsets.UTF_8);
      assertTrue(encoded.contains("+"), encoded);
      assertTrue(encoded.contains("/"), encoded);
   }

   public void testUsedBase64IsUrlSafe() {
      String encoded =  CryptoStreams.base64Url(
              STRING_THAT_GENERATES_URL_UNSAFE_BASE64_ENCODING.getBytes(Charsets.UTF_8));
      assertTrue(!encoded.contains("+"));
      assertTrue(!encoded.contains("/"));
      assertTrue(!encoded.endsWith("="));
   }
}
