/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox;

import org.jclouds.PropertiesBuilder;
import org.jclouds.virtualbox.config.VirtualBoxConstants;

import java.util.Properties;

import static org.jclouds.Constants.*;

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
      properties.put(PROPERTY_IDENTITY, "administrator");
      properties.put(PROPERTY_CREDENTIAL, "12345");
      properties.put(PROPERTY_ENDPOINT, "http://localhost:18083/");
      properties.put(VirtualBoxConstants.VIRTUALBOX_PRESEED_URL, "http://dl.dropbox.com/u/693111/preseed.cfg");
      properties.put(VirtualBoxConstants.VIRTUALBOX_SNAPSHOT_DESCRIPTION, "jclouds-virtualbox-snaphot");
      // TODO: Add more properties and use the wired properties from test code.
      return properties;
   }
}
