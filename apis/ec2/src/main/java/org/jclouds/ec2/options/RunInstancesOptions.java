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

import java.util.Set;

import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;
import org.jclouds.encryption.internal.Base64;

/**
 * Contains options supported in the Form API for the RunInstances operation. <h2>
 * Usage</h2> The recommended way to instantiate a RunInstancesOptions object is to statically
 * import RunInstancesOptions.Builder.* and invoke a static creation method followed by an instance
 * mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.*
 * <p/>
 * EC2Client connection = // get connection
 * Future<ReservationInfo> instances = connection.runInstances(executableBy("123125").imageIds(1000, 1004));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-RunInstances.html"
 *      />
 */
public class RunInstancesOptions extends BaseEC2RequestOptions {
   public static final RunInstancesOptions NONE = new RunInstancesOptions();

   /**
    * The name of the key pair.
    */
   public RunInstancesOptions withKeyName(String keyName) {
      formParameters.put("KeyName", checkNotNull(keyName, "keyName"));
      return this;
   }

   /**
    * Attach multiple security groups
    */
   public RunInstancesOptions withSecurityGroups(String... securityGroups) {
      indexFormValuesWithPrefix("SecurityGroup", securityGroups);
      return this;
   }

   /**
    * Attach multiple security groups
    */
   public RunInstancesOptions withSecurityGroups(Iterable<String> securityGroups) {
      indexFormValuesWithPrefix("SecurityGroup", securityGroups);
      return this;
   }

   /**
    * Attaches a single security group. Multiple calls to this method won't add more groups.
    * 
    * @param securityGroup
    *           name of an existing security group
    */
   public RunInstancesOptions withSecurityGroup(String securityGroup) {
      return withSecurityGroups(securityGroup);
   }

   /**
    * Unencoded data
    */
   public RunInstancesOptions withUserData(byte[] unencodedData) {
      int length = checkNotNull(unencodedData, "unencodedData").length;
      checkArgument(length > 0, "userData cannot be empty");
      checkArgument(length <= 16 * 1024, "userData cannot be larger than 16kb");
      formParameters.put("UserData", Base64.encodeBytes(unencodedData));
      return this;
   }

   /**
    * Specifies the instance type. default small;
    */
   public RunInstancesOptions asType(String type) {
      formParameters.put("InstanceType", checkNotNull(type, "type"));
      return this;
   }

   /**
    * The ID of the kernel with which to launch the instance.
    */
   public RunInstancesOptions withKernelId(String kernelId) {
      formParameters.put("KernelId", checkNotNull(kernelId, "kernelId"));
      return this;
   }

   /**
    * The ID of the RAM disk with which to launch the instance. Some kernels require additional
    * drivers at l aunch. Check the kernel requirements for information on whether you need to
    * specify a RAM disk. To find kernel requirements, go to th e Resource Center and search for the
    * kernel ID.
    */
   public RunInstancesOptions withRamdisk(String ramDiskId) {
      formParameters.put("RamdiskId", checkNotNull(ramDiskId, "ramDiskId"));
      return this;
   }

   /**
    * Specifies the Block Device Mapping for the instance
    * 
    */
   public RunInstancesOptions withBlockDeviceMappings(Set<? extends BlockDeviceMapping> mappings) {
      int i = 1;
      for (BlockDeviceMapping mapping : checkNotNull(mappings, "mappings")) {
         checkNotNull(mapping.getDeviceName(), "deviceName");
         formParameters.put(String.format("BlockDeviceMapping.%d.DeviceName", i), mapping.getDeviceName());
         if (mapping.getVirtualName() != null)
            formParameters.put(String.format("BlockDeviceMapping.%d.VirtualName", i), mapping.getVirtualName());
         if (mapping.getEbsSnapshotId() != null)
            formParameters.put(String.format("BlockDeviceMapping.%d.Ebs.SnapshotId", i), mapping.getEbsSnapshotId());
         if (mapping.getEbsVolumeSize() != null)
            formParameters.put(String.format("BlockDeviceMapping.%d.Ebs.VolumeSize", i),
                  String.valueOf(mapping.getEbsVolumeSize()));
         if (mapping.getEbsNoDevice() != null)
            formParameters.put(String.format("BlockDeviceMapping.%d.Ebs.NoDevice", i),
                  String.valueOf(mapping.getEbsNoDevice()));
         if (mapping.getEbsDeleteOnTermination() != null)
            formParameters.put(String.format("BlockDeviceMapping.%d.Ebs.DeleteOnTermination", i),
                  String.valueOf(mapping.getEbsDeleteOnTermination()));
         i++;
      }
      return this;
   }

   public static class Builder {
      /**
       * @see RunInstancesOptions#withKeyName(String)
       */
      public static RunInstancesOptions withKeyName(String keyName) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.withKeyName(keyName);
      }

      /**
       * @see RunInstancesOptions#withSecurityGroup(String)
       */
      public static RunInstancesOptions withSecurityGroup(String securityGroup) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.withSecurityGroup(securityGroup);
      }

      /**
       * @see RunInstancesOptions#withUserData(byte [])
       */
      public static RunInstancesOptions withUserData(byte[] unencodedData) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.withUserData(unencodedData);
      }

      /**
       * @see RunInstancesOptions#asType(InstanceType)
       */
      public static RunInstancesOptions asType(String instanceType) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.asType(instanceType);
      }

      /**
       * @see RunInstancesOptions#withKernelId(String)
       */
      public static RunInstancesOptions withKernelId(String kernelId) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.withKernelId(kernelId);
      }

      /**
       * @see RunInstancesOptions#withRamdisk(String)
       */
      public static RunInstancesOptions withRamdisk(String ramdiskId) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.withRamdisk(ramdiskId);
      }

      /**
       * @see RunInstancesOptions#withBlockDeviceMappings(Set<BlockDeviceMapping> mappings)
       */
      public static RunInstancesOptions withBlockDeviceMappings(Set<? extends BlockDeviceMapping> mappings) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.withBlockDeviceMappings(mappings);
      }

   }
}
