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
package org.jclouds.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.concurrent.ExecutionException;

import org.testng.annotations.Test;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "CacheLearningTest")
public class CacheLearningTest {
   @Test
   public void howTo() throws ExecutionException {
      LoadingCache<String, String> cache = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {

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
         fail("expected exception on miss");
      } catch (CacheLoader.InvalidCacheLoadException e) {
         assertEquals(e.getMessage(), "CacheLoader returned null for key foo.");
      }
      
      try {
         cache.getUnchecked("foo");
         fail("expected exception on miss");
      } catch (CacheLoader.InvalidCacheLoadException e) {
         assertEquals(e.getMessage(), "CacheLoader returned null for key foo.");
      }
      
      assertEquals(cache.asMap().keySet().size(), 0);
      assertEquals(cache.asMap().size(), 0);

      // check insert behind
      cache.asMap().put("foo", "bar");
      assertEquals(cache.get("foo"), "bar");

      assertEquals(cache.asMap().keySet().size(), 1);
      assertEquals(cache.asMap().size(), 1);
      
      // check delete behind invalidates
      cache.asMap().remove("foo");
      assertEquals(cache.asMap().keySet().size(), 0);
      assertEquals(cache.asMap().size(), 0);
      
      try {
         cache.get("exception");
         fail("expected checked exception in loader to rethrow as ExecutionException");
      } catch (ExecutionException e) {
         assertEquals(e.getMessage(), "java.lang.Exception: exception");
      }

      try {
         cache.get("runtimeexception");
         fail("expected unchecked exception in loader to rethrow as UncheckedExecutionException");
      } catch (UncheckedExecutionException e) {
         assertEquals(e.getMessage(), "java.lang.RuntimeException: runtimeexception");
      }

      try {
         cache.getUnchecked("exception");
         fail("expected checked exception in loader to rethrow as UncheckedExecutionException, when getUnchecked called");
      } catch (UncheckedExecutionException e) {
         assertEquals(e.getMessage(), "java.lang.Exception: exception");
      }

      assertEquals(cache.get("bar"), "bar");
      assertEquals(cache.get("baz"), "baz");
      assertEquals(cache.asMap().size(), 2);
   }
}
