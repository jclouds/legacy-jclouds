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
package org.jclouds.trmk.enterprisecloud;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in TerremarkEnterpriseCloud Clients
 * 
 * @author Adrian Cole
 */
public class TerremarkEnterpriseCloudPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      // TODO replace with the actual rest url
      properties.setProperty(PROPERTY_ENDPOINT, "http://209.251.187.125/livespec");
      properties.setProperty(PROPERTY_API_VERSION, "2011-07-01");
      return properties;
   }

   public TerremarkEnterpriseCloudPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
