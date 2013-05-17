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
package org.jclouds.openstack.keystone.v2_0;

import static org.testng.Assert.assertNotNull;

import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests KeystoneApi
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "KeystoneApiLiveTest")
public class KeystoneApiLiveTest extends BaseKeystoneApiLiveTest {

   public void testGetApiMetaData() {
      ApiMetadata result = api.getApiMetadata();
      assertNotNull(result);
      assertNotNull(result.getId());
      assertNotNull(result.getStatus());
      assertNotNull(result.getUpdated());
   }

}
