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

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.Cluster;
import org.jclouds.cloudstack.domain.Host;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options for the GlobalHostClient.updateCluster() API call.
 *
 * @author Richard Downer
 */
public class UpdateClusterOptions extends BaseHttpRequestOptions {

   public static final UpdateClusterOptions NONE = new UpdateClusterOptions();

   /**
    * @param allocationState Allocation state of this cluster for allocation of new resources
    */
   public UpdateClusterOptions allocationState(AllocationState allocationState) {
      this.queryParameters.replaceValues("allocationstate", ImmutableSet.<String>of(allocationState.toString()));
      return this;
   }

   /**
    * @param clusterName the cluster name
    */
   public UpdateClusterOptions clusterName(String clusterName) {
      this.queryParameters.replaceValues("clustername", ImmutableSet.<String>of(clusterName));
      return this;
   }

   /**
    * @param clusterType type of the cluster
    */
   public UpdateClusterOptions clusterType(Host.ClusterType clusterType) {
      this.queryParameters.replaceValues("clustertype", ImmutableSet.<String>of(clusterType.toString()));
      return this;
   }

   /**
    * @param hypervisor hypervisor type of the cluster
    */
   public UpdateClusterOptions hypervisor(String hypervisor) {
      this.queryParameters.replaceValues("hypervisor", ImmutableSet.<String>of(hypervisor));
      return this;
   }

   /**
    * @param managedState whether this cluster is managed by cloudstack
    */
   public UpdateClusterOptions managedState(Cluster.ManagedState managedState) {
      this.queryParameters.replaceValues("managedstate", ImmutableSet.<String>of(managedState.toString()));
      return this;
   }

   public static class Builder {

      /**
       * @param allocationState Allocation state of this cluster for allocation of new resources
       */
      public static UpdateClusterOptions allocationState(AllocationState allocationState) {
         return new UpdateClusterOptions().allocationState(allocationState);
      }

      /**
       * @param clusterName the cluster name
       */
      public static UpdateClusterOptions clusterName(String clusterName) {
         return new UpdateClusterOptions().clusterName(clusterName);
      }

      /**
       * @param clusterType type of the cluster
       */
      public static UpdateClusterOptions clusterType(Host.ClusterType clusterType) {
         return new UpdateClusterOptions().clusterType(clusterType);
      }

      /**
       * @param hypervisor hypervisor type of the cluster
       */
      public static UpdateClusterOptions hypervisor(String hypervisor) {
         return new UpdateClusterOptions().hypervisor(hypervisor);
      }

      /**
       * @param managedState whether this cluster is managed by cloudstack
       */
      public static UpdateClusterOptions managedState(Cluster.ManagedState managedState) {
         return new UpdateClusterOptions().managedState(managedState);
      }

   }
}
