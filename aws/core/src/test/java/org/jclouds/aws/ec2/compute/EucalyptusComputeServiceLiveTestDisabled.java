/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.ec2.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Disabled until we have an environment with enough room to run a few nodes simultaneously.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "ec2.EucalyptusComputeServiceLiveTest")
public class EucalyptusComputeServiceLiveTestDisabled extends EC2ComputeServiceLiveTest {

   public EucalyptusComputeServiceLiveTestDisabled() {
      provider = "eucalyptus";
   }

   @BeforeClass
   @Override
   public void setServiceDefaults() {
      // security groups must be <30 characters
      tag = "euc";
   }

   @Override
   @Test(enabled = false)
   public void testExtendedOptionsAndLogin() throws Exception {
      // euc does not support monitoring
   }

   @Override
   protected void assertDefaultWorks() {
      Template defaultTemplate = client.templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

}
