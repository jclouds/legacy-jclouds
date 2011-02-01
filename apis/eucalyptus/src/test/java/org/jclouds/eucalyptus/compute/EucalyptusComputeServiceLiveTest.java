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

package org.jclouds.eucalyptus.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.ec2.compute.EC2ComputeServiceLiveTest;
import org.jclouds.http.HttpResponseException;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "EucalyptusComputeServiceLiveTest")
public class EucalyptusComputeServiceLiveTest extends EC2ComputeServiceLiveTest {

   public EucalyptusComputeServiceLiveTest() {
      provider = "eucalyptus";
      // security groups must be <30 characters
      group = "eu";
   }

   @Override
   protected void assertDefaultWorks() {
      Template defaultTemplate = client.templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

   @Override
   @Test(enabled = true, dependsOnMethods = "testReboot")
   public void testSuspendResume() throws Exception {
      try {
         super.testSuspendResume();
         assert false;
      } catch (HttpResponseException e) {
         // ebs backed not yet available
      }
   }

   @Override
   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testListNodes() throws Exception {
      super.testListNodes();
   }

   @Override
   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testGetNodesWithDetails() throws Exception {
      super.testGetNodesWithDetails();
   }

   @Override
   @Test(enabled = true, dependsOnMethods = { "testListNodes", "testGetNodesWithDetails" })
   public void testDestroyNodes() {
      super.testDestroyNodes();
   }

}
