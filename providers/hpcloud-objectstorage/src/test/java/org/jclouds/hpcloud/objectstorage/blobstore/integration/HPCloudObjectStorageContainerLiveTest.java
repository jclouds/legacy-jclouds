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
import java.net.MalformedURLException;

import org.jclouds.blobstore.integration.internal.BaseContainerLiveTest;
import org.testng.annotations.Test;

/**
 * @author Jeremy Daggett
 */
@Test(groups = { "live" })
public class HPCloudObjectStorageContainerLiveTest extends BaseContainerLiveTest {
   public HPCloudObjectStorageContainerLiveTest() {
      provider = "hpcloud-objectstorage";
   }

	@Test(enabled = false)
   //@Test(expectedExceptions=UnsupportedOperationException.class)
   public void testPublicAccess() throws MalformedURLException, InterruptedException, IOException {
      super.testPublicAccess();
   }
}
