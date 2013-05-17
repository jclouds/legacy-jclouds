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
package org.jclouds.bluelock.vcloud.zone01;

import org.jclouds.providers.internal.BaseProviderMetadataTest;
import org.jclouds.vcloud.VCloudApiMetadata;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "BluelockVCloudZone01ProviderTest")
public class BluelockVCloudZone01ProviderTest extends BaseProviderMetadataTest {

   public BluelockVCloudZone01ProviderTest() {
      super(new BluelockVCloudZone01ProviderMetadata(), new VCloudApiMetadata());
   }
}
