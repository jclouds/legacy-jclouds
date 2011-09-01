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

import org.jclouds.ec2.domain.Image.Architecture;
import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;

/**
 * Contains options supported in the Form API for the RegisterImage operation. <h2>
 * Usage</h2> The recommended way to instantiate a RegisterImageOptions object is to statically
 * import RegisterImageOptions.Builder.* and invoke a static creation method followed by an instance
 * mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ec2.options.RegisterImageOptions.Builder.*
 * <p/>
 * EC2Client connection = // get connection
 * String imageId = connection.getImageServices().registerImageFromManifest(...withArchitecture(Architecture.I386).withDescription("description"));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-RegisterImage.html"
 *      />
 */
public class RegisterImageOptions extends BaseEC2RequestOptions {

   /**
    * The architecture of the image.
    */
   public RegisterImageOptions asArchitecture(Architecture architecture) {
      formParameters.put("Architecture", checkNotNull(architecture, "architecture").value());
      return this;
   }

   String getArchitecture() {
      return getFirstFormOrNull("Architecture");
   }

   /**
    *The description of the AMI. "Up to 255 characters."
    */
   public RegisterImageOptions withDescription(String info) {
      formParameters.put("Description", checkNotNull(info, "info"));
      return this;
   }

   String getDescription() {
      return getFirstFormOrNull("Description");
   }

   /**
    * The ID of the kernel to select.
    */
   public RegisterImageOptions withKernelId(String kernelId) {
      formParameters.put("KernelId", checkNotNull(kernelId, "kernelId"));
      return this;
   }

   String getKernelId() {
      return getFirstFormOrNull("KernelId");
   }

   /**
    * The ID of the RAM disk to select. Some kernels require additional drivers at launch. Check the
    * kernel requirements for information on whether you need to specify a RAM disk. To find kernel
    * requirements, refer to the Resource Center and search for the kernel ID.
    */
   public RegisterImageOptions withRamdisk(String ramDiskId) {
      formParameters.put("RamdiskId", checkNotNull(ramDiskId, "ramDiskId"));
      return this;
   }

   String getRamdiskId() {
      return getFirstFormOrNull("RamdiskId");
   }

   public static class Builder {
      /**
       * @see RegisterImageOptions#asArchitecture(Architecture)
       */
      public static RegisterImageOptions asArchitecture(Architecture architecture) {
         RegisterImageOptions options = new RegisterImageOptions();
         return options.asArchitecture(architecture);
      }

      /**
       * @see RegisterImageOptions#withDescription(String)
       */
      public static RegisterImageOptions withDescription(String additionalInfo) {
         RegisterImageOptions options = new RegisterImageOptions();
         return options.withDescription(additionalInfo);
      }

      /**
       * @see RegisterImageOptions#withKernelId(String)
       */
      public static RegisterImageOptions withKernelId(String kernelId) {
         RegisterImageOptions options = new RegisterImageOptions();
         return options.withKernelId(kernelId);
      }

      /**
       * @see RegisterImageOptions#withRamdisk(String)
       */
      public static RegisterImageOptions withRamdisk(String ramdiskId) {
         RegisterImageOptions options = new RegisterImageOptions();
         return options.withRamdisk(ramdiskId);
      }

   }
}
