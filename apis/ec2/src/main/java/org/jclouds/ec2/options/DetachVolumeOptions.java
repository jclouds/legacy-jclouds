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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;

/**
 * Contains options supported in the Form API for the DetachVolume operation. <h2>
 * Usage</h2> The recommended way to instantiate a DetachVolumeOptions object is to statically
 * import DetachVolumeOptions.Builder.* and invoke a static creation method followed by an instance
 * mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ec2.options.DetachVolumeOptions.Builder.*
 * <p/>
 * EC2Client client = // get connection
 * client.getElasticBlockStoreServices().detachVolumeInRegion(null, id, fromDevice("123125").force());
 * <code>
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-DetachVolume.html"
 *      />
 */
public class DetachVolumeOptions extends BaseEC2RequestOptions {
   /**
    * The ID of the instance.
    */
   public DetachVolumeOptions fromInstance(String instanceId) {
      formParameters.put("InstanceId", checkNotNull(instanceId, "instanceId"));
      return this;
   }

   public String getInstance() {
      return getFirstFormOrNull("InstanceId");

   }

   /**
    * The device name.
    */
   public DetachVolumeOptions fromDevice(String device) {
      formParameters.put("Device", checkNotNull(device, "device"));
      return this;
   }

   public String getDevice() {
      return getFirstFormOrNull("Device");

   }

   public static class Builder {
      /**
       * @see DetachVolumeOptions#fromInstance(String )
       */
      public static DetachVolumeOptions fromInstance(String instance) {
         DetachVolumeOptions options = new DetachVolumeOptions();
         return options.fromInstance(instance);
      }

      /**
       * @see DetachVolumeOptions#fromDevice(String )
       */
      public static DetachVolumeOptions fromDevice(String device) {
         DetachVolumeOptions options = new DetachVolumeOptions();
         return options.fromDevice(device);
      }

   }
}
