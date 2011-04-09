/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.io.File;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BlobNameTest {
   BlobName fn = new BlobName();
   private static final Factory BLOB_FACTORY = new RestContextFactory()
         .createContextBuilder("transient", "identity", "credential").buildInjector().getInstance(Blob.Factory.class);

   @Test
   public void testCorrect() throws SecurityException, NoSuchMethodException {

      Blob blob = BLOB_FACTORY.create(null);
      blob.getMetadata().setName("foo");

      assertEquals(fn.apply(blob), "foo");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeBlob() {
      fn.apply(new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      fn.apply(null);
   }
}
