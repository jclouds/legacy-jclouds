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
package org.jclouds.rackspace.clouddns.v1.features;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.v2_0.domain.Limits;
import org.jclouds.rackspace.clouddns.v1.internal.BaseCloudDNSApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Everett Toews
 */
@Test(groups = "live", singleThreaded = true, testName = "LimitApiLiveTest")
public class LimitApiLiveTest extends BaseCloudDNSApiLiveTest {
   @Test
   public void testList() throws Exception {
      Limits limits = api.getLimitApi().list();
      assertNotNull(limits.getAbsoluteLimits());
      assertNotNull(limits.getRateLimits());
      assertTrue(limits.getAbsoluteLimits().size() > 1);
      assertTrue(Iterables.size(limits.getRateLimits()) > 1);
   }
   
   @Test
   public void testListTypes() throws Exception {
      Iterable<String> limitTypes = api.getLimitApi().listTypes();
      assertNotNull(limitTypes);
      assertTrue(Iterables.size(limitTypes) > 1);
   }
}
