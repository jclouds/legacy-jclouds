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

package org.jclouds.epc.compute;

import java.util.Properties;

import org.jclouds.eucalyptus.compute.EucalyptusComputeServiceLiveTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "EucalyptusPartnerCloudEucalyptusComputeServiceLiveTest")
public class EucalyptusPartnerCloudEucalyptusComputeServiceLiveTest extends EucalyptusComputeServiceLiveTest {

   public EucalyptusPartnerCloudEucalyptusComputeServiceLiveTest() {
      provider = "eucalyptus-partnercloud-ec2";
      // security groups must be <30 characters
      group = "eu";
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      if (System.getProperties().containsKey("test.eucalyptus-partnercloud-ec2.virtualization-type"))
         overrides.setProperty("eucalyptus-partnercloud-ec2.virtualization-type", System
                  .getProperty("test.eucalyptus-partnercloud-ec2.virtualization-type"));
      return overrides;
   }

   // test hangs
   @Override
   public void testExtendedOptionsAndLogin() throws Exception {
   }
}
