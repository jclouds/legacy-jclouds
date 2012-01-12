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
package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableSet;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options to the GlobalHostClient.deleteHost() API call
 *
 * @author Richard Downer
 */
public class DeleteHostOptions extends BaseHttpRequestOptions {

   public static final DeleteHostOptions NONE = new DeleteHostOptions();

   /**
    * @param forced Force delete the host. All HA enabled vms running on the host will be put to HA; HA disabled ones will be stopped
    */
   public DeleteHostOptions forced(boolean forced) {
      this.queryParameters.replaceValues("forced", ImmutableSet.of(forced + ""));
      return this;
   }

   /**
    * @param forceDestroyLocalStorage Force destroy local storage on this host. All VMs created on this local storage will be destroyed
    */
   public DeleteHostOptions forceDestroyLocalStorage(boolean forceDestroyLocalStorage) {
      this.queryParameters.replaceValues("forcedestroylocalstorage", ImmutableSet.of(forceDestroyLocalStorage + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param forced Force delete the host. All HA enabled vms running on the host will be put to HA; HA disabled ones will be stopped
       */
      public static DeleteHostOptions forced(boolean forced) {
         return new DeleteHostOptions().forced(forced);
      }

      /**
       * @param forceDestroyLocalStorage Force destroy local storage on this host. All VMs created on this local storage will be destroyed
       */
      public static DeleteHostOptions forceDestroyLocalStorage(boolean forceDestroyLocalStorage) {
         return new DeleteHostOptions().forceDestroyLocalStorage(forceDestroyLocalStorage);
      }

   }
}
