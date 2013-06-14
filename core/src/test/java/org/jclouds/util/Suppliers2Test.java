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
package org.jclouds.util;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.NoSuchElementException;

import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;

public class Suppliers2Test {

   @Test
   public void testGetLastValueInMap() {
      assertEquals(
               Suppliers2.<String, String> getLastValueInMap(
                        Suppliers.<Map<String, Supplier<String>>> ofInstance(ImmutableMap.of("foo",
                                 Suppliers.ofInstance("bar")))).get(), "bar");
   }

   @Test
   public void testGetSpecificValueInMap() {
      Supplier<Map<String, Supplier<String>>> testMap =  Suppliers.<Map<String, Supplier<String>>> ofInstance(
               ImmutableMap.of("foo", Suppliers.ofInstance("bar")));

      assertEquals(Suppliers2.<String, String> getValueInMapOrNull(testMap, "foo").get(), "bar");
      assertEquals(Suppliers2.<String, String> getValueInMapOrNull(testMap, "baz").get(), null);
   }

   @Test
   public void testOfInstanceFunction() {
      assertEquals(Suppliers2.ofInstanceFunction().apply("foo").get(), "foo");
   }

   @Test
   public void testOrWhenFirstNull() {
      assertEquals(Suppliers2.or(Suppliers.<String> ofInstance(null), Suppliers.ofInstance("foo")).get(), "foo");
   }

   @Test
   public void testOrWhenFirstNotNull() {
      assertEquals(Suppliers2.or(Suppliers.<String> ofInstance("foo"), Suppliers.ofInstance("bar")).get(), "foo");
   }

   @Test
   public void testOnThrowableWhenFirstThrowsMatchingException() {
      assertEquals(Suppliers2.onThrowable(new Supplier<String>() {

         @Override
         public String get() {
            throw new NoSuchElementException();
         }

      }, NoSuchElementException.class, Suppliers.ofInstance("foo")).get(), "foo");
   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testOnThrowableWhenFirstThrowsUnmatchingException() {
      Suppliers2.onThrowable(new Supplier<String>() {

         @Override
         public String get() {
            throw new RuntimeException();
         }

      }, NoSuchElementException.class, Suppliers.ofInstance("foo")).get();
   }

   @Test
   public void testOnThrowableWhenFirstIsFine() {
      assertEquals(
               Suppliers2.onThrowable(Suppliers.<String> ofInstance("foo"), NoSuchElementException.class,
                        Suppliers.ofInstance("bar")).get(), "foo");
   }

   
   @Test
   public void testCombination() {
      Supplier<String> alternate = Suppliers.ofInstance("bar");
      Supplier<String> or = Suppliers2.or(Suppliers.<String> ofInstance("foo"), alternate);
      Supplier<String> combined = Suppliers2.onThrowable(or, NoSuchElementException.class, alternate);    
      
      assertEquals(combined.get(), "foo");
   }

}
