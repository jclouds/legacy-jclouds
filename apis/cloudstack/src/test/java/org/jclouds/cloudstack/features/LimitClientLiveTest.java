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
package org.jclouds.cloudstack.features;

import java.util.Set;

import org.jclouds.cloudstack.domain.ResourceLimit;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code LimitClient}
 *
 * @author Vijay Kiran
 */
@Test(groups = "live", singleThreaded = true, testName = "LimitClientLiveTest")
public class LimitClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testListResourceLimits() {
      final Set<ResourceLimit> resourceLimits = client.getLimitClient().listResourceLimits();

      for (ResourceLimit resourceLimit : resourceLimits) {
         checkResourceLimit(resourceLimit);
      }
   }

   private void checkResourceLimit(ResourceLimit resourceLimit) {
      assert resourceLimit.getAccount() != null : resourceLimit;
      assert resourceLimit.getDomain() != null : resourceLimit;
      assert resourceLimit.getResourceType() != ResourceLimit.ResourceType.UNRECOGNIZED : resourceLimit;
   }
}
