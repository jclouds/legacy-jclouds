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
package org.jclouds.location.functions;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code ZoneToEndpoint}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ZoneToEndpointTest")
public class ZoneToEndpointTest {

   @Test
   public void testCorrect() throws SecurityException, NoSuchMethodException {
      ZoneToEndpoint fn = new ZoneToEndpoint(ImmutableMap.of("1", URI.create("http://1")));
      assertEquals(fn.apply("1"), URI.create("http://1"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeString() {
      ZoneToEndpoint fn = new ZoneToEndpoint(ImmutableMap.of("1", URI.create("http://1")));
      fn.apply(new File("foo"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustHaveEndpoints() {
      new ZoneToEndpoint(ImmutableMap.<String, URI> of());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNullIsIllegal() {
      ZoneToEndpoint fn = new ZoneToEndpoint(ImmutableMap.of("1", URI.create("http://1")));
      fn.apply(null);
   }
}
