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

package org.jclouds.virtualbox.statements;

import com.google.common.base.CaseFormat;
import com.google.inject.Injector;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;

/**
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "InstallGuestAdditionsLiveTest")
public class InstallGuestAdditionsLiveTest extends BaseVirtualBoxClientLiveTest {

   private String vmName;
   
   @Override
   @BeforeClass(groups = "live")
   public void setupClient() {
      super.setupClient();
      vmName = VIRTUALBOX_IMAGE_PREFIX
            + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass()
                  .getSimpleName());
      
      vmName = "jclouds-image-create-and-install-vm-live-test";
   }

   public void testInstallGuestAdditionsOnTheMachine() throws Exception {
      Injector injector = context.utils().injector();      
      injector.getInstance(GuestAdditionsInstaller.class).apply(vmName);
   }
   
}