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
package org.jclouds.cloudstack.parse;

import java.util.Set;

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.Cluster;
import org.jclouds.cloudstack.domain.Host;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListClustersResponseTest extends BaseSetParserTest<Cluster> {

   @Override
   public String resource() {
      return "/listclustersresponse.json";
   }

   @Override
   @SelectJson("cluster")
   public Set<Cluster> expected() {
      Cluster cluster1 = Cluster.builder()
         .id("1")
         .name("Xen Clust 1")
         .podId("1").podName("Dev Pod 1")
         .zoneId("1").zoneName("Dev Zone 1")
         .hypervisor("XenServer")
         .clusterType(Host.ClusterType.CLOUD_MANAGED)
         .allocationState(AllocationState.ENABLED)
         .managedState(Cluster.ManagedState.MANAGED)
         .build();
      Cluster cluster2 = Cluster.builder()
         .id("2")
         .name("Xen Clust 1")
         .podId("2").podName("Dev Pod 2")
         .zoneId("2").zoneName("Dev Zone 2")
         .hypervisor("XenServer")
         .clusterType(Host.ClusterType.CLOUD_MANAGED)
         .allocationState(AllocationState.ENABLED)
         .managedState(Cluster.ManagedState.MANAGED)
         .build();

      return ImmutableSet.of(cluster1, cluster2);
   }

}
