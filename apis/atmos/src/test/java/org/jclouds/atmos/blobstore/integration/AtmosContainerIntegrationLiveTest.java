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
package org.jclouds.atmos.blobstore.integration;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.integration.internal.BaseContainerIntegrationTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live")
public class AtmosContainerIntegrationLiveTest extends BaseContainerIntegrationTest {
   public AtmosContainerIntegrationLiveTest() {
      provider = "atmos";
   }
   @Override
   public void testListContainerMaxResults() throws InterruptedException {
      // Not currently working
   }

   @Override
   public void testListContainerMarker() throws InterruptedException {
      // Not currently working https://community.emc.com/thread/100545
   }
   
   protected void checkMD5(BlobMetadata metadata) throws IOException {
      // atmos doesn't support MD5
      assertEquals(metadata.getContentMetadata().getContentMD5(), null);
   }
}
