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
package org.jclouds.openstack.nova.v1_1;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.openstack.nova.v1_1.reference.NovaConstants.PROPERTY_NOVA_AUTO_ALLOCATE_FLOATING_IPS;
import static org.jclouds.openstack.nova.v1_1.reference.NovaConstants.PROPERTY_NOVA_AUTO_GENERATE_KEYPAIRS;
import static org.jclouds.openstack.nova.v1_1.reference.NovaConstants.PROPERTY_NOVA_TIMEOUT_SECURITYGROUP_PRESENT;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.services.ServiceType;

/**
 * Builds properties used in Nova Clients
 * 
 * @author Adrian Cole
 */
public class NovaPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ENDPOINT, "http://localhost:5000");
      properties.setProperty(KeystoneProperties.SERVICE_TYPE, ServiceType.COMPUTE);
      // TODO: this doesn't actually do anything yet.
      properties.setProperty(KeystoneProperties.VERSION, "2.0");
      properties.setProperty(PROPERTY_API_VERSION, "1.1");
      properties.setProperty(PROPERTY_NOVA_AUTO_ALLOCATE_FLOATING_IPS, "false");
      properties.setProperty(PROPERTY_NOVA_AUTO_GENERATE_KEYPAIRS, "false");
      properties.setProperty(PROPERTY_NOVA_TIMEOUT_SECURITYGROUP_PRESENT, "500");
      return properties;
   }

   public NovaPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
