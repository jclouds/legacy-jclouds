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
package org.jclouds.savvis.vpdc.options;


/**
 * Contains options supported for the GetVApp operation. <h2>
 * Usage</h2> The recommended way to instantiate a GetVAppOptions object is to statically import
 * GetVAppOptions.Builder.* and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p/>
 * <code>
 * import static org.jclouds.savvis.vpdc.options.GetVAppOptions.Builder.*
 * <p/>
 * 
 * vApp = context.getApi().getBrowsingClient().getVAppInVDC(orgId, vdcId, vAppId, withPowerState());
 * <code>
 * 
 * @author Adrian Cole
 * @see <a href= "https://api.sandbox.savvis.net/doc/spec/api/getVAppPowerState.html"
 *      />
 */
public class GetVMOptions {
   public static final GetVMOptions NONE = new GetVMOptions();
   private boolean withPowerState;

   /**
    * The VM State is the real time state.
    */
   public GetVMOptions withPowerState() {
      this.withPowerState = true;
      return this;
   }

   public boolean isWithPowerState() {
      return withPowerState;
   }

   public static class Builder {

      /**
       * @see GetVMOptions#withPowerState()
       */
      public static GetVMOptions withPowerState() {
         GetVMOptions options = new GetVMOptions();
         return options.withPowerState();
      }

   }
}
