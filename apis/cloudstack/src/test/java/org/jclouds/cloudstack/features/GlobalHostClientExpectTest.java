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
package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.Cluster;
import org.jclouds.cloudstack.domain.Host;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.cloudstack.options.AddClusterOptions;
import org.jclouds.cloudstack.options.AddHostOptions;
import org.jclouds.cloudstack.options.AddSecondaryStorageOptions;
import org.jclouds.cloudstack.options.DeleteHostOptions;
import org.jclouds.cloudstack.options.UpdateClusterOptions;
import org.jclouds.cloudstack.options.UpdateHostOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Test the CloudStack GlobalHostClient
 *
 * @author Richard Downer
 */
@Test(groups = "unit", testName = "GlobalConfigurationClientExpectTest")
public class GlobalHostClientExpectTest extends BaseCloudStackExpectTest<GlobalHostClient> {

   @Test
   public void testListHostsWhenResponseIs2xx() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint("http://localhost:8080/client/api?response=json&command=listHosts&listAll=true&apiKey=identity&signature=NnYyyEy30G3V2dcIt7w4WZ68AU8%3D")
         .addHeader("Accept", "application/json").build();
      HttpResponse response = HttpResponse.builder()
         .payload(payloadFromResource("/listhostsresponse.json"))
         .statusCode(200).build();

      Set<Host> actual = requestSendsResponse(request, response).listHosts();

      Date lastPinged = makeDate(1970, Calendar.JANUARY, 16, 0, 54, 43, "GMT+02:00");
      Date created = makeDate(2011, Calendar.NOVEMBER, 26, 23, 28, 36, "GMT+02:00");
      Host host1 = Host.builder().id("1").name("cs2-xevsrv.alucloud.local").state(Host.State.UP).type(Host.Type.ROUTING).ipAddress("10.26.26.107").zoneId("1").zoneName("Dev Zone 1").podId("1").podName("Dev Pod 1").version("2.2.12.20110928142833").hypervisor("XenServer").cpuNumber(24).cpuSpeed(2266).cpuAllocated("2.76%").cpuUsed("0.1%").cpuWithOverProvisioning(54384.0F).networkKbsRead(4443).networkKbsWrite(15048).memoryTotal(100549733760L).memoryAllocated(3623878656L).memoryUsed(3623878656L).capabilities("xen-3.0-x86_64 , xen-3.0-x86_32p , hvm-3.0-x86_32 , hvm-3.0-x86_32p , hvm-3.0-x86_64").lastPinged(lastPinged).managementServerId("223098941760041").clusterId("1").clusterName("Xen Clust 1").clusterType(Host.ClusterType.CLOUD_MANAGED).localStorageActive(false).created(created).events("PrepareUnmanaged; HypervisorVersionChanged; ManagementServerDown; PingTimeout; AgentDisconnected; MaintenanceRequested; HostDown; AgentConnected; StartAgentRebalance; ShutdownRequested; Ping").hasEnoughCapacity(false).allocationState(AllocationState.ENABLED).build();

      Date disconnected = makeDate(2011, Calendar.NOVEMBER, 26, 23, 33, 38, "GMT+02:00");
      lastPinged = makeDate(1970, Calendar.JANUARY, 16, 0, 42, 30, "GMT+02:00");
      created = makeDate(2011, Calendar.NOVEMBER, 26, 23, 33, 38, "GMT+02:00");
      Host host2 = Host.builder().id("2").name("nfs://10.26.26.165/mnt/nfs/cs_sec").state(Host.State.ALERT).disconnected(disconnected).type(Host.Type.SECONDARY_STORAGE).ipAddress("nfs").zoneId("1").zoneName("Dev Zone 1").version("2.2.12.20110928142833").hypervisor("None").lastPinged(lastPinged).localStorageActive(false).created(created).events("ManagementServerDown; AgentDisconnected; Remove; MaintenanceRequested; AgentConnected; Ping").hasEnoughCapacity(false).allocationState(AllocationState.ENABLED).build();

      lastPinged = makeDate(1970, Calendar.JANUARY, 16, 0, 54, 43, "GMT+02:00");
      created = makeDate(2011, Calendar.NOVEMBER, 26, 23, 35, 51, "GMT+02:00");
      Host host3 = Host.builder().id("3").name("s-1-VM").state(Host.State.UP).type(Host.Type.SECONDARY_STORAGE_VM).ipAddress("10.26.26.81").zoneId("1").zoneName("Dev Zone 1").podId("1").podName("Dev Pod 1").version("2.2.12.20110928142833").lastPinged(lastPinged).managementServerId("223098941760041").localStorageActive(false).created(created).events("PrepareUnmanaged; HypervisorVersionChanged; ManagementServerDown; PingTimeout; AgentDisconnected; MaintenanceRequested; HostDown; AgentConnected; StartAgentRebalance; ShutdownRequested; Ping").hasEnoughCapacity(false).allocationState(AllocationState.ENABLED).build();

      lastPinged = makeDate(1970, Calendar.JANUARY, 16, 0, 54, 43, "GMT+02:00");
      created = makeDate(2011, Calendar.NOVEMBER, 26, 23, 36, 46, "GMT+02:00");
      Host host4 = Host.builder().id("4").name("v-2-VM").state(Host.State.UP).type(Host.Type.CONSOLE_PROXY).ipAddress("10.26.26.96").zoneId("1").zoneName("Dev Zone 1").podId("1").podName("Dev Pod 1").version("2.2.12.20110928142833").lastPinged(lastPinged).managementServerId("223098941760041").localStorageActive(false).created(created).events("PrepareUnmanaged; HypervisorVersionChanged; ManagementServerDown; PingTimeout; AgentDisconnected; MaintenanceRequested; HostDown; AgentConnected; StartAgentRebalance; ShutdownRequested; Ping").hasEnoughCapacity(false).allocationState(AllocationState.ENABLED).build();

      Set<Host> expected = ImmutableSet.of(host1, host2, host3, host4);

      assertEquals(actual, expected);
   }

   @Test
   public void testListHostsEmptyOn404() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint("http://localhost:8080/client/api?response=json&command=listHosts&listAll=true&apiKey=identity&signature=NnYyyEy30G3V2dcIt7w4WZ68AU8%3D")
         .addHeader("Accept", "application/json").build();
      HttpResponse response = HttpResponse.builder().statusCode(404).build();
      GlobalHostClient client = requestSendsResponse(request, response);

      assertEquals(client.listHosts(), ImmutableSet.of());
   }

   HttpRequest addHost = HttpRequest.builder().method("GET")
                                    .endpoint("http://localhost:8080/client/api")
                                    .addQueryParam("response", "json")
                                    .addQueryParam("command", "addHost")
                                    .addQueryParam("zoneid", "1")
                                    .addQueryParam("url", "http%3A//example.com")
                                    .addQueryParam("hypervisor", "XenServer")
                                    .addQueryParam("username", "fred")
                                    .addQueryParam("password", "sekrit")
                                    .addQueryParam("hosttags", "")
                                    .addQueryParam("allocationstate", "Enabled")
                                    .addQueryParam("clusterid", "1")
                                    .addQueryParam("clustername", "Xen%20Clust%201")
                                    .addQueryParam("podid", "1")
                                    .addQueryParam("apiKey", "identity")
                                    .addQueryParam("signature", "ExGaljKKQIlVbWk5hd0BnnjmBzs=")
                                    .addHeader("Accept", "application/json").build();

   @Test
   public void testAddHostWhenResponseIs2xx() {
      HttpResponse response = HttpResponse.builder()
         .payload(payloadFromResource("/addhostresponse.json"))
         .statusCode(200).build();

      Date lastPinged = makeDate(1970, Calendar.JANUARY, 16, 0, 54, 43, "GMT+02:00");
      Date created = makeDate(2011, Calendar.NOVEMBER, 26, 23, 28, 36, "GMT+02:00");
      Host expected = Host.builder().id("1").name("cs2-xevsrv.alucloud.local").state(Host.State.UP).type(Host.Type.ROUTING).ipAddress("10.26.26.107").zoneId("1").zoneName("Dev Zone 1").podId("1").podName("Dev Pod 1").version("2.2.12.20110928142833").hypervisor("XenServer").cpuNumber(24).cpuSpeed(2266).cpuAllocated("2.76%").cpuUsed("0.1%").cpuWithOverProvisioning(54384.0F).networkKbsRead(4443).networkKbsWrite(15048).memoryTotal(100549733760L).memoryAllocated(3623878656L).memoryUsed(3623878656L).capabilities("xen-3.0-x86_64 , xen-3.0-x86_32p , hvm-3.0-x86_32 , hvm-3.0-x86_32p , hvm-3.0-x86_64").lastPinged(lastPinged).managementServerId("223098941760041").clusterId("1").clusterName("Xen Clust 1").clusterType(Host.ClusterType.CLOUD_MANAGED).localStorageActive(false).created(created).events("PrepareUnmanaged; HypervisorVersionChanged; ManagementServerDown; PingTimeout; AgentDisconnected; MaintenanceRequested; HostDown; AgentConnected; StartAgentRebalance; ShutdownRequested; Ping").hasEnoughCapacity(false).allocationState(AllocationState.ENABLED).build();

      Host actual = requestSendsResponse(addHost, response).addHost("1", "http://example.com", "XenServer", "fred", "sekrit",
         AddHostOptions.Builder.hostTags(ImmutableSet.<String>of()).allocationState(AllocationState.ENABLED).clusterId("1").clusterName("Xen Clust 1").podId("1"));

      assertEquals(actual, expected);
   }

   @Test
   public void testUpdateHostWhenResponseIs2xx() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint("http://localhost:8080/client/api?response=json&command=updateHost&id=1&allocationstate=Enabled&hosttags=&oscategoryid=5&apiKey=identity&signature=qTxNq9yQG8S108giqS/ROFzgev8%3D")
         .addHeader("Accept", "application/json").build();
      HttpResponse response = HttpResponse.builder()
         .payload(payloadFromResource("/updatehostresponse.json"))
         .statusCode(200).build();

      Date lastPinged = makeDate(1970, Calendar.JANUARY, 16, 0, 54, 43, "GMT+02:00");
      Date created = makeDate(2011, Calendar.NOVEMBER, 26, 23, 28, 36, "GMT+02:00");
      Host expected = Host.builder().id("1").name("cs2-xevsrv.alucloud.local").state(Host.State.UP).type(Host.Type.ROUTING).ipAddress("10.26.26.107").zoneId("1").zoneName("Dev Zone 1").podId("1").podName("Dev Pod 1").version("2.2.12.20110928142833").hypervisor("XenServer").cpuNumber(24).cpuSpeed(2266).cpuAllocated("2.76%").cpuUsed("0.1%").cpuWithOverProvisioning(54384.0F).networkKbsRead(4443).networkKbsWrite(15048).memoryTotal(100549733760L).memoryAllocated(3623878656L).memoryUsed(3623878656L).capabilities("xen-3.0-x86_64 , xen-3.0-x86_32p , hvm-3.0-x86_32 , hvm-3.0-x86_32p , hvm-3.0-x86_64").lastPinged(lastPinged).managementServerId("223098941760041").clusterId("1").clusterName("Xen Clust 1").clusterType(Host.ClusterType.CLOUD_MANAGED).localStorageActive(false).created(created).events("PrepareUnmanaged; HypervisorVersionChanged; ManagementServerDown; PingTimeout; AgentDisconnected; MaintenanceRequested; HostDown; AgentConnected; StartAgentRebalance; ShutdownRequested; Ping").hasEnoughCapacity(false).allocationState(AllocationState.ENABLED).build();

      Host actual = requestSendsResponse(request, response).updateHost("1", UpdateHostOptions.Builder.allocationState(AllocationState.ENABLED).hostTags(ImmutableSet.<String>of()).osCategoryId("5"));

      assertEquals(actual, expected);
   }

   HttpRequest updateHostPassword = HttpRequest.builder().method("GET")
                                               .endpoint("http://localhost:8080/client/api")
                                               .addQueryParam("response", "json")
                                               .addQueryParam("command", "updateHostPassword")
                                               .addQueryParam("hostid", "1")
                                               .addQueryParam("username", "fred")
                                               .addQueryParam("password", "sekrit")
                                               .addQueryParam("apiKey", "identity")
                                               .addQueryParam("signature", "g9nMKDWoiU72y0HhaRFekZCgfJc=")
                                               .addHeader("Accept", "application/json").build();

   @Test
   public void testUpdateHostPasswordWhenResponseIs2xx() {
      HttpResponse response = HttpResponse.builder()
         .statusCode(200).build();
      requestSendsResponse(updateHostPassword, response).updateHostPassword("1", "fred", "sekrit");
   }

   @Test
   public void testDeleteHostWhenResponseIs2xx() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint("http://localhost:8080/client/api?response=json&command=deleteHost&id=1&forced=true&forcedestroylocalstorage=true&apiKey=identity&signature=ZdvO1BWBkdPiDAjsVlKtqDe6N7k%3D")
         .addHeader("Accept", "application/json")
         .build();
      HttpResponse response = HttpResponse.builder()
         .statusCode(200).build();

      requestSendsResponse(request, response).deleteHost("1", DeleteHostOptions.Builder.forced(true).forceDestroyLocalStorage(true));
   }

   @Test
   public void testPrepareHostForMaintenanceWhenResponseIs2xx() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint("http://localhost:8080/client/api?response=json&command=prepareHostForMaintenance&id=1&apiKey=identity&signature=9tDwdox/xAKmZr9kVrR6Ttnxf3U%3D")
         .addHeader("Accept", "application/json").build();
      HttpResponse response = HttpResponse.builder()
         .payload(payloadFromResource("/preparehostformaintenanceresponse.json"))
         .statusCode(200).build();

      String actual = requestSendsResponse(request, response).prepareHostForMaintenance("1");
      assertEquals(actual, "2036");
   }

   @Test
   public void testCancelHostMaintenanceWhenResponseIs2xx() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint("http://localhost:8080/client/api?response=json&command=cancelHostMaintenance&id=1&apiKey=identity&signature=9RduzuBoyRZKNTzAoVqUo9gRTfk%3D")
         .addHeader("Accept", "application/json").build();
      HttpResponse response = HttpResponse.builder()
         .payload(payloadFromResource("/cancelhostmaintenanceresponse.json"))
         .statusCode(200).build();

      String actual = requestSendsResponse(request, response).cancelHostMaintenance("1");
      assertEquals(actual, "2036");
   }

   @Test
   public void testReconnectHostWhenResponseIs2xx() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint("http://localhost:8080/client/api?response=json&command=reconnectHost&id=1&apiKey=identity&signature=wJEF02vwdyOnJOTa%2BWMMK906aRU%3D")
         .addHeader("Accept", "application/json").build();
      HttpResponse response = HttpResponse.builder()
         .payload(payloadFromResource("/reconnecthostresponse.json"))
         .statusCode(200).build();

      String actual = requestSendsResponse(request, response).reconnectHost("1");
      assertEquals(actual, "2036");
   }

   @Test
   public void testAddSecondaryStorageWhenResponseIs2xx() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint("http://localhost:8080/client/api?response=json&command=addSecondaryStorage&url=nfs%3A//10.26.26.165/mnt/nfs/cs_sec&zoneid=1&apiKey=identity&signature=MccRKx1yPP43ImiO70WlhVDlAIA%3D")
         .addHeader("Accept", "application/json").build();
      HttpResponse response = HttpResponse.builder()
         .payload(payloadFromResource("/addsecondarystorageresponse.json"))
         .statusCode(200).build();

      Date disconnected = makeDate(2011, Calendar.NOVEMBER, 26, 23, 33, 38, "GMT+02:00");
      Date lastPinged = makeDate(1970, Calendar.JANUARY, 16, 0, 42, 30, "GMT+02:00");
      Date created = makeDate(2011, Calendar.NOVEMBER, 26, 23, 33, 38, "GMT+02:00");
      Host expected = Host.builder().id("2").name("nfs://10.26.26.165/mnt/nfs/cs_sec").state(Host.State.ALERT).disconnected(disconnected).type(Host.Type.SECONDARY_STORAGE).ipAddress("nfs").zoneId("1").zoneName("Dev Zone 1").version("2.2.12.20110928142833").hypervisor("None").lastPinged(lastPinged).localStorageActive(false).created(created).events("ManagementServerDown; AgentDisconnected; Remove; MaintenanceRequested; AgentConnected; Ping").hasEnoughCapacity(false).allocationState(AllocationState.ENABLED).build();

      Host actual = requestSendsResponse(request, response).addSecondaryStorage("nfs://10.26.26.165/mnt/nfs/cs_sec", AddSecondaryStorageOptions.Builder.zoneId("1"));

      assertEquals(actual, expected);
   }

   @Test
   public void testListClustersWhenResponseIs2xx() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint("http://localhost:8080/client/api?response=json&command=listClusters&listAll=true&apiKey=identity&signature=lbimqg0OKIq8sgQBpNmi4oQNFog%3D")
         .addHeader("Accept", "application/json").build();
      HttpResponse response = HttpResponse.builder()
         .payload(payloadFromResource("/listclustersresponse.json"))
         .statusCode(200).build();

      Set<Cluster> actual = requestSendsResponse(request, response).listClusters();

      Cluster cluster1 = Cluster.builder().id("1").name("Xen Clust 1").podId("1").podName("Dev Pod 1").zoneId("1").zoneName("Dev Zone 1").hypervisor("XenServer").clusterType(Host.ClusterType.CLOUD_MANAGED).allocationState(AllocationState.ENABLED).managedState(Cluster.ManagedState.MANAGED).build();
      Cluster cluster2 = Cluster.builder().id("2").name("Xen Clust 1").podId("2").podName("Dev Pod 2").zoneId("2").zoneName("Dev Zone 2").hypervisor("XenServer").clusterType(Host.ClusterType.CLOUD_MANAGED).allocationState(AllocationState.ENABLED).managedState(Cluster.ManagedState.MANAGED).build();
      ImmutableSet<Cluster> expected = ImmutableSet.of(cluster1, cluster2);

      assertEquals(actual, expected);
   }

   @Test
   public void testListClustersEmptyOn404() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint("http://localhost:8080/client/api?response=json&command=listClusters&listAll=true&apiKey=identity&signature=lbimqg0OKIq8sgQBpNmi4oQNFog%3D")
         .addHeader("Accept", "application/json").build();
      HttpResponse response = HttpResponse.builder().statusCode(404).build();
      GlobalHostClient client = requestSendsResponse(request, response);

      assertEquals(client.listClusters(), ImmutableSet.of());
   }

   HttpRequest addCluster = HttpRequest.builder().method("GET")
                                       .endpoint("http://localhost:8080/client/api")
                                       .addQueryParam("response", "json")
                                       .addQueryParam("command", "addCluster")
                                       .addQueryParam("zoneid", "1")
                                       .addQueryParam("clustername", "Xen Clust 1")
                                       .addQueryParam("clustertype", "CloudManaged")
                                       .addQueryParam("hypervisor", "XenServer")
                                       .addQueryParam("allocationstate", "Enabled")
                                       .addQueryParam("podid", "1")
                                       .addQueryParam("url", "http://example.com/cluster")
                                       .addQueryParam("username", "fred")
                                       .addQueryParam("password", "sekrit")
                                       .addQueryParam("apiKey", "identity")
                                       .addQueryParam("signature", "2uIQ5qF0bVycXK111wxvogWp1Yw=")
                                       .addHeader("Accept", "application/json").build();

   @Test
   public void testAddClusterWhenResponseIs2xx() {
      HttpResponse response = HttpResponse.builder()
         .payload(payloadFromResource("/addclusterresponse.json"))
         .statusCode(200).build();

      Cluster expected = Cluster.builder().id("1").name("Xen Clust 1").podId("1").podName("Dev Pod 1").zoneId("1").zoneName("Dev Zone 1").hypervisor("XenServer").clusterType(Host.ClusterType.CLOUD_MANAGED).allocationState(AllocationState.ENABLED).managedState(Cluster.ManagedState.MANAGED).build();

      Cluster actual = requestSendsResponse(addCluster, response).addCluster("1", "Xen Clust 1", Host.ClusterType.CLOUD_MANAGED, "XenServer", AddClusterOptions.Builder.allocationState(AllocationState.ENABLED).podId("1").url("http://example.com/cluster").username("fred").password("sekrit"));

      assertEquals(actual, expected);
   }

   HttpRequest updateCluster = HttpRequest.builder().method("GET")
                                       .endpoint("http://localhost:8080/client/api")
                                       .addQueryParam("response", "json")
                                       .addQueryParam("command", "updateCluster")
                                       .addQueryParam("id", "1")
                                       .addQueryParam("allocationstate", "Enabled")
                                       .addQueryParam("clustername", "Xen Clust 1")
                                       .addQueryParam("clustertype", "CloudManaged")
                                       .addQueryParam("hypervisor", "XenServer")
                                       .addQueryParam("managedstate", "Managed")
                                       .addQueryParam("apiKey", "identity")
                                       .addQueryParam("signature", "/wbuYKwInciSXWkUf05lEfJZShQ=")
                                       .addHeader("Accept", "application/json").build();

   @Test
   public void testUpdateClusterWhenResponseIs2xx() {
      HttpResponse response = HttpResponse.builder()
         .payload(payloadFromResource("/updateclusterresponse.json"))
         .statusCode(200).build();

      Cluster expected = Cluster.builder().id("1").name("Xen Clust 1").podId("1").podName("Dev Pod 1").zoneId("1").zoneName("Dev Zone 1").hypervisor("XenServer").clusterType(Host.ClusterType.CLOUD_MANAGED).allocationState(AllocationState.ENABLED).managedState(Cluster.ManagedState.MANAGED).build();

      Cluster actual = requestSendsResponse(updateCluster, response).updateCluster("1", UpdateClusterOptions.Builder.allocationState(AllocationState.ENABLED).clusterName("Xen Clust 1").clusterType(Host.ClusterType.CLOUD_MANAGED).hypervisor("XenServer").managedState(Cluster.ManagedState.MANAGED));

      assertEquals(actual, expected);
   }

   HttpRequest updateClusterPassword = HttpRequest.builder().method("GET")
                                                  .endpoint("http://localhost:8080/client/api")
                                                  .addQueryParam("response", "json")
                                                  .addQueryParam("command", "updateHostPassword")
                                                  .addQueryParam("clusterid", "1")
                                                  .addQueryParam("username", "fred")
                                                  .addQueryParam("password", "sekrit")
                                                  .addQueryParam("apiKey", "identity")
                                                  .addQueryParam("signature", "xwc83%2BoYK0cuAiFQAlg/7/1IVHE=")
                                                  .addHeader("Accept", "application/json").build();

   @Test
   public void testUpdateClusterPasswordWhenResponseIs2xx() {
      HttpResponse response = HttpResponse.builder()
         .statusCode(200).build();
      requestSendsResponse(updateClusterPassword, response).updateClusterPassword("1", "fred", "sekrit");
   }

   @Test
   public void testDeleteClusterWhenResponseIs2xx() {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint("http://localhost:8080/client/api?response=json&command=deleteCluster&id=1&apiKey=identity&signature=CKH26MFgKGY7Sosd17LjBMNa3AI%3D")
         .addHeader("Accept", "application/json").build();
      HttpResponse response = HttpResponse.builder()
         .statusCode(200).build();

      requestSendsResponse(request, response).deleteCluster("1");
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
