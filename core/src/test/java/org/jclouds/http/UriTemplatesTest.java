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

import static org.jclouds.http.UriTemplates.expand;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class UriTemplatesTest {

   @DataProvider(name = "strings")
   public Object[][] createData() {
      return new Object[][] { { "apples" }, { "sp ace" }, { "unic₪de" }, { "qu?stion" } };
   }

   @Test(dataProvider = "strings")
   public void testExpandNotUrlEncoded(String val) {
      assertEquals(expand("/repos/{user}", ImmutableMap.of("user", val)), "/repos/" + val);
   }

   public void testMultipleParams() {
      assertEquals(expand("/repos/{user}/{repo}", ImmutableMap.of("user", "unic₪de", "repo", "foo")),
            "/repos/unic₪de/foo");
   }

   // jclouds params often include hyphens
   public void testParamKeyHyphen() {
      assertEquals(expand("/{user-dir}", ImmutableMap.of("user-dir", "foo")), "/foo");
   }

   // sometimes the user intends to have a curly brace in the url
   public void testMissingParamProceeds() {
      assertEquals(expand("/{user-dir}", ImmutableMap.of("user_dir", "foo")), "/{user-dir}");
   }
}
