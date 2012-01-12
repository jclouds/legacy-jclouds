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
package org.jclouds.cloudstack.features;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.Cluster;
import org.jclouds.cloudstack.domain.ConfigurationEntry;
import org.jclouds.cloudstack.domain.Host;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Test the CloudStack GlobalHostClient
 *
 * @author Richard Downer
 */
@Test(groups = "unit", testName = "GlobalConfigurationClientExpectTest")
public class GlobalHostClientExpectTest extends BaseCloudStackRestClientExpectTest<GlobalHostClient> {

   @Test
   public void testListHostsWhenResponseIs2xx() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint(URI.create("http://localhost:8080/client/api?response=json&command=listHosts&apiKey=identity&signature=wsv4UBgXxURW0pNlso4MT9E052s%3D"))
         .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json").build())
         .build();
      HttpResponse response = HttpResponse.builder()
         .payload(payloadFromResource("/listhostsresponse.json"))
         .statusCode(200).build();

      Set<Host> actual = requestSendsResponse(request, response).listHosts();

      Date lastPinged = makeDate(1970, Calendar.JANUARY, 16, 0, 54, 43, "UTC");
      Date created = makeDate(2011, Calendar.NOVEMBER, 26, 23, 28, 36, "UTC");
      Host host1 = Host.builder().id(1).name("cs2-xevsrv.alucloud.local").state(Host.State.UP).type(Host.Type.ROUTING).ipAddress("10.26.26.107").zoneId(1).zoneName("Dev Zone 1").podId(1).podName("Dev Pod 1").version("2.2.12.20110928142833").hypervisor("XenServer").cpuNumber(24).cpuSpeed(2266).cpuAllocated("2.76%").cpuUsed("0.1%").cpuWithOverProvisioning(54384.0F).networkKbsRead(4443).networkKbsWrite(15048).memoryTotal(100549733760L).memoryAllocated(3623878656L).memoryUsed(3623878656L).capabilities("xen-3.0-x86_64 , xen-3.0-x86_32p , hvm-3.0-x86_32 , hvm-3.0-x86_32p , hvm-3.0-x86_64").lastPinged(lastPinged).managementServerId(223098941760041L).clusterId(1).clusterName("Xen Clust 1").clusterType(Host.ClusterType.CLOUD_MANAGED).localStorageActive(false).created(created).events("PrepareUnmanaged; HypervisorVersionChanged; ManagementServerDown; PingTimeout; AgentDisconnected; MaintenanceRequested; HostDown; AgentConnected; StartAgentRebalance; ShutdownRequested; Ping").hostTags("").hasEnoughCapacity(false).allocationState(Host.AllocationState.ENABLED).build();

      Date disconnected = makeDate(2011, Calendar.NOVEMBER, 26, 23, 33, 38, "UTC");
      lastPinged = makeDate(1970, Calendar.JANUARY, 16, 0, 42, 30, "UTC");
      created = makeDate(2011, Calendar.NOVEMBER, 26, 23, 33, 38, "UTC");
      Host host2 = Host.builder().id(2).name("nfs://10.26.26.165/mnt/nfs/cs_sec").state(Host.State.ALERT).disconnected(disconnected).type(Host.Type.SECONDARY_STORAGE).ipAddress("nfs").zoneId(1).zoneName("Dev Zone 1").version("2.2.12.20110928142833").hypervisor("None").lastPinged(lastPinged).localStorageActive(false).created(created).events("ManagementServerDown; AgentDisconnected; Remove; MaintenanceRequested; AgentConnected; Ping").hasEnoughCapacity(false).allocationState(Host.AllocationState.ENABLED).build();

      lastPinged = makeDate(1970, Calendar.JANUARY, 16, 0, 54, 43, "UTC");
      created = makeDate(2011, Calendar.NOVEMBER, 26, 23, 35, 51, "UTC");
      Host host3 = Host.builder().id(3).name("s-1-VM").state(Host.State.UP).type(Host.Type.SECONDARY_STORAGE_VM).ipAddress("10.26.26.81").zoneId(1).zoneName("Dev Zone 1").podId(1).podName("Dev Pod 1").version("2.2.12.20110928142833").lastPinged(lastPinged).managementServerId(223098941760041L).localStorageActive(false).created(created).events("PrepareUnmanaged; HypervisorVersionChanged; ManagementServerDown; PingTimeout; AgentDisconnected; MaintenanceRequested; HostDown; AgentConnected; StartAgentRebalance; ShutdownRequested; Ping").hasEnoughCapacity(false).allocationState(Host.AllocationState.ENABLED).build();

      lastPinged = makeDate(1970, Calendar.JANUARY, 16, 0, 54, 43, "UTC");
      created = makeDate(2011, Calendar.NOVEMBER, 26, 23, 36, 46, "UTC");
      Host host4 = Host.builder().id(4).name("v-2-VM").state(Host.State.UP).type(Host.Type.CONSOLE_PROXY).ipAddress("10.26.26.96").zoneId(1).zoneName("Dev Zone 1").podId(1).podName("Dev Pod 1").version("2.2.12.20110928142833").lastPinged(lastPinged).managementServerId(223098941760041L).localStorageActive(false).created(created).events("PrepareUnmanaged; HypervisorVersionChanged; ManagementServerDown; PingTimeout; AgentDisconnected; MaintenanceRequested; HostDown; AgentConnected; StartAgentRebalance; ShutdownRequested; Ping").hasEnoughCapacity(false).allocationState(Host.AllocationState.ENABLED).build();

      Set<Host> expected = ImmutableSet.of(host1, host2, host3, host4);

      assertEquals(actual, expected);
   }

   @Test
   public void testListHostsEmptyOn404() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint(URI.create("http://localhost:8080/client/api?response=json&command=listHosts&apiKey=identity&signature=wsv4UBgXxURW0pNlso4MT9E052s%3D"))
         .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json").build())
         .build();
      HttpResponse response = HttpResponse.builder().statusCode(404).build();
      GlobalHostClient client = requestSendsResponse(request, response);

      assertEquals(client.listHosts(), ImmutableSet.of());
   }

   @Test
   public void testListClustersWhenResponseIs2xx() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint(URI.create("http://localhost:8080/client/api?response=json&command=listClusters&apiKey=identity&signature=MWOOe7bm1J14DIfLjAGqsSVb8oo%3D"))
         .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json").build())
         .build();
      HttpResponse response = HttpResponse.builder()
         .payload(payloadFromResource("/listclustersresponse.json"))
         .statusCode(200).build();

      Set<Cluster> actual = requestSendsResponse(request, response).listClusters();

      Cluster cluster1 = Cluster.builder().id(1).name("Xen Clust 1").podId(1).podName("Dev Pod 1").zoneId(1).zoneName("Dev Zone 1").hypervisor("XenServer").clusterType(Host.ClusterType.CLOUD_MANAGED).allocationState(Host.AllocationState.ENABLED).managedState(Cluster.ManagedState.MANAGED).build();
      Cluster cluster2 = Cluster.builder().id(2).name("Xen Clust 1").podId(2).podName("Dev Pod 2").zoneId(2).zoneName("Dev Zone 2").hypervisor("XenServer").clusterType(Host.ClusterType.CLOUD_MANAGED).allocationState(Host.AllocationState.ENABLED).managedState(Cluster.ManagedState.MANAGED).build();
      ImmutableSet<Cluster> expected = ImmutableSet.of(cluster1, cluster2);

      assertEquals(actual, expected);
   }

   @Test
   public void testListClustersEmptyOn404() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint(URI.create("http://localhost:8080/client/api?response=json&command=listClusters&apiKey=identity&signature=MWOOe7bm1J14DIfLjAGqsSVb8oo%3D"))
         .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json").build())
         .build();
      HttpResponse response = HttpResponse.builder().statusCode(404).build();
      GlobalHostClient client = requestSendsResponse(request, response);

      assertEquals(client.listClusters(), ImmutableSet.of());
   }

   private Date makeDate(int year, int month, int date, int hour, int minute, int second, String timeZoneName) {
      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneName));
      cal.set(Calendar.YEAR, year);
      cal.set(Calendar.MONTH, month);
      cal.set(Calendar.DATE, date);
      cal.set(Calendar.HOUR_OF_DAY, hour);
      cal.set(Calendar.MINUTE, minute);
      cal.set(Calendar.SECOND, second);
      cal.set(Calendar.MILLISECOND, 0);
      return cal.getTime();
   }

   @Override
   protected GlobalHostClient clientFrom(CloudStackContext context) {
      return context.getGlobalContext().getApi().getHostClient();
   }
}