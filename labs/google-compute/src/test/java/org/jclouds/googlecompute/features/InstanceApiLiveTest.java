/*
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

package org.jclouds.googlecompute.features;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecompute.domain.Instance;
import org.jclouds.googlecompute.domain.InstanceNetworkInterface;
import org.jclouds.googlecompute.internal.BaseGoogleComputeApiLiveTest;
import org.jclouds.googlecompute.options.ListOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author David Alves
 */
public class InstanceApiLiveTest extends BaseGoogleComputeApiLiveTest {

   private static final String NETWORK_NAME = "instance-api-live-test-instance";
   private static final int TIME_WAIT = 600;

   private Instance instance;

   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      super.setupContext();
      instance = Instance.builder()
              .name(NETWORK_NAME)
              .machineType(getDefaultMachineTypekUrl(getUserProject()))
              .addNetworkInterface(
                      InstanceNetworkInterface.builder()
                              .network(getDefaultNetworkUrl(getUserProject()))
                              .build()
              )
              .zone(getDefaultZoneUrl(getUserProject()))
              .build();
   }

   private InstanceApi api() {
      return context.getApi().getInstanceApi();
   }

   @Test(groups = "live")
   public void testInsertInstance() {

      assertOperationDoneSucessfully(api().insert(getUserProject(), instance), TIME_WAIT);

   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testGetInstance() {

      Instance instance = api().get(getUserProject(), NETWORK_NAME);
      assertNotNull(instance);
      assertInstanceEquals(instance, this.instance);
   }

   @Test(groups = "live", dependsOnMethods = "testGetInstance")
   public void testListInstance() {

      PagedIterable<Instance> instances = api().list(getUserProject(), ListOptions.builder()
              .filter("name eq " + NETWORK_NAME)
              .build());

      List<Instance> instancesAsList = Lists.newArrayList(instances.concat());

      assertEquals(instancesAsList.size(), 1);

      assertInstanceEquals(Iterables.getOnlyElement(instancesAsList), instance);

   }

   @Test(groups = "live", dependsOnMethods = "testListInstance")
   public void testDeleteInstance() {

      assertOperationDoneSucessfully(api().delete(getUserProject(), NETWORK_NAME), TIME_WAIT);
   }

   private void assertInstanceEquals(Instance result, Instance expected) {
      assertEquals(result.getName(), expected.getName());
   }
}
