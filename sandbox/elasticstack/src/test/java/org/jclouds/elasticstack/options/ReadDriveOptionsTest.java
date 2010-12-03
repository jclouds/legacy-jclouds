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

package org.jclouds.elasticstack.options;

import static org.jclouds.elasticstack.options.ReadDriveOptions.Builder.offset;
import static org.jclouds.elasticstack.options.ReadDriveOptions.Builder.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

/**
 * Tests possible uses of ReadDriveOptions and ReadDriveOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ReadDriveOptionsTest {

   @Test
   public void testNullOffset() {
      ReadDriveOptions options = new ReadDriveOptions();
      assertNull(options.getOffset());
   }

   @Test
   public void testOffset() {
      ReadDriveOptions options = new ReadDriveOptions().offset(1024);
      assertEquals(options.getOffset(), new Long(1024));
   }

   @Test
   public void testOffsetStatic() {
      ReadDriveOptions options = offset(1024);
      assertEquals(options.getOffset(), new Long(1024));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testOffsetNegative() {
      offset(-1);
   }

   @Test
   public void testNullSize() {
      ReadDriveOptions options = new ReadDriveOptions();
      assertNull(options.getSize());
   }

   @Test
   public void testSize() {
      ReadDriveOptions options = new ReadDriveOptions().size(1024);
      assertEquals(options.getSize(), new Long(1024));
   }

   @Test
   public void testSizeStatic() {
      ReadDriveOptions options = size(1024);
      assertEquals(options.getSize(), new Long(1024));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testSizeNegative() {
      size(-1);
   }

}
