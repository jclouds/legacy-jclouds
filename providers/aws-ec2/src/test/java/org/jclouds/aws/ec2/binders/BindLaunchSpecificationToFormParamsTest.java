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
package org.jclouds.aws.ec2.binders;

import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;

import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.encryption.internal.Base64;
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
            Base64.encodeBytes("hello".getBytes())));
   }

   @Test
   public void testApplyWithSecurityId() throws UnknownHostException {
      LaunchSpecification spec = LaunchSpecification.builder().instanceType(InstanceType.T1_MICRO).imageId("ami-123")
            .securityGroupId("sid-foo").build();

      assertEquals(binder.apply(spec), ImmutableMap.of("LaunchSpecification.InstanceType", "t1.micro",
            "LaunchSpecification.ImageId", "ami-123", "LaunchSpecification.SecurityGroupId.1", "sid-foo"));
   }
}
