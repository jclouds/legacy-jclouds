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
package org.jclouds.hpcloud.object.storage;

import org.jclouds.providers.BaseProviderMetadataTest;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.Test;

/**
 * The HPCloudObjectStorageProviderTest tests the {@link org.jclouds.hpcloud.object.storage.HPCloudObjectStorageProviderMetadata} class.
 * 
 * @author Jeremy Daggett
 */
@Test(groups = "unit", testName = "HPCloudObjectStorageProviderTest")
public class HPCloudObjectStorageProviderTest extends BaseProviderMetadataTest {

   public HPCloudObjectStorageProviderTest() {
      super(new HPCloudObjectStorageProviderMetadata(), ProviderMetadata.BLOBSTORE_TYPE);
   }

}