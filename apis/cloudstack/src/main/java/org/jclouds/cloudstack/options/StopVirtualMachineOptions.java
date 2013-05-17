/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options for stopping virtual machines.
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/3.0.3/api_3.0.3/root_admin/stopVirtualMachine.html"
 *      />
 * @author Adrian Cole
 * @author Andrew Bayer
 */
public class StopVirtualMachineOptions extends BaseHttpRequestOptions {

   public static final StopVirtualMachineOptions NONE = new StopVirtualMachineOptions();

   /**
    * @param forced
    *           Whether to force stop the virtual machine. Defaults to false.
    */
   public StopVirtualMachineOptions forced(boolean forced) {
      this.queryParameters.replaceValues("forced", ImmutableSet.of(forced + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see StopVirtualMachineOptions#forced
       */
      public static StopVirtualMachineOptions forced(boolean forced) {
         StopVirtualMachineOptions options = new StopVirtualMachineOptions();
         return options.forced(forced);
      }

   }
}
