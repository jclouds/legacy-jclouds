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
public class AddHostOptions extends BaseHttpRequestOptions {

   public static final AddHostOptions NONE = new AddHostOptions();

   /**
    * @param allocationState Allocation state of this Host for allocation of new resources
    */
   public AddHostOptions allocationState(AllocationState allocationState) {
      this.queryParameters.replaceValues("allocationstate", ImmutableSet.of(allocationState.toString()));
      return this;
   }

   /**
    * @param clusterId the cluster ID for the host
    */
   public AddHostOptions clusterId(String clusterId) {
      this.queryParameters.replaceValues("clusterid", ImmutableSet.of(clusterId + ""));
      return this;
   }

   /**
    * @param clusterName the cluster name for the host
    */
   public AddHostOptions clusterName(String clusterName) {
      this.queryParameters.replaceValues("clustername", ImmutableSet.of(clusterName));
      return this;
   }

   /**
    * @param hostTags list of tags to be added to the host
    */
   public AddHostOptions hostTags(Set<String> hostTags) {
      this.queryParameters.replaceValues("hosttags", ImmutableSet.of(Joiner.on(',').join(hostTags)));
      return this;
   }

   /**
    * @param podId the Pod ID for the host
    */
   public AddHostOptions podId(String podId) {
      this.queryParameters.replaceValues("podid", ImmutableSet.of(podId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param allocationState Allocation state of this Host for allocation of new resources
       */
      public static AddHostOptions allocationState(AllocationState allocationState) {
         return new AddHostOptions().allocationState(allocationState);
      }

      /**
       * @param clusterId the cluster ID for the host
       */
      public static AddHostOptions clusterId(String clusterId) {
         return new AddHostOptions().clusterId(clusterId);
      }

      /**
       * @param clusterName the cluster name for the host
       */
      public static AddHostOptions clusterName(String clusterName) {
         return new AddHostOptions().clusterName(clusterName);
      }

      /**
       * @param hostTags list of tags to be added to the host
       */
      public static AddHostOptions hostTags(Set<String> hostTags) {
         return new AddHostOptions().hostTags(hostTags);
      }

      /**
       * @param podId the Pod ID for the host
       */
      public static AddHostOptions podId(String podId) {
         return new AddHostOptions().podId(podId);
      }

   }
}
