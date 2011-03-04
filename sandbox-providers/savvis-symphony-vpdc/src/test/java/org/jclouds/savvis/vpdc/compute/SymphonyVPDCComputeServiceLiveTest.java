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

package org.jclouds.savvis.vpdc.compute;

import java.util.Properties;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.savvis.vpdc.SymphonyVPDCContextBuilder;
import org.jclouds.savvis.vpdc.SymphonyVPDCPropertiesBuilder;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.Test;

/**
 * 
 * 
 * @author Kedar Dave
 */
@Test(groups = "live", enabled = false, sequential = true)
public class SymphonyVPDCComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   public SymphonyVPDCComputeServiceLiveTest() {
      provider = "savvis-symphony-vpdc";
      group = "savvis.jclouds";
   }

   @Override
   protected Properties getRestProperties() {
      // TODO remove these lines when this is registered under jclouds-core/rest.properties
      Properties restProperties = new Properties();
      restProperties.setProperty("savvis-symphony-vpdc.contextbuilder", SymphonyVPDCContextBuilder.class.getName());
      restProperties.setProperty("savvis-symphony-vpdc.propertiesbuilder",
            SymphonyVPDCPropertiesBuilder.class.getName());
      return restProperties;
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

}