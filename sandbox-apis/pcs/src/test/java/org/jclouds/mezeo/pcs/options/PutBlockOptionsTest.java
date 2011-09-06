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
package org.jclouds.mezeo.pcs.options;

import static org.jclouds.mezeo.pcs.options.PutBlockOptions.Builder.range;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.mezeo.pcs.options.PutBlockOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of PutBlockOptions and PutBlockOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "mezeo.PutBlockOptionsTest")
public class PutBlockOptionsTest {

   @Test
   public void testRange() {
      PutBlockOptions options = new PutBlockOptions();
      options.range(0, 1024);
      bytes1to1024(options);
   }

   private void bytes1to1024(PutBlockOptions options) {
      assertEquals(options.getRange(), "bytes 0-1024/*");
   }

   @Test
   public void testRangeZeroToFive() {
      PutBlockOptions options = new PutBlockOptions();
      options.range(0, 5);
      assertEquals(options.getRange(), "bytes 0-5/*");
   }

   @Test
   public void testRangeOverride() {
      PutBlockOptions options = new PutBlockOptions();
      options.range(0, 5).range(10, 100);
      assertEquals(options.getRange(), "bytes 10-100/*");
   }

   @Test
   public void testNullRange() {
      PutBlockOptions options = new PutBlockOptions();
      assertNull(options.getRange());
   }

   @Test
   public void testRangeStatic() {
      PutBlockOptions options = range(0, 1024);
      bytes1to1024(options);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testRangeNegative1() {
      range(-1, 0);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testRangeNegative2() {
      range(0, -1);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testRangeNegative() {
      range(-1, -1);
   }

}
