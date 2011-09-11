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
package org.jclouds.aws.ec2;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMIs;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_REGIONS;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;

import java.util.Properties;
import java.util.logging.Logger;

import org.jclouds.aws.domain.Region;

/**
 * Builds properties used in EC2 Clients
 * 
 * @author Adrian Cole
 */
public class AWSEC2PropertiesBuilder extends org.jclouds.ec2.EC2PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      // sometimes, like in ec2, stop takes a very long time, perhaps
      // due to volume management. one example spent 2 minutes moving
      // from stopping->stopped state on an ec2 micro
      properties.setProperty(PROPERTY_TIMEOUT_NODE_SUSPENDED, 120 * 1000 + "");
      // auth fail sometimes happens in EC2, as the rc.local script that injects the
      // authorized key executes after ssh has started
      properties.setProperty("jclouds.ssh.max-retries", "7");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      properties.setProperty(PROPERTY_ENDPOINT, "https://ec2.us-east-1.amazonaws.com");
      properties.putAll(Region.regionProperties());
      properties.remove(PROPERTY_EC2_AMI_OWNERS);
      // amazon, alestic, canonical, and rightscale
      properties.setProperty(PROPERTY_EC2_AMI_QUERY,
               "owner-id=137112412989,063491364108,099720109477,411009282317;state=available;image-type=machine");
      // amis that work with the cluster instances
      properties.setProperty(PROPERTY_EC2_CC_REGIONS, Region.US_EAST_1);
      properties
               .setProperty(
                        PROPERTY_EC2_CC_AMI_QUERY,
                        "virtualization-type=hvm;architecture=x86_64;owner-id=137112412989,099720109477;hypervisor=xen;state=available;image-type=machine;root-device-type=ebs");
      return properties;
   }

   public AWSEC2PropertiesBuilder() {
      super();
   }

   public AWSEC2PropertiesBuilder(Properties properties) {
      super(properties);
   }

   @Override
   public Properties build() {
      Properties props = super.build();
      warnAndReplaceIfUsingOldImageKey(props);
      warnAndReplaceIfUsingOldCCImageKey(props);
      return props;
   }

   protected void warnAndReplaceIfUsingOldImageKey(Properties props) {
      if (props.containsKey(PROPERTY_EC2_AMI_OWNERS)) {
         StringBuilder query = new StringBuilder();
         String owners = properties.remove(PROPERTY_EC2_AMI_OWNERS).toString();
         if ("*".equals(owners))
            query.append("state=available;image-type=machine");
         else if (!"".equals(owners))
            query.append("owner-id=").append(owners).append(";state=available;image-type=machine");
         else if ("".equals(owners))
            query = new StringBuilder();
         props.setProperty(PROPERTY_EC2_AMI_QUERY, query.toString());
         Logger.getAnonymousLogger().warning(
                  String.format("Property %s is deprecated, please use new syntax: %s=%s", PROPERTY_EC2_AMI_OWNERS,
                           PROPERTY_EC2_AMI_QUERY, query.toString()));
      }
   }

   protected void warnAndReplaceIfUsingOldCCImageKey(Properties props) {
      if (props.containsKey(PROPERTY_EC2_CC_AMIs)) {
         String amis = properties.remove(PROPERTY_EC2_CC_AMIs).toString();
         String value = ("".equals(amis)) ? "" : "image-id=" + amis.replace("us-east-1/", "");
         props.setProperty(PROPERTY_EC2_CC_AMI_QUERY, value);
         Logger.getAnonymousLogger().warning(
                  String.format("Property %s is deprecated, please use new syntax: %s=%s", PROPERTY_EC2_CC_AMIs,
                           PROPERTY_EC2_CC_AMI_QUERY, value));
      }
   }

}
