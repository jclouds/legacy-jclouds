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

package org.jclouds.scaleup.storage.blobstore;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.s3.blobstore.integration.S3ContainerIntegrationLiveTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ScaleUpStorageContainerIntegrationLiveTest")
public class ScaleUpStorageContainerIntegrationLiveTest extends S3ContainerIntegrationLiveTest {
   // doesn't support MD5
   @Override
   protected void checkMD5(BlobMetadata metadata) throws IOException {
      assertEquals(metadata.getContentMetadata().getContentMD5(), null);
   }
}
