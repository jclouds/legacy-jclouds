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
package org.jclouds.rackspace.cloudblockstorage.uk;

import org.jclouds.openstack.cinder.v1.CinderApiMetadata;
import org.jclouds.providers.internal.BaseProviderMetadataTest;
import org.jclouds.rackspace.cloudblockstorage.uk.CloudBlockStorageUKProviderMetadata;
import org.testng.annotations.Test;

/**
 * @author Everett Toews
 */
@Test(groups = "unit", testName = "CloudBlockStorageUKProviderTest")
public class CloudBlockStorageUKProviderTest extends BaseProviderMetadataTest {

   public CloudBlockStorageUKProviderTest() {
      super(new CloudBlockStorageUKProviderMetadata(), new CinderApiMetadata());
   }
}
