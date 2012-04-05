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
package org.jclouds.virtualbox;

import static junit.framework.Assert.assertEquals;

import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.jclouds.virtualbox.functions.admin.PreseedCfgServer;
import org.testng.annotations.Test;

/**
 * Tests that jetty is able to serve the preseed.cfg from the provided yaml image. This test is here
 * to have access to the defaultProperties() method in {@link VirtualBoxPropertiesBuilder}.
 * 
 * @author dralves
 * 
 */
@Test(groups = "live", singleThreaded = true, testName = "StartJettyIfNotAlreadyRunningLiveTest")
public class PreseedCfgServerTest {

   @Test
   public void testJettyServerServesPreseedFile() throws Exception {
      Properties props = VirtualBoxApiMetadata.defaultProperties();

      String preconfigurationUrl = props.getProperty(VirtualBoxConstants.VIRTUALBOX_PRECONFIGURATION_URL);

      int port = URI.create(preconfigurationUrl).getPort();

      PreseedCfgServer starter = new PreseedCfgServer();

      starter.start(preconfigurationUrl, BaseVirtualBoxClientLiveTest.getDefaultImage().preseed_cfg);

      String preseedFileFromJetty = IOUtils.toString(new URL("http://127.0.0.1:" + port + "/preseed.cfg").openStream());
      String preseedFileFromFile = BaseVirtualBoxClientLiveTest.getDefaultImage().preseed_cfg + "\n";
      assertEquals(preseedFileFromFile, preseedFileFromJetty);

      starter.stop();
   }
}
