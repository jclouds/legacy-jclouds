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
package org.jclouds.functions;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.ExecutionException;

import org.testng.annotations.Test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "CacheLearningTest")
public class CacheLearningTest {
   @Test
   public void howTo() throws ExecutionException {
      Cache<String, String> cache = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {

         @Override
         public String load(String key) throws Exception {
            if (key.equals("runtimeexception"))
               throw new RuntimeException("runtimeexception");
            if (key.equals("exception"))
               throw new Exception("exception");
            return key.equals("foo") ? null : key;
         }

         @Override
         public String toString() {
            return "testLoader";
         }

      });
      try {
         cache.get("foo");
      } catch (NullPointerException e) {
         assertEquals(e.getMessage(), "testLoader returned null for key foo.");
      }
      try {
         cache.get("exception");
      } catch (ExecutionException e) {
         assertEquals(e.getMessage(), "java.lang.Exception: exception");
      }
      try {
         cache.get("runtimeexception");
      } catch (UncheckedExecutionException e) {
         assertEquals(e.getMessage(), "java.lang.RuntimeException: runtimeexception");
      }
      try {
         cache.getUnchecked("exception");
      } catch (UncheckedExecutionException e) {
         assertEquals(e.getMessage(), "java.lang.Exception: exception");
      }
      assertEquals(cache.get("bar"), "bar");
      assertEquals(cache.get("baz"), "baz");
      assertEquals(cache.asMap().size(), 2);
   }
}
