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

import static org.jclouds.Constants.*;
import static org.jclouds.Constants.PROPERTY_BUILD_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_IMAGE_AUTHENTICATE_SUDO;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_IMAGE_LOGIN_USER;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_DIR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGES_DESCRIPTOR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_PRECONFIGURATION_URL;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_WORKINGDIR;

import java.io.File;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties for VirtualBox integration.
 * 
 * @author Mattias Holmqvist
 */
public class VirtualBoxPropertiesBuilder extends PropertiesBuilder {

   public VirtualBoxPropertiesBuilder() {
      super();
   }

   public VirtualBoxPropertiesBuilder(Properties properties) {
      super(properties);
   }

   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.put(PROPERTY_ENDPOINT, "http://localhost:18083/");
      // later version not in maven, yet
      properties.put(PROPERTY_API_VERSION, "4.1.4");

      properties.put(PROPERTY_BUILD_VERSION, "4.1.8r75467");
      properties.put(PROPERTY_IMAGE_LOGIN_USER, "toor:password");
      properties.put(PROPERTY_IMAGE_AUTHENTICATE_SUDO, "true");
      

      properties.put(VIRTUALBOX_INSTALLATION_KEY_SEQUENCE, "<Esc><Esc><Enter> "
               + "/install/vmlinuz noapic preseed/url=PRECONFIGURATION_URL "
               + "debian-installer=en_US auto locale=en_US kbd-chooser/method=us " + "hostname=" + "HOSTNAME "
               + "fb=false debconf/frontend=noninteractive "
               + "keyboard-configuration/layout=USA keyboard-configuration/variant=USA console-setup/ask_detect=false "
               + "initrd=/install/initrd.gz -- <Enter>");

      String workingDir = System.getProperty("test.virtualbox.workingDir", VIRTUALBOX_DEFAULT_DIR);

      properties.put(VIRTUALBOX_WORKINGDIR, workingDir);

      String yamlDescriptor = System.getProperty("test.virtualbox.image.descriptor.yaml", VIRTUALBOX_WORKINGDIR
               + File.separator + "images.yaml");

      properties.put(VIRTUALBOX_IMAGES_DESCRIPTOR, yamlDescriptor);

      properties.put(VIRTUALBOX_PRECONFIGURATION_URL, "http://10.0.2.2:23232/preseed.cfg");

      return properties;
   }
}
