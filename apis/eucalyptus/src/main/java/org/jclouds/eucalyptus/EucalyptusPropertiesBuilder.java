/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.eucalyptus;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_PORT_OPEN;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;

import org.jclouds.ec2.EC2PropertiesBuilder;

/**
 * Builds properties used in Eucalyptus Clients
 * 
 * @author Adrian Cole
 */
public class EucalyptusPropertiesBuilder extends EC2PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ENDPOINT, "http://173.205.188.130:8773/services/Eucalyptus");
      properties.setProperty(PROPERTY_REGIONS, "Eucalyptus");
      properties.setProperty(PROPERTY_EC2_AMI_OWNERS, "admin");
      properties.setProperty(PROPERTY_TIMEOUT_PORT_OPEN, 5 * 60 * 1000 + "");
      return properties;
   }

   public EucalyptusPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
