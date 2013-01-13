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
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecompute.domain.MachineType;
import org.jclouds.googlecompute.internal.BaseGoogleComputeApiLiveTest;
import org.jclouds.googlecompute.options.ListOptions;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * @author David Alves
 */
public class MachineTypeApiLiveTest extends BaseGoogleComputeApiLiveTest {

   private MachineType machineType;

   private MachineTypeApi api() {
      return context.getApi().getMachineTypeApiForProject(getUserProject());
   }

   @Test(groups = "live")
   public void testListMachineType() {

      PagedIterable<MachineType> machineTypes = api().list(new ListOptions.Builder()
              .maxResults(1));

      Iterator<IterableWithMarker<MachineType>> pageIterator = machineTypes.iterator();
      assertTrue(pageIterator.hasNext());

      IterableWithMarker<MachineType> singlePageIterator = pageIterator.next();
      List<MachineType> machineTypeAsList = Lists.newArrayList(singlePageIterator);

      assertSame(machineTypeAsList.size(), 1);

      this.machineType = Iterables.getOnlyElement(machineTypeAsList);
   }


   @Test(groups = "live", dependsOnMethods = "testListMachineType")
   public void testGetMachineType() {
      MachineType machineType = api().get(this.machineType.getName());
      assertNotNull(machineType);
      assertMachineTypeEquals(machineType, this.machineType);
   }

   private void assertMachineTypeEquals(MachineType result, MachineType expected) {
      assertEquals(result.getName(), expected.getName());
   }
}
