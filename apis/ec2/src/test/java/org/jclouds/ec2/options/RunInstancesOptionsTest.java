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
package org.jclouds.ec2.options;

import static org.jclouds.ec2.options.RunInstancesOptions.Builder.asType;
import static org.jclouds.ec2.options.RunInstancesOptions.Builder.withBlockDeviceMappings;
import static org.jclouds.ec2.options.RunInstancesOptions.Builder.withKernelId;
import static org.jclouds.ec2.options.RunInstancesOptions.Builder.withKeyName;
import static org.jclouds.ec2.options.RunInstancesOptions.Builder.withRamdisk;
import static org.jclouds.ec2.options.RunInstancesOptions.Builder.withSecurityGroup;
import static org.jclouds.ec2.options.RunInstancesOptions.Builder.withUserData;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests possible uses of RunInstancesOptions and RunInstancesOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class RunInstancesOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(RunInstancesOptions.class);
      assert !String.class.isAssignableFrom(RunInstancesOptions.class);
   }

   @Test
   public void testWithKeyName() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withKeyName("test");
      assertEquals(options.buildFormParameters().get("KeyName"), Collections.singletonList("test"));
   }

   @Test
   public void testNullWithKeyName() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("KeyName"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithKeyNameStatic() {
      RunInstancesOptions options = withKeyName("test");
      assertEquals(options.buildFormParameters().get("KeyName"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithKeyNameNPE() {
      withKeyName(null);
   }

   @Test
   public void testWithSecurityGroup() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withSecurityGroup("test");
      assertEquals(options.buildFormParameters().get("SecurityGroup.1"), Collections.singletonList("test"));
   }

   @Test
   public void testNullWithSecurityGroup() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("SecurityGroup"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithSecurityGroupStatic() {
      RunInstancesOptions options = withSecurityGroup("test");
      assertEquals(options.buildFormParameters().get("SecurityGroup.1"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithSecurityGroupNPE() {
      withSecurityGroup(null);
   }

   @Test
   public void testNullWithAdditionalInfo() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("AdditionalInfo"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithUserData() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withUserData("test".getBytes());
      assertEquals(options.buildFormParameters().get("UserData"), Collections.singletonList("dGVzdA=="));
   }

   @Test
   public void testNullWithUserData() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("UserData"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithUserDataStatic() {
      RunInstancesOptions options = withUserData("test".getBytes());
      assertEquals(options.buildFormParameters().get("UserData"), Collections.singletonList("dGVzdA=="));
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
      RunInstancesOptions options = new RunInstancesOptions();
      options.asType(InstanceType.C1_XLARGE);
      assertEquals(options.buildFormParameters().get("InstanceType"), Collections.singletonList("c1.xlarge"));
   }

   @Test
   public void testNullWithInstanceType() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("InstanceType"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithInstanceTypeStatic() {
      RunInstancesOptions options = asType(InstanceType.C1_XLARGE);
      assertEquals(options.buildFormParameters().get("InstanceType"), Collections.singletonList("c1.xlarge"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithInstanceTypeNPE() {
      asType(null);
   }

   @Test
   public void testWithKernelId() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withKernelId("test");
      assertEquals(options.buildFormParameters().get("KernelId"), Collections.singletonList("test"));
   }

   @Test
   public void testNullWithKernelId() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("KernelId"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithKernelIdStatic() {
      RunInstancesOptions options = withKernelId("test");
      assertEquals(options.buildFormParameters().get("KernelId"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithKernelIdNPE() {
      withKernelId(null);
   }

   @Test
   public void testWithRamdisk() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withRamdisk("test");
      assertEquals(options.buildFormParameters().get("RamdiskId"), Collections.singletonList("test"));
   }

   @Test
   public void testNullWithRamdisk() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("RamdiskId"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithRamdiskStatic() {
      RunInstancesOptions options = withRamdisk("test");
      assertEquals(options.buildFormParameters().get("RamdiskId"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithRamdiskNPE() {
      withRamdisk(null);
   }

   @Test
   public void testNullWithVirtualName() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.VirtualName"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithBlockDeviceMapping() {
      BlockDeviceMapping mapping = new BlockDeviceMapping.MapNewVolumeToDevice("/dev/sda1", 120, true);
      RunInstancesOptions options = new RunInstancesOptions().withBlockDeviceMappings(ImmutableSet
               .<BlockDeviceMapping> of(mapping));
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.1.DeviceName"), Collections
               .singletonList("/dev/sda1"));
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.1.Ebs.VolumeSize"), Collections
               .singletonList("120"));
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.1.Ebs.DeleteOnTermination"), Collections
               .singletonList("true"));
   }

   @Test
   public void testNullWithBlockDeviceMapping() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithBlockDeviceMappingStatic() {
      BlockDeviceMapping mapping = new BlockDeviceMapping.MapNewVolumeToDevice("/dev/sda1", 120, true);
      RunInstancesOptions options = withBlockDeviceMappings(ImmutableSet
               .<BlockDeviceMapping> of(mapping));
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.1.DeviceName"), Collections
               .singletonList("/dev/sda1"));
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.1.Ebs.VolumeSize"), Collections
               .singletonList("120"));
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.1.Ebs.DeleteOnTermination"), Collections
               .singletonList("true"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithBlockDeviceMappingNPE() {
      withBlockDeviceMappings(null);
   }

}
