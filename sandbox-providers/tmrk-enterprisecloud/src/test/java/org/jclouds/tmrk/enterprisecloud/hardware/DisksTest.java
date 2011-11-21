/**
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
package org.jclouds.tmrk.enterprisecloud.hardware;

import org.jclouds.tmrk.enterprisecloud.domain.Size;
import org.jclouds.tmrk.enterprisecloud.domain.hardware.Disks;
import org.jclouds.tmrk.enterprisecloud.domain.hardware.VirtualDisk;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Jason King
 */
@Test(groups = "unit", testName = "DisksTest")
public class DisksTest {

   private VirtualDisk disk;
   private Disks disks;

   @BeforeMethod()
   public void setUp() throws URISyntaxException {
      disk = VirtualDisk.builder().index(0).name("test disk").size(Size.builder().value(1).unit("GB").build()).build();
      disks = Disks.builder().addDisk(disk).build();
   }

   @Test
   public void testAddDisk() throws URISyntaxException {
      VirtualDisk disk2 = VirtualDisk.builder().index(1).name("test disk 1").size(Size.builder().value(1).unit("GB").build()).build();
      Disks twoDisks = disks.toBuilder().addDisk(disk2).build();
      Set<VirtualDisk> virtualDisks = twoDisks.getVirtualDisks();

      assertEquals(2, virtualDisks.size());
      assertTrue(virtualDisks.contains(disk));
      assertTrue(virtualDisks.contains(disk2));
   }
}
