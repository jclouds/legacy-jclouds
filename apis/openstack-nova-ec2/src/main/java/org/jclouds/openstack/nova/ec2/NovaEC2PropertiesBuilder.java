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
package org.jclouds.openstack.nova.ec2;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_RELAX_HOSTNAME;
import static org.jclouds.Constants.PROPERTY_TRUST_ALL_CERTS;
import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_DELIMITER;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;

import org.jclouds.ec2.EC2PropertiesBuilder;

/**
 * Builds properties used in NovaEC2 Clients
 * 
 * @author Adrian Cole
 */
public class NovaEC2PropertiesBuilder extends EC2PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_API_VERSION, "2009-04-04");
      properties.setProperty(PROPERTY_ENDPOINT, "http://localhost:8773/services/Cloud");
      properties.setProperty(PROPERTY_REGIONS, "nova");
      properties.setProperty(PROPERTY_EC2_AMI_OWNERS, "admin");
      // hash characters are banned
      properties.setProperty(RESOURCENAME_DELIMITER, "-");
      // often, we are dealing with IP addresses, not hostnames
      properties.setProperty(PROPERTY_RELAX_HOSTNAME, "true");
      properties.setProperty(PROPERTY_TRUST_ALL_CERTS, "true");
      return properties;
   }

   public NovaEC2PropertiesBuilder(Properties properties) {
      super(properties);
   }

}
