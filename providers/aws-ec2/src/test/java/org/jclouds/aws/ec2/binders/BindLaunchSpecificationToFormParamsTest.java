/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.aws.ec2.binders;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.BaseEncoding.base64;
import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;

import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.ec2.domain.InstanceType;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindLaunchSpecificationToFormParamsTest {
   BindLaunchSpecificationToFormParams binder = new BindLaunchSpecificationToFormParams();

   @Test
   public void testApplyWithBlockDeviceMappings() throws UnknownHostException {
      LaunchSpecification spec = LaunchSpecification.builder().instanceType(InstanceType.T1_MICRO).imageId("ami-123")
            .mapNewVolumeToDevice("/dev/sda1", 120, true).build();

      assertEquals(binder.apply(spec), ImmutableMap.of("LaunchSpecification.InstanceType", "t1.micro",
            "LaunchSpecification.ImageId", "ami-123", "LaunchSpecification.BlockDeviceMapping.1.DeviceName",
            "/dev/sda1", "LaunchSpecification.BlockDeviceMapping.1.Ebs.VolumeSize", "120",
            "LaunchSpecification.BlockDeviceMapping.1.Ebs.DeleteOnTermination", "true"));
   }

   @Test
   public void testApplyWithUserData() throws UnknownHostException {
      LaunchSpecification spec = LaunchSpecification.builder().instanceType(InstanceType.T1_MICRO).imageId("ami-123")
            .userData("hello".getBytes()).build();

      assertEquals(binder.apply(spec), ImmutableMap.of("LaunchSpecification.InstanceType", "t1.micro",
            "LaunchSpecification.ImageId", "ami-123", "LaunchSpecification.UserData",
            base64().encode("hello".getBytes(UTF_8))));
   }

   @Test
   public void testApplyWithSecurityId() throws UnknownHostException {
      LaunchSpecification spec = LaunchSpecification.builder().instanceType(InstanceType.T1_MICRO).imageId("ami-123")
            .securityGroupId("sid-foo").build();

      assertEquals(binder.apply(spec), ImmutableMap.of("LaunchSpecification.InstanceType", "t1.micro",
            "LaunchSpecification.ImageId", "ami-123", "LaunchSpecification.SecurityGroupId.1", "sid-foo"));
   }

   @Test
   public void testApplyWithSubnetId() throws UnknownHostException {
      LaunchSpecification spec = LaunchSpecification.builder().instanceType(InstanceType.T1_MICRO).imageId("ami-123")
            .subnetId("subnet-xyz").build();

      assertEquals(binder.apply(spec), ImmutableMap.of("LaunchSpecification.InstanceType", "t1.micro",
            "LaunchSpecification.ImageId", "ami-123", "LaunchSpecification.SubnetId", "subnet-xyz"));
   }

   @Test
   public void testApplyWithIAMInstanceProfileArn() {
      LaunchSpecification spec = LaunchSpecification.builder()
            .instanceType(InstanceType.T1_MICRO)
            .imageId("ami-123")
            .iamInstanceProfileArn("arn:aws:iam::123456789012:instance-profile/application_abc/component_xyz/Webserver")
            .build();

      assertEquals(binder.apply(spec), ImmutableMap.of("LaunchSpecification.InstanceType", "t1.micro",
            "LaunchSpecification.ImageId", "ami-123", "LaunchSpecification.IamInstanceProfile.Arn",
            "arn:aws:iam::123456789012:instance-profile/application_abc/component_xyz/Webserver"));
   }

   @Test
   public void testApplyWithIAMInstanceProfileName() {
      LaunchSpecification spec = LaunchSpecification.builder().instanceType(InstanceType.T1_MICRO).imageId("ami-123")
            .iamInstanceProfileName("Webserver").build();

      assertEquals(binder.apply(spec), ImmutableMap.of("LaunchSpecification.InstanceType", "t1.micro",
            "LaunchSpecification.ImageId", "ami-123", "LaunchSpecification.IamInstanceProfile.Name", "Webserver"));
   }
}
