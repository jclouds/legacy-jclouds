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

package org.jclouds.scaleup.storage;

import static org.testng.Assert.assertEquals;

import org.jclouds.s3.S3ClientLiveTest;
import org.jclouds.s3.domain.S3Object;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code S3Client}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ScaleUpStorageClientLiveTest")
public class ScaleUpStorageClientLiveTest extends S3ClientLiveTest {
   // no support for content encoding
   @Override
   protected void assertContentEncoding(S3Object newObject, String string) {
      assert (newObject.getPayload().getContentMetadata().getContentEncoding().indexOf(string) != -1);
      assert (newObject.getMetadata().getContentMetadata().getContentEncoding().indexOf(string) != -1);
   }

   // no support for cache control
   @Override
   protected void assertCacheControl(S3Object newObject, String string) {
      assertEquals(newObject.getMetadata().getCacheControl(), null);
   }
}
