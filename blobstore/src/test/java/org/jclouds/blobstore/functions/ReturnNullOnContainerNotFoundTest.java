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

package org.jclouds.blobstore.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ReturnNullOnContainerNotFoundTest {
   ReturnNullOnContainerNotFound fn = new ReturnNullOnContainerNotFound();

   @Test
   public void testFoundIsNull() throws SecurityException, NoSuchMethodException {
      assertEquals(fn.apply(new ContainerNotFoundException()), null);
   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testPropagates() throws SecurityException, NoSuchMethodException {
      fn.apply(new RuntimeException());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      fn.apply(null);
   }
}
