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
 * Options used to control what cluster information is returned
 *
 * @author Richard Downer
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/listClusters.html"
 *      />
 */
public class ListClustersOptions extends BaseHttpRequestOptions {

   public static final ListHostsOptions NONE = new ListHostsOptions();

   public ListClustersOptions allocationState(AllocationState allocationState) {
      this.queryParameters.replaceValues("allocationstate", ImmutableSet.of(allocationState.toString()));
      return this;
   }

   public ListClustersOptions clusterType(Host.ClusterType clusterType) {
      this.queryParameters.replaceValues("clustertype", ImmutableSet.of(clusterType.toString()));
      return this;
   }

   public ListClustersOptions hypervisor(String hypervisor) {
      this.queryParameters.replaceValues("hypervisor", ImmutableSet.of(hypervisor));
      return this;
   }

   public ListClustersOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   public ListClustersOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   public ListClustersOptions managedState(Cluster.ManagedState managedState) {
      this.queryParameters.replaceValues("managedstate", ImmutableSet.of(managedState.toString()));
      return this;
   }

   public ListClustersOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   public ListClustersOptions podId(String podId) {
      this.queryParameters.replaceValues("podid", ImmutableSet.of(podId + ""));
      return this;
   }

   public ListClustersOptions zoneId(String zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;
   }

   public static class Builder {

      public static ListClustersOptions allocationState(AllocationState allocationState) {
         return new ListClustersOptions().allocationState(allocationState);
      }

      public static ListClustersOptions clusterType(Host.ClusterType clusterType) {
         return new ListClustersOptions().clusterType(clusterType);
      }

      public static ListClustersOptions hypervisor(String hypervisor) {
         return new ListClustersOptions().hypervisor(hypervisor);
      }

      public static ListClustersOptions id(String id) {
         return new ListClustersOptions().id(id);
      }

      public static ListClustersOptions keyword(String keyword) {
         return new ListClustersOptions().keyword(keyword);
      }

      public static ListClustersOptions managedState(Cluster.ManagedState managedState) {
         return new ListClustersOptions().managedState(managedState);
      }

      public static ListClustersOptions name(String name) {
         return new ListClustersOptions().name(name);
      }

      public static ListClustersOptions podId(String podId) {
         return new ListClustersOptions().podId(podId);
      }

      public static ListClustersOptions zoneId(String zoneId) {
         return new ListClustersOptions().zoneId(zoneId);
      }

   }
}
