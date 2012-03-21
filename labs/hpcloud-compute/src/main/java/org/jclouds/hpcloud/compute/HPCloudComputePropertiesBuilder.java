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
package org.jclouds.hpcloud.compute;

import org.jclouds.openstack.nova.v1_1.NovaPropertiesBuilder;
import org.jclouds.openstack.nova.v1_1.reference.NovaConstants;

import java.util.Properties;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;

/**
 * 
 * @author Adrian Cole
 */
public class HPCloudComputePropertiesBuilder extends NovaPropertiesBuilder {

   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ISO3166_CODES, "US-NV");
      properties.setProperty(PROPERTY_ENDPOINT, "https://region-a.geo-1.identity.hpcloudsvc.com:35357");
      properties.setProperty(NovaConstants.PROPERTY_NOVA_AUTO_ALLOCATE_FLOATING_IPS, "true");
      return properties;
   }

   public HPCloudComputePropertiesBuilder(Properties properties) {
      super(properties);
   }

}
