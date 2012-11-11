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
import org.jclouds.googlecompute.domain.Disk;
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
public class DiskApiLiveTest extends BaseGoogleComputeApiLiveTest {

   private static final String DISK_NAME = "disk-api-live-test-disk";
   private static final int TIME_WAIT = 10;

   private Disk disk;

   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      super.setupContext();
      disk = Disk.builder()
              .name(DISK_NAME)
              .zone(getDefaultZoneUrl(getUserProject()))
              .sizeGb(1)
              .build();
   }

   private DiskApi api() {
      return context.getApi().getDiskApi();
   }

   @Test(groups = "live")
   public void testInsertDisk() {

      assertOperationDoneSucessfully(api().insert(getUserProject(), disk), TIME_WAIT);

   }

   @Test(groups = "live", dependsOnMethods = "testInsertDisk")
   public void testGetDisk() {

      Disk disk = api().get(getUserProject(), DISK_NAME);
      assertNotNull(disk);
      assertDiskEquals(disk, this.disk);
   }

   @Test(groups = "live", dependsOnMethods = "testGetDisk")
   public void testListDisk() {

      PagedIterable<Disk> disks = api().list(getUserProject(), ListOptions.builder()
              .filter("name eq " + DISK_NAME)
              .build());

      List<Disk> disksAsList = Lists.newArrayList(disks.concat());

      assertEquals(disksAsList.size(), 1);

      assertDiskEquals(Iterables.getOnlyElement(disksAsList), disk);

   }

   @Test(groups = "live", dependsOnMethods = "testListDisk")
   public void testDeleteDisk() {

      assertOperationDoneSucessfully(api().delete(getUserProject(), DISK_NAME), TIME_WAIT);
   }

   private void assertDiskEquals(Disk result, Disk expected) {
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getSizeGb(), expected.getSizeGb());
   }

}
