/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not computee this file except in compliance
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
package org.jclouds.trystack.nova;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.openstack.nova.v1_1.reference.NovaConstants.PROPERTY_NOVA_AUTO_GENERATE_KEYPAIRS;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.nova.v1_1.NovaPropertiesBuilder;

/**
 * 
 * @author Adrian Cole
 */
public class TryStackNovaPropertiesBuilder extends NovaPropertiesBuilder {

   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ISO3166_CODES, "US-CA");
      properties.setProperty(PROPERTY_ENDPOINT, "https://nova-api.trystack.org:5443");
      properties.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      properties.setProperty(KeystoneProperties.CREDENTIAL_TYPE, "passwordCredentials");
      // auth fail can happen while cloud-init applies keypair updates
      properties.setProperty("jclouds.ssh.max-retries", "7");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      properties.setProperty(PROPERTY_NOVA_AUTO_GENERATE_KEYPAIRS, "true");
      return properties;
   }

   public TryStackNovaPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
