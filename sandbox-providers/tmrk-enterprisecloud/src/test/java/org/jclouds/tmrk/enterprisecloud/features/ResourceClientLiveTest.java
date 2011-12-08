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
package org.jclouds.tmrk.enterprisecloud.features;

import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;
import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolPerformanceStatistics;
import org.jclouds.tmrk.enterprisecloud.domain.resource.PerformanceStatistics;
import org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.ComputePoolCpuUsage;
import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummary;
import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummaryList;
import org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.ComputePoolCpuUsageDetail;
import org.jclouds.tmrk.enterprisecloud.domain.resource.memory.ComputePoolMemoryUsage;
import org.jclouds.tmrk.enterprisecloud.domain.resource.memory.ComputePoolMemoryUsageDetail;
import org.jclouds.tmrk.enterprisecloud.domain.resource.storage.ComputePoolStorageUsageDetail;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Tests behavior of {@code ResourceClient}
 * 
 * @author Jason King
 */
@Test(groups = "live", testName = "ResourceClientLiveTest")
public class ResourceClientLiveTest extends BaseTerremarkEnterpriseCloudClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getResourceClient();
   }

   private ResourceClient client;

   public void testGetResourceSummaries() throws Exception {
      ComputePoolResourceSummaryList list = client.getResourceSummaries(URI.create("/cloudapi/ecloud/computepools/environments/77/resourcesummarylist"));
      assertNotNull(list);
   }

   public void testMissingResourceSummaries() {
      assertNull(client.getResourceSummaries(URI.create("/cloudapi/ecloud/computepools/environments/-1/resourcesummarylist")));
   }

   public void testGetResourceSummary() throws Exception {
      ComputePoolResourceSummary resourceSummary = client.getResourceSummary(URI.create("/cloudapi/ecloud/computepools/89/resourcesummary"));
      assertNotNull(resourceSummary);
   }

   public void testMissingResourceSummary() {
      assertNull(client.getResourceSummary(URI.create("/cloudapi/ecloud/computepools/-1/resourcesummary")));
   }

   public void testGetComputePoolCpuUsage() throws Exception {
      ComputePoolCpuUsage usage = client.getComputePoolCpuUsage(URI.create("/cloudapi/ecloud/computepools/89/usage/cpu"));
      assertNotNull(usage);
      for(Link link: usage.getLinks()) {
         //The compute pool cpu usage has a link to a detail entry
         if( "application/vnd.tmrk.cloud.computePoolCpuUsageDetail".equals(link.getType())) {
            testGetComputePoolCpuUsageDetail(link.getHref());
         }
      }
   }

   public void testMissingComputePoolCpuUsage() {
      assertNull(client.getComputePoolCpuUsage(URI.create("/cloudapi/ecloud/computepools/-1/usage/cpu")));
   }

   private void testGetComputePoolCpuUsageDetail(URI uri) {
      ComputePoolCpuUsageDetail detail = client.getComputePoolCpuUsageDetail(uri);
      assertNotNull(detail.getTime());
   }

   public void testMissingComputePoolCpuUsageDetail() {
      assertNull(client.getComputePoolCpuUsageDetail(URI.create("/cloudapi/ecloud/computepools/-1/usage/cpu/details?time=2011-12-05t10%3a10%3a00z")));
   }

   public void testMissingDateComputePoolCpuUsageDetail() {
      ComputePoolCpuUsageDetail detail = client.getComputePoolCpuUsageDetail(URI.create("/cloudapi/ecloud/computepools/89/usage/cpu/details?time=1974-01-01t10%3a10%3a00z"));
      assertNotNull(detail.getTime());
      assertEquals(detail.getValue(), ResourceCapacity.builder().value(0).unit("MHz").build());
      assertNull(detail.getVirtualMachinesCpuUsage());
   }

   public void testGetComputePoolMemoryUsage() throws Exception {
      ComputePoolMemoryUsage usage = client.getComputePoolMemoryUsage(URI.create("/cloudapi/ecloud/computepools/89/usage/memory"));
      assertNotNull(usage);
      for(Link link: usage.getLinks()) {
         //The compute pool memory usage has a link to a detail entry
         if( "application/vnd.tmrk.cloud.computePoolMemoryUsageDetail".equals(link.getType())) {
            testGetComputePoolMemoryUsageDetail(link.getHref());
         }
      }
   }

   private void testGetComputePoolMemoryUsageDetail(URI uri) {
      ComputePoolMemoryUsageDetail detail = client.getComputePoolMemoryUsageDetail(uri);
      assertNotNull(detail.getTime());
   }

   public void testGetComputePoolStorageUsage() throws Exception {
      ComputePoolStorageUsageDetail usage = client.getComputePoolStorageUsage(URI.create("/cloudapi/ecloud/computepools/89/usage/storage"));
      assertNotNull(usage);
   }

   public void testGetPerformanceStatistics() throws Exception {
      ComputePoolPerformanceStatistics statistics = client.getComputePoolPerformanceStatistics(URI.create("/cloudapi/ecloud/computepools/89/performancestatistics"));
      assertNotNull(statistics);
      testPerformanceStatistic(statistics.getDaily().getCpu().getHref());
      testPerformanceStatistic(statistics.getDaily().getMemory().getHref());
      testPerformanceStatistic(statistics.getHourly().getCpu().getHref());
      testPerformanceStatistic(statistics.getHourly().getMemory().getHref());
   }
   
   private void testPerformanceStatistic(URI uri) {
      PerformanceStatistics stats = client.getPerformanceStatistics(uri);
      assertNotNull(stats);
   }
}