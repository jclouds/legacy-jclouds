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
package org.jclouds.hpcloud.objectstorage.lvs;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;

import java.util.Properties;

import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.services.ServiceType;
import org.jclouds.openstack.swift.SwiftPropertiesBuilder;

/**
 * @author Jeremy Daggett
 */
public class HPCloudObjectStorageLasVegasPropertiesBuilder extends SwiftPropertiesBuilder {

   public HPCloudObjectStorageLasVegasPropertiesBuilder(Properties properties) {
      super(properties);
   }
	
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(KeystoneProperties.SERVICE_TYPE, ServiceType.OBJECT_STORE);
      properties.setProperty(PROPERTY_ISO3166_CODES, "US-NV");
      properties.setProperty(PROPERTY_ENDPOINT, "https://region-a.geo-1.identity.hpcloudsvc.com");
      properties.setProperty(PROPERTY_API_VERSION, "2.0");
      
      return properties;
   }
      
}
