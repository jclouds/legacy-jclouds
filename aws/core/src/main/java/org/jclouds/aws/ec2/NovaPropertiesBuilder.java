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

package org.jclouds.aws.ec2;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_CC_AMIs;

import java.util.Properties;

/**
 * Builds properties used in Eucalyptus Clients
 * 
 * @author Adrian Cole
 */
public class NovaPropertiesBuilder extends EC2PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ENDPOINT, "YOU_MUST_SET_" + PROPERTY_ENDPOINT);
      properties.setProperty(PROPERTY_EC2_AMI_OWNERS, "*");
      properties.setProperty(PROPERTY_EC2_CC_AMIs, "");
      return properties;
   }

   public NovaPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
