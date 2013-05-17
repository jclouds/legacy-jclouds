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

import java.util.Set;

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * Options to the GlobalHostClient.addHost() API call
 *
 * @author Richard Downer
 */
public class UpdateHostOptions extends BaseHttpRequestOptions {


   public static final UpdateHostOptions NONE = new UpdateHostOptions();

   /**
    * @param allocationState Allocation state of this Host for allocation of new resources
    */
   public UpdateHostOptions allocationState(AllocationState allocationState) {
      this.queryParameters.replaceValues("allocationstate", ImmutableSet.of(allocationState.toString()));
      return this;
   }

   /**
    * @param hostTags list of tags to be added to the host
    */
   public UpdateHostOptions hostTags(Set<String> hostTags) {
      this.queryParameters.replaceValues("hosttags", ImmutableSet.of(Joiner.on(',').join(hostTags)));
      return this;
   }

   /**
    * @param osCategoryId the id of Os category to update the host with
    */
   public UpdateHostOptions osCategoryId(String osCategoryId) {
      this.queryParameters.replaceValues("oscategoryid", ImmutableSet.of(osCategoryId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param allocationState Allocation state of this Host for allocation of new resources
       */
      public static UpdateHostOptions allocationState(AllocationState allocationState) {
         return new UpdateHostOptions().allocationState(allocationState);
      }

      /**
       * @param hostTags list of tags to be added to the host
       */
      public static UpdateHostOptions hostTags(Set<String> hostTags) {
         return new UpdateHostOptions().hostTags(hostTags);
      }

      /**
       * @param podId the Pod ID for the host
       */
      public static UpdateHostOptions osCategoryId(String osCategoryId) {
         return new UpdateHostOptions().osCategoryId(osCategoryId);
      }

   }
}
