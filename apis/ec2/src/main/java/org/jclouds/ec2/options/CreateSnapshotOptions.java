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
 * Contains options supported in the Form API for the CreateSnapshot operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateSnapshotOptions object is to statically
 * import CreateSnapshotOptions.Builder.* and invoke a static creation method followed by an
 * instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ec2.options.CreateSnapshotOptions.Builder.*
 * <p/>
 * EC2Client connection = // get connection
 * Snapshot snapshot = connection.getElasticBlockStoreServices().createSnapshotInRegion(volumeId, withDescription("123125"));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-CreateSnapshot.html"
 *      />
 */
public class CreateSnapshotOptions extends BaseEC2RequestOptions {

   /**
    * Description of the Amazon EBS snapshot.
    * <p/>
    * 
    * Up to 255 characters
    */
   public CreateSnapshotOptions withDescription(String description) {
      formParameters.put("Description", checkNotNull(description, "description"));
      return this;
   }

   public String getDescription() {
      return getFirstFormOrNull("Description");

   }

   public static class Builder {

      /**
       * @see CreateSnapshotOptions#withDescription(String )
       */
      public static CreateSnapshotOptions withDescription(String identityId) {
         CreateSnapshotOptions options = new CreateSnapshotOptions();
         return options.withDescription(identityId);
      }

   }
}
