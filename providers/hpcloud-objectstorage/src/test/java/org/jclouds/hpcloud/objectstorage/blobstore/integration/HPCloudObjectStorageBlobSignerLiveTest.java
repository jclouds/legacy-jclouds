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
package org.jclouds.hpcloud.objectstorage.blobstore.integration;

import java.io.IOException;

import org.jclouds.openstack.swift.blobstore.integration.SwiftBlobSignerLiveTest;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

/**
 * @author Jeremy Daggett
 */
@Test(groups = { "live" })
public class HPCloudObjectStorageBlobSignerLiveTest extends SwiftBlobSignerLiveTest {
   public HPCloudObjectStorageBlobSignerLiveTest() {
      provider = "hpcloud-objectstorage";
   }

   // hp doesn't yet support time-bound request signing
   // https://api-docs.hpcloud.com/hpcloud-object-storage/1.0/content/ch_object-storage-dev-overview.html
   @Override
   @Test(expectedExceptions = AuthorizationException.class)
   public void testSignGetUrlWithTime() throws InterruptedException, IOException {
      super.testSignGetUrlWithTime();
   }

   // hp doesn't yet support time-bound request signing
   // https://api-docs.hpcloud.com/hpcloud-object-storage/1.0/content/ch_object-storage-dev-overview.html
   @Override
   @Test(expectedExceptions = AuthorizationException.class)
   public void testSignPutUrlWithTime() throws Exception {
      super.testSignPutUrlWithTime();
   }

}
