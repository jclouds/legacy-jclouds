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

package org.jclouds.virtualbox.functions;

import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.ErrorCode;
import org.testng.annotations.Test;
import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.VBoxException;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author Mattias Holmqvist
 */
public class CreateMediumIfNotAlreadyExistsLiveTest extends BaseVirtualBoxClientLiveTest {

   @Test
   public void testCreateMedium() throws Exception {
      String path = System.getProperty("user.home") + "/jclouds-virtualbox-test/test-medium-1.vdi";
      new CreateMediumIfNotAlreadyExists(manager, "vdi", true).apply(path);
      manager.getVBox().findMedium(path, DeviceType.HardDisk);
      assertFileCanBeDeleted(path);
   }

   @Test
   public void testCreateMediumFailWhenUsingNonFullyQualifiedPath() throws Exception {
      String path = "test-medium-2.vdi";
      try {
         new CreateMediumIfNotAlreadyExists(manager, "vdi", true).apply(path);
         fail();
      } catch (VBoxException e) {
         ErrorCode errorCode = ErrorCode.valueOf(e);
         assertEquals(errorCode, ErrorCode.VBOX_E_FILE_ERROR);
      }
   }

   @Test
   public void testCreateSameMediumTwiceWhenUsingOverwrite() throws Exception {
      String path = System.getProperty("user.home") + "/jclouds-virtualbox-test/test-medium-3.vdi";
      new CreateMediumIfNotAlreadyExists(manager, "vdi", true).apply(path);
      new CreateMediumIfNotAlreadyExists(manager, "vdi", true).apply(path);
      manager.getVBox().findMedium(path, DeviceType.HardDisk);
      assertFileCanBeDeleted(path);
   }

   private void assertFileCanBeDeleted(String path) {
      File file = new File(path);
      boolean mediumDeleted = file.delete();
      assertTrue(mediumDeleted);
   }
}
