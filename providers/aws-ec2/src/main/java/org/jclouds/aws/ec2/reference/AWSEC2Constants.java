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
package org.jclouds.aws.ec2.reference;

import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.reference.EC2Constants;

/**
 * Configuration properties and constants used in EC2 connections.
 * 
 * @author Adrian Cole
 */
public interface AWSEC2Constants extends EC2Constants {
   /**
    * expression to find amis that work on the cluster instance type <br/>
    * ex. {@code
    * virtualization-type=hvm;architecture=x86_64;owner-id=137112412989,099720109477;hypervisor=xen;
    * state=available;image-type=machine;root-device-type=ebs}
    * 
    * @see InstanceType.CC1_4XLARGE
    */
   public static final String PROPERTY_EC2_CC_AMI_QUERY = "jclouds.ec2.cc-ami-query";
   public static final String PROPERTY_EC2_CC_REGIONS = "jclouds.ec2.cc-regions";
   public static final String PROPERTY_EC2_AMI_QUERY = "jclouds.ec2.ami-query";
   /**
    * If this property is set to true(default), jclouds generate a name for each instance based on
    * the group. ex. i-ef34ae2 becomes hadoop-ef34ae2.
    */
   public static final String PROPERTY_EC2_GENERATE_INSTANCE_NAMES = "jclouds.ec2.generate-instance-names";

}
