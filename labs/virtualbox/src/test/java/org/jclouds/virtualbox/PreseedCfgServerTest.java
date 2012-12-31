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

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.jclouds.compute.domain.Image;
import org.jclouds.util.Strings2;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.jclouds.virtualbox.domain.YamlImage;
import org.jclouds.virtualbox.functions.YamlImagesFromFileConfig;
import org.jclouds.virtualbox.functions.admin.ImagesToYamlImagesFromYamlDescriptor;
import org.jclouds.virtualbox.functions.admin.PreseedCfgServer;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests that jetty is able to serve the preseed.cfg from the provided yaml
 * image. This test is here to have access to the defaultProperties() method in
 * {@link VirtualBoxPropertiesBuilder}.
 * 
 * @author dralves
 * 
 */
@Test(groups = "live", singleThreaded = true, testName = "PreseedCfgServerTest")
public class PreseedCfgServerTest {
   private static final String lineSeparator = System.getProperty("line.separator");

   @Test
   public void testJettyServerServesPreseedFile() throws Exception {
      Properties props = VirtualBoxApiMetadata.defaultProperties();

      String preconfigurationUrl = props.getProperty(VirtualBoxConstants.VIRTUALBOX_PRECONFIGURATION_URL);

      int port = URI.create(preconfigurationUrl).getPort();

      PreseedCfgServer starter = new PreseedCfgServer();

      starter.start(preconfigurationUrl, getDefaultImage().preseed_cfg);

      String preseedFileFromJetty = Strings2.toStringAndClose(new URL("http://127.0.0.1:" + port + "/preseed.cfg").openStream());
      String preseedFileFromFile = getDefaultImage().preseed_cfg + lineSeparator;
      assertEquals(preseedFileFromFile, preseedFileFromJetty);

      starter.stop();
   }

   public static YamlImage getDefaultImage() {
      Map<Image, YamlImage> images = new ImagesToYamlImagesFromYamlDescriptor(new YamlImagesFromFileConfig(
            "/default-images.yaml")).get();
      return Iterables.get(images.values(), 0);
   }
}
