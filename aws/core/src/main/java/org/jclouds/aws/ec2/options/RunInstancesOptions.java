/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.options.internal.BaseEC2RequestOptions;

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
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-RunInstances.html"
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

   String getKeyName() {
      return getFirstFormOrNull("KeyName");
   }

   /**
    * Name of the security group.
    */
   public RunInstancesOptions withSecurityGroup(String securityGroup) {
      formParameters.put("SecurityGroup", checkNotNull(securityGroup, "securityGroup"));
      return this;
   }

   String getSecurityGroup() {
      return getFirstFormOrNull("SecurityGroup");
   }

   /**
    * Specifies additional information to make available to the instance(s).
    */
   public RunInstancesOptions withAdditionalInfo(String info) {
      formParameters.put("AdditionalInfo", checkNotNull(info, "info"));
      return this;
   }

   String getAdditionalInfo() {
      return getFirstFormOrNull("AdditionalInfo");
   }

   /**
    * MIME, Base64-encoded user data.
    */
   public RunInstancesOptions withUserData(String data) {
      formParameters.put("UserData", checkNotNull(data, "data"));
      return this;
   }

   String getUserData() {
      return getFirstFormOrNull("UserData");
   }

   /**
    * Specifies the instance type. default small;
    */
   public RunInstancesOptions asType(InstanceType type) {
      formParameters.put("InstanceType", checkNotNull(type, "type").toString());
      return this;
   }

   String getType() {
      return getFirstFormOrNull("InstanceType");
   }

   /**
    * The ID of the kernel with which to launch the instance.
    */
   public RunInstancesOptions withKernelId(String kernelId) {
      formParameters.put("KernelId", checkNotNull(kernelId, "kernelId"));
      return this;
   }

   String getKernelId() {
      return getFirstFormOrNull("KernelId");
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

   String getRamdiskId() {
      return getFirstFormOrNull("RamdiskId");
   }

   /**
    * The virtual name.
    */
   public RunInstancesOptions withVirtualName(String virtualName) {
      formParameters
               .put("BlockDeviceMapping.VirtualName", checkNotNull(virtualName, "virtualName"));
      return this;
   }

   String getVirtualName() {
      return getFirstFormOrNull("BlockDeviceMapping.VirtualName");
   }

   /**
    * The device name (e.g., /dev/sdh).
    */
   public RunInstancesOptions withDeviceName(String deviceName) {
      formParameters.put("BlockDeviceMapping.DeviceName", checkNotNull(deviceName, "deviceName"));
      return this;
   }

   String getDeviceName() {
      return getFirstFormOrNull("BlockDeviceMapping.DeviceName");
   }

   /**
    * Enables monitoring for the instance.
    */
   public RunInstancesOptions enableMonitoring() {
      formParameters.put("Monitoring.Enabled", "true");
      return this;
   }

   String getMonitoringEnabled() {
      return getFirstFormOrNull("Monitoring.Enabled");
   }

   /**
    * Specifies the subnet ID within which to launch the instance(s) for Amazon Virtual Private
    * Cloud.
    */
   public RunInstancesOptions withSubnetId(String subnetId) {
      formParameters.put("SubnetId", checkNotNull(subnetId, "subnetId"));
      return this;
   }

   String getSubnetId() {
      return getFirstFormOrNull("SubnetId");
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
       * @see RunInstancesOptions#withAdditionalInfo(String)
       */
      public static RunInstancesOptions withAdditionalInfo(String additionalInfo) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.withAdditionalInfo(additionalInfo);
      }

      /**
       * @see RunInstancesOptions#withUserData(String)
       */
      public static RunInstancesOptions withUserData(String userData) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.withUserData(userData);
      }

      /**
       * @see RunInstancesOptions#asType(InstanceType)
       */
      public static RunInstancesOptions asType(InstanceType instanceType) {
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
       * @see RunInstancesOptions#withDeviceName(String)
       */
      public static RunInstancesOptions withDeviceName(String deviceName) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.withDeviceName(deviceName);
      }

      /**
       * @see RunInstancesOptions#enableMonitoring()
       */
      public static RunInstancesOptions enableMonitoring() {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.enableMonitoring();
      }

      /**
       * @see RunInstancesOptions#withSubnetId(String)
       */
      public static RunInstancesOptions withSubnetId(String subnetId) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.withSubnetId(subnetId);
      }

      /**
       * @see RunInstancesOptions#withRamdisk(String)
       */
      public static RunInstancesOptions withRamdisk(String ramdiskId) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.withRamdisk(ramdiskId);
      }

      /**
       * @see RunInstancesOptions#withVirtualName(String)
       */
      public static RunInstancesOptions withVirtualName(String virtualName) {
         RunInstancesOptions options = new RunInstancesOptions();
         return options.withVirtualName(virtualName);
      }

   }
}
