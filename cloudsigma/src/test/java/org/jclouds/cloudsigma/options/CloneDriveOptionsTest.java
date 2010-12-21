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

package org.jclouds.cloudsigma.options;

import static org.jclouds.cloudsigma.options.CloneDriveOptions.Builder.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

/**
 * Tests possible uses of CloneDriveOptions and CloneDriveOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CloneDriveOptionsTest {

   @Test
   public void testNullSize() {
      CloneDriveOptions options = new CloneDriveOptions();
      assertNull(options.getOptions().get("size"));
   }

   @Test
   public void testSize() {
      CloneDriveOptions options = new CloneDriveOptions().size(1024);
      assertEquals(options.getOptions().get("size"), "1024");
   }

   @Test
   public void testSizeStatic() {
      CloneDriveOptions options = size(1024);
      assertEquals(options.getOptions().get("size"), "1024");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testSizeNegative() {
      size(-1);
   }

}
