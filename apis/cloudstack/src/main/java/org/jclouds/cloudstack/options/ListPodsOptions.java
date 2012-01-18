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

/**
 * Options to the GlobalPodClient.listPods API call.
 *
 * @author Richard Downer
 */
public class ListPodsOptions extends BaseHttpRequestOptions {

   public static final ListPodsOptions NONE = new ListPodsOptions();
   
   public static class Builder {

      public static ListPodsOptions allocationState(AllocationState allocationState) {
         return new ListPodsOptions().allocationState(allocationState);
      }

      public static ListPodsOptions id(long id) {
         return new ListPodsOptions().id(id);
      }

      public static ListPodsOptions keyword(String keyword) {
         return new ListPodsOptions().keyword(keyword);
      }

      public static ListPodsOptions name(String name) {
         return new ListPodsOptions().name(name);
      }

      public static ListPodsOptions zoneId(long zoneId) {
         return new ListPodsOptions().zoneId(zoneId);
      }

   }

   public ListPodsOptions allocationState(AllocationState allocationState) {
      this.queryParameters.replaceValues("allocationstate", ImmutableSet.of(allocationState.toString()));
      return this;
   }

   public ListPodsOptions id(long id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   public ListPodsOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   public ListPodsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   public ListPodsOptions zoneId(long zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;
   }

}
