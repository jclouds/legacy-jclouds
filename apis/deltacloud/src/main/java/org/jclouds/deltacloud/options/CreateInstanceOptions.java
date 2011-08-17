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
package org.jclouds.deltacloud.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains options supported in the Deltacloud API for the Create Instance operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateInstanceOptions object is to statically
 * import CreateInstanceOptions.Builder.* and invoke a static creation method followed by an
 * instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.deltacloud.options.CreateInstanceOptions.Builder.*
 * <p/>
 * DeltacloudClient connection = // get connection
 * ListenableFuture<Instance> instance = client.createInstance(collection, "imageId", named("robot"));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a href="http://deltacloud.org/api.html#h1" />
 */
public class CreateInstanceOptions extends BaseHttpRequestOptions {
   public static final CreateInstanceOptions NONE = new CreateInstanceOptions();

   /**
    * A short label to identify the instance.
    * 
    */
   public CreateInstanceOptions named(String name) {
      formParameters.put("name", checkNotNull(name, "name"));
      return this;
   }

   /**
    * The realm in which to launch the instance
    * 
    */
   public CreateInstanceOptions realm(String realmId) {
      formParameters.put("realm_id", checkNotNull(realmId, "realmId"));
      return this;
   }

   /**
    * The hardware profile upon which to launch the instance
    */
   public CreateInstanceOptions hardwareProfile(String hwpName) {
      formParameters.put("hwp_name", checkNotNull(hwpName, "hwpName"));
      return this;
   }

   public String getName() {
      return this.getFirstFormOrNull("name");
   }

   public static class Builder {

      /**
       * @see CreateInstanceOptions#named
       */
      public static CreateInstanceOptions named(String name) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.named(name);
      }

      /**
       * @see CreateInstanceOptions#realm
       */
      public static CreateInstanceOptions realm(String realmId) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.realm(realmId);
      }

      /**
       * @see CreateInstanceOptions#hardwareProfile
       */
      public static CreateInstanceOptions hardwareProfile(String hwpName) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.hardwareProfile(hwpName);
      }

   }
}
