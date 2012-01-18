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
import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.http.options.BaseHttpRequestOptions;

import javax.annotation.concurrent.Immutable;

/**
 * Options to the GlobalPodClient.updatePod API call.
 *
 * @author Richard Downer
 */
public class UpdatePodOptions extends BaseHttpRequestOptions {

   public static final UpdatePodOptions NONE = new UpdatePodOptions();
   
   public static class Builder {

      public static UpdatePodOptions name(String name) {
         return new UpdatePodOptions().name(name);
      }

      public static UpdatePodOptions startIp(String startIp) {
         return new UpdatePodOptions().startIp(startIp);
      }

      public static UpdatePodOptions endIp(String endIp) {
         return new UpdatePodOptions().endIp(endIp);
      }

      public static UpdatePodOptions gateway(String gateway) {
         return new UpdatePodOptions().gateway(gateway);
      }

      public static UpdatePodOptions netmask(String netmask) {
         return new UpdatePodOptions().netmask(netmask);
      }

      public static UpdatePodOptions allocationState(AllocationState allocationState) {
         return new UpdatePodOptions().allocationState(allocationState);
      }

   }

   public UpdatePodOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.<String>of(name));
      return this;
   }

   public UpdatePodOptions startIp(String startIp) {
      this.queryParameters.replaceValues("startip", ImmutableSet.<String>of(startIp));
      return this;
   }

   public UpdatePodOptions endIp(String endIp) {
      this.queryParameters.replaceValues("endip", ImmutableSet.<String>of(endIp));
      return this;
   }

   public UpdatePodOptions gateway(String gateway) {
      this.queryParameters.replaceValues("gateway", ImmutableSet.<String>of(gateway));
      return this;
   }

   public UpdatePodOptions netmask(String netmask) {
      this.queryParameters.replaceValues("netmask", ImmutableSet.<String>of(netmask));
      return this;
   }

   public UpdatePodOptions allocationState(AllocationState allocationState) {
      this.queryParameters.replaceValues("allocationstate", ImmutableSet.of(allocationState.toString()));
      return this;
   }

}
