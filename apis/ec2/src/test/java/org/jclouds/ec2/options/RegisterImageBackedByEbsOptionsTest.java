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

import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.addBlockDeviceFromSnapshot;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.addEphemeralBlockDeviceFromSnapshot;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.addNewBlockDevice;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.addNewEphemeralBlockDevice;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.asArchitecture;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.withDescription;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.withKernelId;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.withRamdisk;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.ec2.domain.Image.Architecture;
import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests possible uses of RegisterImageBackedByEbsOptions and
 * RegisterImageBackedByEbsOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class RegisterImageBackedByEbsOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(RegisterImageBackedByEbsOptions.class);
      assert !String.class.isAssignableFrom(RegisterImageBackedByEbsOptions.class);
   }

   @Test
   public void testWithDescription() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      options.withDescription("test");
      assertEquals(options.buildFormParameters().get("Description"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullWithDescription() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      assertEquals(options.buildFormParameters().get("Description"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithDescriptionStatic() {
      RegisterImageBackedByEbsOptions options = withDescription("test");
      assertEquals(options.buildFormParameters().get("Description"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithDescriptionNPE() {
      withDescription(null);
   }

   @Test
   public void testWithArchitecture() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      options.asArchitecture(Architecture.I386);
      assertEquals(options.buildFormParameters().get("Architecture"), Collections
               .singletonList("i386"));
   }

   @Test
   public void testNullWithArchitecture() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      assertEquals(options.buildFormParameters().get("Architecture"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithArchitectureStatic() {
      RegisterImageBackedByEbsOptions options = asArchitecture(Architecture.I386);
      assertEquals(options.buildFormParameters().get("Architecture"), Collections
               .singletonList("i386"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithArchitectureNPE() {
      asArchitecture(null);
   }

   @Test
   public void testWithKernelId() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      options.withKernelId("test");
      assertEquals(options.buildFormParameters().get("KernelId"), Collections.singletonList("test"));
   }

   @Test
   public void testNullWithKernelId() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      assertEquals(options.buildFormParameters().get("KernelId"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithKernelIdStatic() {
      RegisterImageBackedByEbsOptions options = withKernelId("test");
      assertEquals(options.buildFormParameters().get("KernelId"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithKernelIdNPE() {
      withKernelId(null);
   }

   @Test
   public void testWithRamdisk() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      options.withRamdisk("test");
      assertEquals(options.buildFormParameters().get("RamdiskId"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullWithRamdisk() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      assertEquals(options.buildFormParameters().get("RamdiskId"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithRamdiskStatic() {
      RegisterImageBackedByEbsOptions options = withRamdisk("test");
      assertEquals(options.buildFormParameters().get("RamdiskId"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithRamdiskNPE() {
      withRamdisk(null);
   }

   @Test
   public void testAddBlockDeviceFromSnapshot() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      options.addBlockDeviceFromSnapshot("deviceName", "virtualName", "snapshotId");
      assertEquals(options.buildFormParameters().entries(), ImmutableMultimap.of(
               "BlockDeviceMapping.1.Ebs.DeleteOnTermination", "false",
               "BlockDeviceMapping.1.DeviceName", "deviceName", "BlockDeviceMapping.1.VirtualName",
               "virtualName", "BlockDeviceMapping.1.Ebs.SnapshotId", "snapshotId").entries());
   }

   @Test
   public void testAddBlockDeviceFromSnapshotNullVirtualName() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      options.addBlockDeviceFromSnapshot("deviceName", null, "snapshotId");
      assertEquals(options.buildFormParameters().entries(), ImmutableMultimap.of(
               "BlockDeviceMapping.1.Ebs.DeleteOnTermination", "false",
               "BlockDeviceMapping.1.DeviceName", "deviceName",
               "BlockDeviceMapping.1.Ebs.SnapshotId", "snapshotId").entries());
   }

   @Test
   public void testNullAddBlockDeviceFromSnapshot() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      assertEquals(options.buildFormParameters(), ImmutableMultimap.<String, String> of());
   }

   @Test
   public void testAddBlockDeviceFromSnapshotStatic() {
      RegisterImageBackedByEbsOptions options = addBlockDeviceFromSnapshot("deviceName",
               "virtualName", "snapshotId");
      assertEquals(options.buildFormParameters().entries(), ImmutableMultimap.of(
               "BlockDeviceMapping.1.Ebs.DeleteOnTermination", "false",
               "BlockDeviceMapping.1.DeviceName", "deviceName", "BlockDeviceMapping.1.VirtualName",
               "virtualName", "BlockDeviceMapping.1.Ebs.SnapshotId", "snapshotId").entries());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testAddBlockDeviceFromSnapshotNPE() {
      addBlockDeviceFromSnapshot(null, null, null);
   }

   @Test
   public void testAddEphemeralBlockDeviceFromSnapshot() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      options.addEphemeralBlockDeviceFromSnapshot("deviceName", "virtualName", "snapshotId");
      assertEquals(options.buildFormParameters().entries(), ImmutableMultimap.of(
               "BlockDeviceMapping.1.DeviceName", "deviceName", "BlockDeviceMapping.1.VirtualName",
               "virtualName", "BlockDeviceMapping.1.Ebs.SnapshotId", "snapshotId").entries());
   }

   @Test
   public void testAddEphemeralBlockDeviceFromSnapshotNullVirtualName() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      options.addEphemeralBlockDeviceFromSnapshot("deviceName", null, "snapshotId");
      assertEquals(options.buildFormParameters().entries(), ImmutableMultimap.of(
               "BlockDeviceMapping.1.DeviceName", "deviceName",
               "BlockDeviceMapping.1.Ebs.SnapshotId", "snapshotId").entries());
   }

   @Test
   public void testNullAddEphemeralBlockDeviceFromSnapshot() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      assertEquals(options.buildFormParameters(), ImmutableMultimap.<String, String> of());
   }

   @Test
   public void testAddEphemeralBlockDeviceFromSnapshotStatic() {
      RegisterImageBackedByEbsOptions options = addEphemeralBlockDeviceFromSnapshot("deviceName",
               "virtualName", "snapshotId");
      assertEquals(options.buildFormParameters().entries(), ImmutableMultimap.of(
               "BlockDeviceMapping.1.DeviceName", "deviceName", "BlockDeviceMapping.1.VirtualName",
               "virtualName", "BlockDeviceMapping.1.Ebs.SnapshotId", "snapshotId").entries());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testAddEphemeralBlockDeviceFromSnapshotNPE() {
      addEphemeralBlockDeviceFromSnapshot(null, null, null);
   }

   // //////
   @Test
   public void testAddNewBlockDevice() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      options.addNewBlockDevice("deviceName", "virtualName", 1);
      assertEquals(options.buildFormParameters().entries(), ImmutableMultimap.of(
               "BlockDeviceMapping.1.Ebs.DeleteOnTermination", "false",
               "BlockDeviceMapping.1.DeviceName", "deviceName", "BlockDeviceMapping.1.VirtualName",
               "virtualName", "BlockDeviceMapping.1.Ebs.VolumeSize", "1").entries());
   }

   @Test
   public void testAddNewBlockDeviceNullVirtualName() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      options.addNewBlockDevice("deviceName", null, 1);
      assertEquals(options.buildFormParameters().entries(), ImmutableMultimap.of(
               "BlockDeviceMapping.1.Ebs.DeleteOnTermination", "false",
               "BlockDeviceMapping.1.DeviceName", "deviceName",
               "BlockDeviceMapping.1.Ebs.VolumeSize", "1").entries());
   }

   @Test
   public void testNullAddNewBlockDevice() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      assertEquals(options.buildFormParameters(), ImmutableMultimap.<String, String> of());
   }

   @Test
   public void testAddNewBlockDeviceStatic() {
      RegisterImageBackedByEbsOptions options = addNewBlockDevice("deviceName", "virtualName", 1);
      assertEquals(options.buildFormParameters().entries(), ImmutableMultimap.of(
               "BlockDeviceMapping.1.Ebs.DeleteOnTermination", "false",
               "BlockDeviceMapping.1.DeviceName", "deviceName", "BlockDeviceMapping.1.VirtualName",
               "virtualName", "BlockDeviceMapping.1.Ebs.VolumeSize", "1").entries());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testAddNewBlockDeviceNPE() {
      addNewBlockDevice(null, null, 1);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testAddNewBlockDeviceTooBig() {
      addNewBlockDevice("deviceName", "virtualName", 1025);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testAddNewBlockDeviceTooSmall() {
      addNewBlockDevice("deviceName", "virtualName", 0);
   }

   @Test
   public void testAddNewEphemeralBlockDevice() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      options.addNewEphemeralBlockDevice("deviceName", "virtualName", 1);
      assertEquals(options.buildFormParameters().entries(), ImmutableMultimap.of(
               "BlockDeviceMapping.1.DeviceName", "deviceName", "BlockDeviceMapping.1.VirtualName",
               "virtualName", "BlockDeviceMapping.1.Ebs.VolumeSize", "1").entries());
   }

   @Test
   public void testAddNewEphemeralBlockDeviceNullVirtualName() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      options.addNewEphemeralBlockDevice("deviceName", null, 1);
      assertEquals(options.buildFormParameters().entries(), ImmutableMultimap.of(
               "BlockDeviceMapping.1.DeviceName", "deviceName",
               "BlockDeviceMapping.1.Ebs.VolumeSize", "1").entries());
   }

   @Test
   public void testNullAddNewEphemeralBlockDevice() {
      RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
      assertEquals(options.buildFormParameters(), ImmutableMultimap.<String, String> of());
   }

   @Test
   public void testAddNewEphemeralBlockDeviceStatic() {
      RegisterImageBackedByEbsOptions options = addNewEphemeralBlockDevice("deviceName",
               "virtualName", 1);
      assertEquals(options.buildFormParameters().entries(), ImmutableMultimap.of(
               "BlockDeviceMapping.1.DeviceName", "deviceName", "BlockDeviceMapping.1.VirtualName",
               "virtualName", "BlockDeviceMapping.1.Ebs.VolumeSize", "1").entries());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testAddNewEphemeralBlockDeviceNPE() {
      addNewEphemeralBlockDevice(null, null, 1);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testAddNewEphemeralBlockDeviceTooBig() {
      addNewEphemeralBlockDevice("deviceName", "virtualName", 1025);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testAddNewEphemeralBlockDeviceTooSmall() {
      addNewEphemeralBlockDevice("deviceName", "virtualName", 0);
   }
}
