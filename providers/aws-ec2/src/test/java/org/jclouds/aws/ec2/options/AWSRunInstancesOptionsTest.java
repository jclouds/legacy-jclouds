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
package org.jclouds.aws.ec2.options;

import static org.jclouds.aws.ec2.options.AWSRunInstancesOptions.Builder.asType;
import static org.jclouds.aws.ec2.options.AWSRunInstancesOptions.Builder.enableMonitoring;
import static org.jclouds.aws.ec2.options.AWSRunInstancesOptions.Builder.withBlockDeviceMappings;
import static org.jclouds.aws.ec2.options.AWSRunInstancesOptions.Builder.withIAMInstanceProfileArn;
import static org.jclouds.aws.ec2.options.AWSRunInstancesOptions.Builder.withIAMInstanceProfileName;
import static org.jclouds.aws.ec2.options.AWSRunInstancesOptions.Builder.withKernelId;
import static org.jclouds.aws.ec2.options.AWSRunInstancesOptions.Builder.withKeyName;
import static org.jclouds.aws.ec2.options.AWSRunInstancesOptions.Builder.withRamdisk;
import static org.jclouds.aws.ec2.options.AWSRunInstancesOptions.Builder.withSecurityGroup;
import static org.jclouds.aws.ec2.options.AWSRunInstancesOptions.Builder.withSecurityGroupId;
import static org.jclouds.aws.ec2.options.AWSRunInstancesOptions.Builder.withSubnetId;
import static org.jclouds.aws.ec2.options.AWSRunInstancesOptions.Builder.withUserData;
import static org.testng.Assert.assertEquals;

import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Tests possible uses of AWSRunInstancesOptions and AWSRunInstancesOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class AWSRunInstancesOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(AWSRunInstancesOptions.class);
      assert !String.class.isAssignableFrom(AWSRunInstancesOptions.class);
   }

   @Test
   public void testWithKeyName() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      options.withKeyName("test");
      assertEquals(options.buildFormParameters().get("KeyName"), ImmutableList.of("test"));
   }

   @Test
   public void testNullWithKeyName() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("KeyName"), ImmutableList.of());
   }

   @Test
   public void testWithKeyNameStatic() {
      AWSRunInstancesOptions options = withKeyName("test");
      assertEquals(options.buildFormParameters().get("KeyName"), ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithKeyNameNPE() {
      withKeyName(null);
   }

   @Test
   public void testWithSecurityGroup() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      options.withSecurityGroup("test");
      assertEquals(options.buildFormParameters().get("SecurityGroup.1"), ImmutableList.of("test"));
   }

   @Test
   public void testNullWithSecurityGroup() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("SecurityGroup"), ImmutableList.of());
   }

   @Test
   public void testWithSecurityGroupStatic() {
      AWSRunInstancesOptions options = withSecurityGroup("test");
      assertEquals(options.buildFormParameters().get("SecurityGroup.1"), ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithSecurityGroupNPE() {
      withSecurityGroup(null);
   }
   

   @Test
   public void testWithSecurityGroupId() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      options.withSecurityGroupId("test");
      assertEquals(options.buildFormParameters().get("SecurityGroupId.1"), ImmutableList.of("test"));
   }

   @Test
   public void testNullWithSecurityGroupId() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("SecurityGroupId"), ImmutableList.of());
   }

   @Test
   public void testWithSecurityGroupIdStatic() {
      AWSRunInstancesOptions options = withSecurityGroupId("test");
      assertEquals(options.buildFormParameters().get("SecurityGroupId.1"), ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithSecurityGroupIdNPE() {
      withSecurityGroupId(null);
   }

   @Test
   public void testNullWithAdditionalInfo() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("AdditionalInfo"), ImmutableList.of());
   }

   @Test
   public void testWithUserData() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      options.withUserData("test".getBytes());
      assertEquals(options.buildFormParameters().get("UserData"), ImmutableList.of("dGVzdA=="));
   }

   @Test
   public void testNullWithUserData() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("UserData"), ImmutableList.of());
   }

   @Test
   public void testWithUserDataStatic() {
      AWSRunInstancesOptions options = withUserData("test".getBytes());
      assertEquals(options.buildFormParameters().get("UserData"), ImmutableList.of("dGVzdA=="));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithUserDataNPE() {
      withUserData(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testWithUserDataEmpty() {
      withUserData("".getBytes());
   }

   @Test
   public void testWithInstanceType() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      options.asType(InstanceType.C1_XLARGE);
      assertEquals(options.buildFormParameters().get("InstanceType"), ImmutableList.of("c1.xlarge"));
   }

   @Test
   public void testNullWithInstanceType() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("InstanceType"), ImmutableList.of());
   }

   @Test
   public void testWithInstanceTypeStatic() {
      AWSRunInstancesOptions options = asType(InstanceType.C1_XLARGE);
      assertEquals(options.buildFormParameters().get("InstanceType"), ImmutableList.of("c1.xlarge"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithInstanceTypeNPE() {
      asType(null);
   }

   @Test
   public void testWithKernelId() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      options.withKernelId("test");
      assertEquals(options.buildFormParameters().get("KernelId"), ImmutableList.of("test"));
   }

   @Test
   public void testNullWithKernelId() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("KernelId"), ImmutableList.of());
   }

   @Test
   public void testWithKernelIdStatic() {
      AWSRunInstancesOptions options = withKernelId("test");
      assertEquals(options.buildFormParameters().get("KernelId"), ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithKernelIdNPE() {
      withKernelId(null);
   }

   @Test
   public void testWithMonitoringEnabled() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      options.enableMonitoring();
      assertEquals(options.buildFormParameters().get("Monitoring.Enabled"), ImmutableList.of("true"));
   }

   @Test
   public void testNullWithMonitoringEnabled() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("Monitoring.Enabled"), ImmutableList.of());
   }

   @Test
   public void testWithMonitoringEnabledStatic() {
      AWSRunInstancesOptions options = enableMonitoring();
      assertEquals(options.buildFormParameters().get("Monitoring.Enabled"), ImmutableList.of("true"));
   }

   @Test
   public void testWithSubnetId() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      options.withSubnetId("test");
      assertEquals(options.buildFormParameters().get("SubnetId"), ImmutableList.of("test"));
   }

   @Test
   public void testNullWithSubnetId() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("SubnetId"), ImmutableList.of());
   }

   @Test
   public void testWithSubnetIdStatic() {
      AWSRunInstancesOptions options = withSubnetId("test");
      assertEquals(options.buildFormParameters().get("SubnetId"), ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithSubnetIdNPE() {
      withSubnetId(null);
   }

   @Test
   public void testWithIAMInstanceProfileArn() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      options
            .withIAMInstanceProfileArn("arn:aws:iam::123456789012:instance-profile/application_abc/component_xyz/Webserver");
      assertEquals(options.buildFormParameters().get("IamInstanceProfile.Arn"),
            ImmutableList.of("arn:aws:iam::123456789012:instance-profile/application_abc/component_xyz/Webserver"));
   }

   @Test
   public void testNullWithIAMInstanceProfileArn() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("IamInstanceProfile.Arn"), ImmutableList.of());
   }

   @Test
   public void testWithIAMInstanceProfileArnStatic() {
      AWSRunInstancesOptions options = withIAMInstanceProfileArn("arn:aws:iam::123456789012:instance-profile/application_abc/component_xyz/Webserver");
      assertEquals(options.buildFormParameters().get("IamInstanceProfile.Arn"),
            ImmutableList.of("arn:aws:iam::123456789012:instance-profile/application_abc/component_xyz/Webserver"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithIAMInstanceProfileArnNPE() {
      withIAMInstanceProfileArn(null);
   }

   @Test
   public void testWithIAMInstanceProfileName() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      options.withIAMInstanceProfileName("Webserver");
      assertEquals(options.buildFormParameters().get("IamInstanceProfile.Name"), ImmutableList.of("Webserver"));
   }

   @Test
   public void testNullWithIAMInstanceProfileName() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("IamInstanceProfile.Name"), ImmutableList.of());
   }

   @Test
   public void testWithIAMInstanceProfileNameStatic() {
      AWSRunInstancesOptions options = withIAMInstanceProfileName("Webserver");
      assertEquals(options.buildFormParameters().get("IamInstanceProfile.Name"), ImmutableList.of("Webserver"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithIAMInstanceProfileNameNPE() {
      withIAMInstanceProfileName(null);
   }

   @Test
   public void testWithRamdisk() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      options.withRamdisk("test");
      assertEquals(options.buildFormParameters().get("RamdiskId"), ImmutableList.of("test"));
   }

   @Test
   public void testNullWithRamdisk() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("RamdiskId"), ImmutableList.of());
   }

   @Test
   public void testWithRamdiskStatic() {
      AWSRunInstancesOptions options = withRamdisk("test");
      assertEquals(options.buildFormParameters().get("RamdiskId"), ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithRamdiskNPE() {
      withRamdisk(null);
   }

   @Test
   public void testNullWithVirtualName() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.VirtualName"), ImmutableList.of());
   }

   @Test
   public void testWithBlockDeviceMapping() {
      BlockDeviceMapping mapping = new BlockDeviceMapping.MapNewVolumeToDevice("/dev/sda1", 120, true);
      AWSRunInstancesOptions options = new AWSRunInstancesOptions().withBlockDeviceMappings(ImmutableSet
               .<BlockDeviceMapping> of(mapping));
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.1.DeviceName"),
               ImmutableList.of("/dev/sda1"));
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.1.Ebs.VolumeSize"),
               ImmutableList.of("120"));
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.1.Ebs.DeleteOnTermination"),
               ImmutableList.of("true"));
   }

   @Test
   public void testNullWithBlockDeviceMapping() {
      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping"), ImmutableList.of());
   }

   @Test
   public void testWithBlockDeviceMappingStatic() {
      BlockDeviceMapping mapping = new BlockDeviceMapping.MapNewVolumeToDevice("/dev/sda1", 120, true);
      AWSRunInstancesOptions options = withBlockDeviceMappings(ImmutableSet.<BlockDeviceMapping> of(mapping));
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.1.DeviceName"),
               ImmutableList.of("/dev/sda1"));
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.1.Ebs.VolumeSize"),
               ImmutableList.of("120"));
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.1.Ebs.DeleteOnTermination"),
               ImmutableList.of("true"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithBlockDeviceMappingNPE() {
      withBlockDeviceMappings(null);
   }

}
