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

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.cloudstack.config.CloudStackParserModule;
import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.Host;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class ListHostsResponseTest extends BaseParserTest<Set<Host>, Set<Host>> {

   public void compare(Set<Host> expects, Set<Host> response) {
      assertEquals(response.toString(), expects.toString());
   }

   @Override
   protected Injector injector() {
      return Guice.createInjector(new GsonModule(), new CloudStackParserModule());
   }

   @Override
   public String resource() {
      return "/listhostsresponse.json";
   }

   @Override
   @SelectJson("host")
   public Set<Host> expected() {
      return ImmutableSet.of(

         Host.builder()
            .id("1")
            .name("cs2-xevsrv.alucloud.local")
            .state(Host.State.UP)
            .type(Host.Type.ROUTING)
            .ipAddress("10.26.26.107")
            .zoneId("1")
            .zoneName("Dev Zone 1")
            .podId("1")
            .podName("Dev Pod 1")
            .version("2.2.12.20110928142833")
            .hypervisor("XenServer")
            .cpuNumber(24)
            .cpuSpeed(2266)
            .cpuAllocated("2.76%")
            .cpuUsed("0.1%")
            .cpuWithOverProvisioning(54384.0f)
            .networkKbsRead(4443L)
            .networkKbsWrite(15048L)
            .memoryTotal(100549733760L)
            .memoryAllocated(3623878656L)
            .memoryUsed(3623878656L)
            .capabilities("xen-3.0-x86_64 , xen-3.0-x86_32p , hvm-3.0-x86_32 , hvm-3.0-x86_32p , hvm-3.0-x86_64")
            .lastPinged(new SimpleDateFormatDateService().iso8601SecondsDateParse("1970-01-16T00:54:43+0200"))
            .managementServerId("223098941760041")
            .clusterId("1")
            .clusterName("Xen Clust 1")
            .clusterType(Host.ClusterType.CLOUD_MANAGED)
            .localStorageActive(false)
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-11-26T23:28:36+0200"))
            .events("PrepareUnmanaged; HypervisorVersionChanged; ManagementServerDown; PingTimeout; " +
               "AgentDisconnected; MaintenanceRequested; HostDown; AgentConnected; StartAgentRebalance; ShutdownRequested; Ping")
            .hasEnoughCapacity(false)
            .allocationState(AllocationState.ENABLED).build(),

         Host.builder()
            .id("2")
            .name("nfs://10.26.26.165/mnt/nfs/cs_sec")
            .state(Host.State.ALERT)
            .disconnected(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-11-26T23:33:38+0200"))
            .type(Host.Type.SECONDARY_STORAGE)
            .ipAddress("nfs")
            .zoneId("1")
            .zoneName("Dev Zone 1")
            .version("2.2.12.20110928142833")
            .hypervisor("None")
            .lastPinged(new SimpleDateFormatDateService().iso8601SecondsDateParse("1970-01-16T00:42:30+0200"))
            .localStorageActive(false)
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-11-26T23:33:38+0200"))
            .events("ManagementServerDown; AgentDisconnected; Remove; MaintenanceRequested; AgentConnected; Ping")
            .hasEnoughCapacity(false)
            .allocationState(AllocationState.ENABLED).build(),

         Host.builder()
            .id("3")
            .name("s-1-VM")
            .state(Host.State.UP)
            .type(Host.Type.SECONDARY_STORAGE_VM)
            .ipAddress("10.26.26.81")
            .zoneId("1")
            .zoneName("Dev Zone 1")
            .podId("1")
            .podName("Dev Pod 1")
            .version("2.2.12.20110928142833")
            .lastPinged(new SimpleDateFormatDateService().iso8601SecondsDateParse("1970-01-16T00:54:43+0200"))
            .managementServerId("223098941760041")
            .localStorageActive(false)
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-11-26T23:35:51+0200"))
            .events("PrepareUnmanaged; HypervisorVersionChanged; ManagementServerDown; PingTimeout; " +
               "AgentDisconnected; MaintenanceRequested; HostDown; AgentConnected; StartAgentRebalance; ShutdownRequested; Ping")
            .hasEnoughCapacity(false)
            .allocationState(AllocationState.ENABLED).build(),

         Host.builder()
            .id("4")
            .name("v-2-VM")
            .state(Host.State.UP)
            .type(Host.Type.CONSOLE_PROXY)
            .ipAddress("10.26.26.96")
            .zoneId("1")
            .zoneName("Dev Zone 1")
            .podId("1")
            .podName("Dev Pod 1")
            .version("2.2.12.20110928142833")
            .lastPinged(new SimpleDateFormatDateService().iso8601SecondsDateParse("1970-01-16T00:54:43+0200"))
            .managementServerId("223098941760041")
            .localStorageActive(false)
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-11-26T23:36:46+0200"))
            .events("PrepareUnmanaged; HypervisorVersionChanged; ManagementServerDown; PingTimeout; " +
               "AgentDisconnected; MaintenanceRequested; HostDown; AgentConnected; StartAgentRebalance; ShutdownRequested; Ping")
            .hasEnoughCapacity(false)
            .allocationState(AllocationState.ENABLED).build()
      );
   }

}
