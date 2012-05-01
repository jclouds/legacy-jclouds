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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.ec2.domain.Image.Architecture;
import org.jclouds.javax.annotation.Nullable;

/**
 * Contains options supported in the Form API for the RegisterImage operation. <h2>
 * Usage</h2> The recommended way to instantiate a RegisterImageBackedByEbsOptions object is to statically
 * import RegisterImageBackedByEbsOptions.Builder.* and invoke a static creation method followed by an instance
 * mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.*
 * <p/>
 * EC2Client connection = // get connection
 * String imageId = connection.getImageServices().registerImageBackedByEbs(...addEphemeralBlockDeviceFromSnapshot("/dev/sda2","virtual-1","snapshot-id"));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-RegisterImage.html"
 *      />
 */
public class RegisterImageBackedByEbsOptions extends RegisterImageOptions {

   private int deviceIndex = 1;

   /**
    * 
    * adds a block device to the image from an ebs snapshot. This device is deleted on instance
    * termination.
    * 
    * @param name
    *           The device name (e.g., /dev/sdh).
    * @param virtualName
    *           The virtual device name. (nullable)
    * @param snapshotId
    *           The ID of the snapshot.
    */
   public RegisterImageBackedByEbsOptions addEphemeralBlockDeviceFromSnapshot(String deviceName,
            @Nullable String virtualName, String snapshotId) {
      formParameters.put("BlockDeviceMapping." + deviceIndex + ".DeviceName", checkNotNull(
               deviceName, "deviceName"));
      if (virtualName != null)
         formParameters.put("BlockDeviceMapping." + deviceIndex + ".VirtualName", checkNotNull(
                  virtualName, "virtualName"));
      formParameters.put("BlockDeviceMapping." + deviceIndex + ".Ebs.SnapshotId", checkNotNull(
               snapshotId, "snapshotId"));
      deviceIndex++;
      return this;
   }

   /**
    * 
    * adds a new block device to the image. This device is deleted on instance termination.
    * 
    * @param name
    *           The device name (e.g., /dev/sdh).
    * @param virtualName
    *           The virtual device name.
    * @param volumeSize
    *           The size of the volume, in GiBs.
    */
   public RegisterImageBackedByEbsOptions addNewEphemeralBlockDevice(String deviceName,
            @Nullable String virtualName, int volumeSize) {
      checkArgument(volumeSize > 0 && volumeSize < 1025, "volumeSize must be between 1 and 1024 gb");
      formParameters.put("BlockDeviceMapping." + deviceIndex + ".DeviceName", checkNotNull(
               deviceName, "deviceName"));
      if (virtualName != null)
         formParameters.put("BlockDeviceMapping." + deviceIndex + ".VirtualName", checkNotNull(
                  virtualName, "virtualName"));
      formParameters.put("BlockDeviceMapping." + deviceIndex + ".Ebs.VolumeSize", volumeSize + "");
      deviceIndex++;
      return this;
   }

   /**
    * 
    * adds a block device to the image from an ebs snapshot. This device is retained on instance
    * termination.
    * 
    * @param name
    *           The device name (e.g., /dev/sdh).
    * @param virtualName
    *           The virtual device name. (nullable)
    * @param snapshotId
    *           The ID of the snapshot.
    */
   public RegisterImageBackedByEbsOptions addBlockDeviceFromSnapshot(String deviceName,
            @Nullable String virtualName, String snapshotId) {
      formParameters.put("BlockDeviceMapping." + deviceIndex + ".Ebs.DeleteOnTermination", "false");
      addEphemeralBlockDeviceFromSnapshot(deviceName, virtualName, snapshotId);
      return this;
   }

   /**
    * 
    * adds a new block device to the image. This device is retained on instance termination.
    * 
    * @param name
    *           The device name (e.g., /dev/sdh).
    * @param virtualName
    *           The virtual device name.
    * @param volumeSize
    *           The size of the volume, in GiBs..
    */
   public RegisterImageBackedByEbsOptions addNewBlockDevice(String deviceName,
            @Nullable String virtualName, int volumeSize) {
      formParameters.put("BlockDeviceMapping." + deviceIndex + ".Ebs.DeleteOnTermination", "false");
      addNewEphemeralBlockDevice(deviceName, virtualName, volumeSize);
      return this;
   }

   public static class Builder {
      /**
       * @see RegisterImageBackedByEbsOptions#asArchitecture(Architecture)
       */
      public static RegisterImageBackedByEbsOptions asArchitecture(Architecture architecture) {
         RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
         return options.asArchitecture(architecture);
      }

      /**
       * @see RegisterImageBackedByEbsOptions#withDescription(String)
       */
      public static RegisterImageBackedByEbsOptions withDescription(String additionalInfo) {
         RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
         return options.withDescription(additionalInfo);
      }

      /**
       * @see RegisterImageBackedByEbsOptions#withKernelId(String)
       */
      public static RegisterImageBackedByEbsOptions withKernelId(String kernelId) {
         RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
         return options.withKernelId(kernelId);
      }

      /**
       * @see RegisterImageBackedByEbsOptions#withRamdisk(String)
       */
      public static RegisterImageBackedByEbsOptions withRamdisk(String ramdiskId) {
         RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
         return options.withRamdisk(ramdiskId);
      }

      /**
       * @see RegisterImageBackedByEbsOptions#addBlockDeviceFromSnapshot(String, String, String)
       */
      public static RegisterImageBackedByEbsOptions addBlockDeviceFromSnapshot(String deviceName,
               @Nullable String virtualName, String snapshotId) {
         RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
         return options.addBlockDeviceFromSnapshot(deviceName, virtualName, snapshotId);
      }

      /**
       * @see RegisterImageBackedByEbsOptions#addEphemeralBlockDeviceFromSnapshot(String, String,
       *      String)
       */
      public static RegisterImageBackedByEbsOptions addEphemeralBlockDeviceFromSnapshot(
               String deviceName, @Nullable String virtualName, String snapshotId) {
         RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
         return options.addEphemeralBlockDeviceFromSnapshot(deviceName, virtualName, snapshotId);
      }

      /**
       * @see RegisterImageBackedByEbsOptions#addNewBlockDevice(String, String, int)
       */
      public static RegisterImageBackedByEbsOptions addNewBlockDevice(String deviceName,
               @Nullable String virtualName, int volumeSize) {
         RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
         return options.addNewBlockDevice(deviceName, virtualName, volumeSize);
      }

      /**
       * @see RegisterImageBackedByEbsOptions#addNewEphemeralBlockDevice(String, String, int)
       */
      public static RegisterImageBackedByEbsOptions addNewEphemeralBlockDevice(String deviceName,
               @Nullable String virtualName, int volumeSize) {
         RegisterImageBackedByEbsOptions options = new RegisterImageBackedByEbsOptions();
         return options.addNewEphemeralBlockDevice(deviceName, virtualName, volumeSize);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public RegisterImageBackedByEbsOptions asArchitecture(Architecture architecture) {
      return (RegisterImageBackedByEbsOptions) super.asArchitecture(architecture);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public RegisterImageBackedByEbsOptions withDescription(String info) {
      return (RegisterImageBackedByEbsOptions) super.withDescription(info);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public RegisterImageBackedByEbsOptions withKernelId(String kernelId) {
      return (RegisterImageBackedByEbsOptions) super.withKernelId(kernelId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public RegisterImageBackedByEbsOptions withRamdisk(String ramDiskId) {
      return (RegisterImageBackedByEbsOptions) super.withRamdisk(ramDiskId);
   }
}
