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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.BaseEncoding.base64Url;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

/**
 * Tests that the Base64 implementations used to Base64 encode the tokens are Url safe.
 * 
 * @author David Alves
 */
@Test(groups = "unit")
public class Base64UrlSafeTest {

   public static final String STRING_THAT_GENERATES_URL_UNSAFE_BASE64_ENCODING = "§1234567890'+±!\"#$%&/()"
         + "=?*qwertyuiopº´WERTYUIOPªàsdfghjklç~ASDFGHJKLÇ^<zxcvbnm,.->ZXCVBNM;:_@€";

   public void testUsedBase64IsUrlSafe() {
      String encoded = base64Url().omitPadding().encode(STRING_THAT_GENERATES_URL_UNSAFE_BASE64_ENCODING.getBytes(UTF_8));
      assertTrue(!encoded.contains("+"));
      assertTrue(!encoded.contains("/"));
      assertTrue(!encoded.endsWith("="));
   }
}
