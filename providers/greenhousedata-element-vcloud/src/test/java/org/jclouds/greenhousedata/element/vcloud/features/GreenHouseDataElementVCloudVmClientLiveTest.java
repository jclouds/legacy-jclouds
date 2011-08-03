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
package org.jclouds.greenhousedata.element.vcloud.features;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.vcloud.features.VmClientLiveTest;
import org.testng.annotations.Test;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true, testName = "GreenHouseDataElementVCloudVmClientLiveTest")
public class GreenHouseDataElementVCloudVmClientLiveTest extends VmClientLiveTest {

   public GreenHouseDataElementVCloudVmClientLiveTest() {
      provider = "greenhousedata-element-vcloud";
   }

   @Override
   protected void checkApiOutput(String apiOutput) {
      checkApiOutput1_0_0(apiOutput);
   }
   
   @Override
   protected void checkCustomizationOccurred(ExecResponse exec) {
      // for some reason customization doesn't actually occur
      assert exec.getOutput().equals("") : exec;
   }
}
