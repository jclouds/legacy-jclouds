/*
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
package org.jclouds.vcloud.director.v1_5;

import static org.jclouds.Constants.*;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.*;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in VCloudDirector clients
 * 
 * @author Adrian Cole
 */
public class VCloudDirectorPropertiesBuilder extends PropertiesBuilder {

   @Override
   public Properties defaultProperties() {
      Properties properties = super.defaultProperties();

      properties.setProperty(PROPERTY_ENDPOINT, "https://vcloudbeta.bluelock.com/api");
      properties.setProperty(PROPERTY_SESSION_INTERVAL, Integer.toString(30 * 60));
      properties.setProperty(PROPERTY_API_VERSION, "1.5");

      properties.setProperty(PROPERTY_VCLOUD_DIRECTOR_XML_NAMESPACE,
            String.format("http://www.vmware.com/vcloud/v${%s}", PROPERTY_VCLOUD_DIRECTOR_VERSION_SCHEMA));
      properties.setProperty(PROPERTY_SESSION_INTERVAL, Integer.toString(8 * 60));
      properties.setProperty(PROPERTY_VCLOUD_DIRECTOR_XML_SCHEMA, PROPERTY_ENDPOINT + "/v1.5/schema/master.xsd");
      
      // TODO integrate these with the {@link ComputeTimeouts} instead of having a single timeout for everything.
      properties.setProperty(PROPERTY_SESSION_INTERVAL, Integer.toString(300));
      properties.setProperty(PROPERTY_VCLOUD_DIRECTOR_TIMEOUT_TASK_COMPLETED, Long.toString(1200l * 1000l));

      return properties;
   }

   public VCloudDirectorPropertiesBuilder() {
      super();
   }

   public VCloudDirectorPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
