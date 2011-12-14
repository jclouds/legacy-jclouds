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
package org.jclouds.ec2;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AUTO_ALLOCATE_ELASTIC_IPS;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_TIMEOUT_SECURITYGROUP_PRESENT;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in EC2 Clients
 * 
 * @author Adrian Cole
 */
public class EC2PropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      properties.setProperty(PROPERTY_API_VERSION, EC2AsyncClient.VERSION);
      properties.setProperty(PROPERTY_EC2_AMI_OWNERS, "*");
      properties.setProperty(PROPERTY_EC2_TIMEOUT_SECURITYGROUP_PRESENT, "500");
      properties.setProperty(PROPERTY_EC2_AUTO_ALLOCATE_ELASTIC_IPS, "false");
      return properties;
   }

   public EC2PropertiesBuilder(Properties properties) {
      super(properties);
   }

   public EC2PropertiesBuilder() {
      super();
   }
}
