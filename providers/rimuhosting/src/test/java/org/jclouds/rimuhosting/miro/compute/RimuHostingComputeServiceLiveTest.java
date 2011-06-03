/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rimuhosting.miro.compute;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Ivan Meredith
 */
@Test(groups = "live", sequential = true)
public class RimuHostingComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   public RimuHostingComputeServiceLiveTest() {
      provider = "rimuhosting";
   }

   @BeforeClass
   @Override
   public void setServiceDefaults() {
      group = "rimuhosting.jclouds";
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   @Override
   public void testOptionToNotBlock() {
      // start call is blocking anyway.
   }
}
