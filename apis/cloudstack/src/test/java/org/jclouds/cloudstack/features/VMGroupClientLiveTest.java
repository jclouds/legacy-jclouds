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

import static org.testng.Assert.assertEquals;

import java.util.Random;
import java.util.Set;

import org.jclouds.cloudstack.domain.VMGroup;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.options.ListVMGroupsOptions;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code VMGroupClient}
 *
 * @author Richard Downer
 */
@Test(groups = "live", singleThreaded = true, testName = "VMGroupClientLiveTest")
public class VMGroupClientLiveTest extends BaseCloudStackClientLiveTest {

   private VMGroup groupCreated;

   public VMGroupClientLiveTest() {
      prefix += "2";
   }

   @Test
   public void testCreateListDestroyVMGroup() {
      VMGroupClient vmGroupClient = client.getVMGroupClient();
      String name = "jclouds-test-" + (Integer.toHexString(new Random().nextInt()));
      groupCreated = vmGroupClient.createInstanceGroup(name);
      assertEquals(groupCreated.getName(), name);

      Set<VMGroup> search = vmGroupClient.listInstanceGroups(ListVMGroupsOptions.Builder.name(name));
      assertEquals(1, search.size());
      VMGroup groupFound = Iterables.getOnlyElement(search);
      assertEquals(groupFound, groupCreated);

      vmGroupClient.deleteInstanceGroup(groupCreated.getId());
      groupCreated = null;
   }

   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      if (groupCreated != null) {
         client.getVMGroupClient().deleteInstanceGroup(groupCreated.getId());
      }
      super.tearDownContext();
   }

}
