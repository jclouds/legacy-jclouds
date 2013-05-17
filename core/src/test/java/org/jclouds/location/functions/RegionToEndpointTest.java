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
package org.jclouds.location.functions;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;
import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code RegionToEndpoint}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "RegionToEndpointTest")
public class RegionToEndpointTest {

   @Test
   public void testCorrect() {
      RegionToEndpoint fn = new RegionToEndpoint(Suppliers.<Map<String, Supplier<URI>>> ofInstance(ImmutableMap.of("1",
               Suppliers.ofInstance(URI.create("http://1")))));
      assertEquals(fn.apply("1"), URI.create("http://1"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeString() {
      RegionToEndpoint fn = new RegionToEndpoint(Suppliers.<Map<String, Supplier<URI>>> ofInstance(ImmutableMap.of("1",
               Suppliers.ofInstance(URI.create("http://1")))));
      fn.apply(new File("foo"));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMustHaveEndpoints() {
      RegionToEndpoint fn = new RegionToEndpoint(Suppliers.<Map<String, Supplier<URI>>> ofInstance(ImmutableMap
               .<String, Supplier<URI>> of()));
      fn.apply("1");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNullIsIllegal() {
      RegionToEndpoint fn = new RegionToEndpoint(Suppliers.<Map<String, Supplier<URI>>> ofInstance(ImmutableMap.of("1",
               Suppliers.ofInstance(URI.create("http://1")))));
      fn.apply(null);
   }
}
