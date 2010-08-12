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

import java.util.regex.Matcher;

import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "jclouds.PatternsTest")
public class PatternsTest {

   public void testJSON_STRING_PATTERN1() {
      Matcher matcher = Patterns.JSON_STRING_PATTERN.matcher("hello");
      assert (matcher.find());
   }

   public void testJSON_STRING_PATTERN2() {
      Matcher matcher = Patterns.JSON_STRING_PATTERN.matcher("hello world!");
      assert (matcher.find());
   }

   public void testJSON_STRING_PATTERN3() {
      Matcher matcher = Patterns.JSON_STRING_PATTERN.matcher("\"hello world!\"");
      assert (!matcher.find());
   }

   public void testJSON_STRING_PATTERN4() {
      Matcher matcher = Patterns.JSON_STRING_PATTERN.matcher("[hello world!]");
      assert (!matcher.find());
   }

   public void testJSON_STRING_PATTERN5() {
      Matcher matcher = Patterns.JSON_STRING_PATTERN.matcher("{hello world!}");
      assert (!matcher.find());
   }

   public void testJSON_NUMBER_PATTERN1() {
      Matcher matcher = Patterns.JSON_NUMBER_PATTERN.matcher("1");
      assert (matcher.find());
   }

   public void testJSON_NUMBER_PATTERN2() {
      Matcher matcher = Patterns.JSON_NUMBER_PATTERN.matcher("1.1");
      assert (matcher.find());
   }

   public void testJSON_NUMBER_PATTERN3() {
      Matcher matcher = Patterns.JSON_NUMBER_PATTERN.matcher("\"1.1\"");
      assert (!matcher.find());
   }

   public void testJSON_NUMBER_PATTERN4() {
      Matcher matcher = Patterns.JSON_NUMBER_PATTERN.matcher("\"1\"");
      assert (!matcher.find());
   }

   public void testREST_CONTEXT_BUILDER() {
      Matcher matcher = Patterns.REST_CONTEXT_BUILDER
            .matcher("org.jclouds.rest.RestContextBuilder<java.lang.String,java.lang.Integer>");
      assert (matcher.find());
      assertEquals(matcher.group(1), "org.jclouds.rest.RestContextBuilder");
      assertEquals(matcher.group(2), "java.lang.String");
      assertEquals(matcher.group(3), "java.lang.Integer");

   }

   public void testREST_CONTEXT_BUILDERwithSpace() {
      Matcher matcher = Patterns.REST_CONTEXT_BUILDER
            .matcher("org.jclouds.rest.RestContextBuilder<java.lang.String, java.lang.Integer>");
      assert (matcher.find());
      assertEquals(matcher.group(1), "org.jclouds.rest.RestContextBuilder");
      assertEquals(matcher.group(2), "java.lang.String");
      assertEquals(matcher.group(3), "java.lang.Integer");
   }
}
