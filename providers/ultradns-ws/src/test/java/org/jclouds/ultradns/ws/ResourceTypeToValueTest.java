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
package org.jclouds.ultradns.ws;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ResourceTypeToValueTest")
public class ResourceTypeToValueTest {

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "ResourceTypes do not include RRRR; types: \\[A, NS, CNAME, SOA, PTR, MX, TXT, AAAA, SRV\\]")
   public void testNiceExceptionOnNotFound() {
      new ResourceTypeToValue().apply("RRRR");
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "resource type was null")
   public void testNiceExceptionOnNull() {
      new ResourceTypeToValue().apply(null);
   }

   @Test
   public void testNormalCase() {
      assertEquals(new ResourceTypeToValue().apply("AAAA"), "28");
   }
}
