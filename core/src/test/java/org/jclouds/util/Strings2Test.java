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

package org.jclouds.util;

import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class Strings2Test {

   public void testIsEncoded() {
      assert Strings2.isUrlEncoded("/read-tests/%73%6f%6d%65%20%66%69%6c%65");
      assert !Strings2.isUrlEncoded("/read-tests/ tep");
   }

   public void testNoDoubleEncode() {
      assertEquals(Strings2.urlEncode("/read-tests/%73%6f%6d%65%20%66%69%6c%65", '/'),
            "/read-tests/%73%6f%6d%65%20%66%69%6c%65");
      assertEquals(Strings2.urlEncode("/read-tests/ tep", '/'), "/read-tests/%20tep");
   }
   
   public void testReplaceTokens() throws UnsupportedEncodingException {
      assertEquals(Strings2.replaceTokens("hello {where}", ImmutableMap.of("where", "world")), "hello world");
   }

}
