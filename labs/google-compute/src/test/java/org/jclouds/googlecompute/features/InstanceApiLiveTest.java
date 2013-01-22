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
import org.jclouds.googlecompute.domain.InstanceTemplate;
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

   private static final String INSTANCE_NAME = "instance-api-live-test-instance";
   private static final String DISK_NAME = "instance-live-test-disk";
   private static final int TIME_WAIT = 600;

   private InstanceTemplate instance;

   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      super.setupContext();
      instance = InstanceTemplate.builder()
              .forMachineTypeAndNetwork(getDefaultMachineTypekUrl(getUserProject()),
                      getDefaultNetworkUrl(getUserProject()))
              .addMetadata("mykey", "myvalue")
              .addTag("atag")
              .description("a description")
              .addDisk(InstanceTemplate.PersistentDisk.Mode.READ_WRITE, getDiskUrl(getUserProject(), DISK_NAME))
              .zone(getDefaultZoneUrl(getUserProject()));
   }

   private InstanceApi api() {
      return context.getApi().getInstanceApiForProject(getUserProject());
   }

   @Test(groups = "live")
   public void testInsertInstance() {

      assertOperationDoneSucessfully(context.getApi().getDiskApiForProject(getUserProject()).createInZone
              ("instance-live-test-disk", 1, getDefaultZoneUrl(getUserProject())), TIME_WAIT);

      assertOperationDoneSucessfully(api().createInZone(INSTANCE_NAME, instance, DEFAULT_ZONE_NAME), TIME_WAIT);

   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testInsertInstanceCopy() {
      Instance instance = api().get(INSTANCE_NAME);
      InstanceTemplate copy = InstanceTemplate.fromInstance(instance);
      copy.network(instance.getNetworkInterfaces().iterator().next().getNetwork());

      assertOperationDoneSucessfully(api().createInZone(INSTANCE_NAME + "-2", copy, DEFAULT_ZONE_NAME), TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testGetInstance() {

      Instance instance = api().get(INSTANCE_NAME);
      assertNotNull(instance);
      assertInstanceEquals(instance, this.instance);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testListInstance() {

      PagedIterable<Instance> instances = api().list(new ListOptions.Builder()
              .filter("name eq " + INSTANCE_NAME));

      List<Instance> instancesAsList = Lists.newArrayList(instances.concat());

      assertEquals(instancesAsList.size(), 1);

      assertInstanceEquals(Iterables.getOnlyElement(instancesAsList), instance);

   }

   @Test(groups = "live", dependsOnMethods = {"testListInstance", "testInsertInstanceCopy"})
   public void testDeleteInstance() {

      assertOperationDoneSucessfully(api().delete(INSTANCE_NAME), TIME_WAIT);
      assertOperationDoneSucessfully(api().delete(INSTANCE_NAME + "-2"), TIME_WAIT);
      assertOperationDoneSucessfully(context.getApi().getDiskApiForProject(getUserProject()).delete(DISK_NAME),
              TIME_WAIT);
   }

   private void assertInstanceEquals(Instance result, InstanceTemplate expected) {
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getTags(), expected.getTags());
      assertEquals(result.getMetadata(), expected.getMetadata());
   }
}
