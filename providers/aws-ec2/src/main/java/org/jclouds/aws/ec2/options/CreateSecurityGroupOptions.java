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
package org.jclouds.aws.ec2.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;

/**
 * Contains options supported in the Form API for the CreateSecurityGroup
 * operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateSecurityGroupOptions
 * object is to statically import CreateSecurityGroupOptions.Builder.* and
 * invoke a static creation method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.options.CreateSecurityGroupOptions.Builder.*
 * <p/>
 * AWSEC2Client connection = // get connection
 * group = connection.getAMIServices().createSecurityGroup(vpcId("123125").noReboot());
 * <code>
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-CreateSecurityGroup.html"
 *      />
 */
public class CreateSecurityGroupOptions extends BaseEC2RequestOptions {

   /**
    * ID of the VPC.
    */
   public CreateSecurityGroupOptions vpcId(String vpcId) {
      formParameters.put("VpcId", checkNotNull(vpcId, "vpcId"));
      return this;
   }

   public static class Builder {

      /**
       * @see CreateSecurityGroupOptions#vpcId(String )
       */
      public static CreateSecurityGroupOptions vpcId(String vpcId) {
         CreateSecurityGroupOptions options = new CreateSecurityGroupOptions();
         return options.vpcId(vpcId);
      }

   }
}
