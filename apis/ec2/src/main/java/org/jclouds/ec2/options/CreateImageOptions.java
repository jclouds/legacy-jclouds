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
 * Contains options supported in the Form API for the CreateImage operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateImageOptions object is to statically import
 * CreateImageOptions.Builder.* and invoke a static creation method followed by an instance mutator
 * (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ec2.options.CreateImageOptions.Builder.*
 * <p/>
 * EC2Client connection = // get connection
 * Future<Set<ImageMetadata>> images = connection.getAMIServices().createImage(withDescription("123125").noReboot());
 * <code>
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-CreateImage.html"
 *      />
 */
public class CreateImageOptions extends BaseEC2RequestOptions {
   public static final CreateImageOptions NONE = new CreateImageOptions();

   /**
    * The description of the AMI that was provided during image creation.
    * <p/>
    * 
    * Up to 255 characters
    */
   public CreateImageOptions withDescription(String description) {
      formParameters.put("Description", checkNotNull(description, "description"));
      return this;
   }

   public String getDescription() {
      return getFirstFormOrNull("Description");

   }

   /**
    * By default this property is set to false, which means Amazon EC2 attempts to cleanly shut down
    * the instance before image creation and reboots the instance afterwards. When set to true,
    * Amazon EC2 does not shut down the instance before creating the image. When this option is
    * used, file system integrity on the created image cannot be guaranteed.
    */
   public CreateImageOptions noReboot() {
      formParameters.put("NoReboot", "true");
      return this;
   }

   public boolean getNoReboot() {
      return getFirstFormOrNull("NoReboot") != null;
   }

   public static class Builder {

      /**
       * @see CreateImageOptions#withDescription(String )
       */
      public static CreateImageOptions withDescription(String description) {
         CreateImageOptions options = new CreateImageOptions();
         return options.withDescription(description);
      }

      /**
       * @see CreateImageOptions#noReboot()
       */
      public static CreateImageOptions noReboot() {
         CreateImageOptions options = new CreateImageOptions();
         return options.noReboot();
      }

   }
}
