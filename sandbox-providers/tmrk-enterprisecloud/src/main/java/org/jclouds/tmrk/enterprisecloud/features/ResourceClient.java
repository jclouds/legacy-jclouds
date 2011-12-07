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

import org.jclouds.concurrent.Timeout;
import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolPerformanceStatistics;
import org.jclouds.tmrk.enterprisecloud.domain.resource.PerformanceStatistics;
import org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.ComputePoolCpuUsage;
import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummary;
import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummaryList;
import org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.ComputePoolCpuUsageDetail;
import org.jclouds.tmrk.enterprisecloud.domain.resource.memory.ComputePoolMemoryUsage;
import org.jclouds.tmrk.enterprisecloud.domain.resource.memory.ComputePoolMemoryUsageDetail;
import org.jclouds.tmrk.enterprisecloud.domain.resource.storage.ComputePoolStorageUsageDetail;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to various Resources.
 * <p/>
 * 
 * @see ResourceAsyncClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Jason King
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface ResourceClient {

   /**
    * The Get Resources Summary List call returns summary information regarding
    * resource utilization in the compute pools defined in an environment.
    *
    * @param uri the uri of the call based upon the environment
    * e.g. /cloudapi/ecloud/computepools/environments/{id}/resourcesummarylist
    * @return the summary list
    */
   ComputePoolResourceSummaryList getResourceSummaries(URI uri);

   /**
    * The Get Resources Summary call returns resource summary information regarding
    * a specified compute pool defined in an environment.
    *
    * @param uri the uri of the call based upon the compute pool
    * e.g. /cloudapi/ecloud/computepools/{id}/resourcesummary
    * @return the summary
    */
   ComputePoolResourceSummary getResourceSummary(URI uri);

   /**
    * The call returns information regarding processor usage for a specified compute pool
    * defined in an environment at five minute intervals for the 24 hours ending one hour
    * prior to the current time, rounded later.
    * For example, if current time is 11:22, the end time is 10:25.
    *
    * @param uri the uri of the call based upon the compute pool
    * e.g. /cloudapi/ecloud/computepools/{id}/usage/cpu
    * @return The cpu usage for the compute pool
    */
   ComputePoolCpuUsage getComputePoolCpuUsage(URI uri);

   /**
    * The Get Resources Usage Processor Detail call returns information regarding processor usage
    * for a specified compute pool defined in an environment at the time in the URL query part,
    * a specified five minute time interval within the 24 hours ending one hour prior to
    * the current time, rounded later.
    * For example, if current time is 11:22, the end time is 10:25.
    * The response includes usage of every virtual machine during the interval.
    *
    * Note: The query part is required and the time passed as the parameter must
    * precisely match a time in the preceding 24 hours.
    *
    * Times are on five minute intervals starting on the hour.
    * Available times are in Time in the response to {@code getComputePoolCpuUsage}
    * The time parameter is of the form 2011-12-05t10%3a10%3a00z
    *
    * Deleted is applicable and appears only when virtual machines have been removed
    * subsequent to the time of the snapshot.
    *
    * @param uri the uri of the call based upon the compute pool and the time
    * e.g. /cloudapi/ecloud/computepools/{id}/usage/cpu/details?time={time}
    * @return the compute pool cpu usage detail
    */
   ComputePoolCpuUsageDetail getComputePoolCpuUsageDetail(URI uri);

   /**
    * The Get Resources Usage Memory call returns information regarding memory usage
    * for a specified compute pool defined in an environment at five minute intervals
    * for the 24 hours ending one hour prior to the current time, rounded later.
    * For example, if current time is 11:22, the end time 10:25.
    *
    * @param uri the uri of the call based upon the compute pool
    * e.g. /cloudapi/ecloud/computepools/{id}/usage/memory
    * @return The memory usage for the compute pool
    */
   ComputePoolMemoryUsage getComputePoolMemoryUsage(URI uri);

   /**
    * The Get Resources Usage Memory Detail call returns information regarding memory usage
    * for a specified compute pool defined in an environment at the time in the URL query part,
    * a specified five minute time interval within the 24 hours ending one hour prior to the current time, 
    * rounded later.
    * For example, if current time is 11:22, the end time is 10:25.
    * The response includes usage of every virtual machine during the interval.
    *
    * Note: The query part is required and the time passed as the parameter must precisely match
    * a time in the preceding 24 hours.
    * Times are on five minute intervals starting on the hour.
    * Available times are in Time in the response to {@code getComputePoolMemoryUsage}.
    *
    * Deleted is applicable and appears only when virtual machine have been removed subsequent
    * to the time of the snapshot.
    *
    * @param uri the uri of the call based upon the compute pool and the time
    * e.g. /cloudapi/ecloud/computepools/{id}/usage/memory/details?time={time}
    * @return the compute pool memory usage detail
    */
   ComputePoolMemoryUsageDetail getComputePoolMemoryUsageDetail(URI uri);

   /**
    * The Get Resources Usage Storage call returns information regarding current storage usage
    * for a specified compute pool defined in an environment for active virtual machines.
    * The response includes usage of every active virtual machine.
    * @param uri the uri of the call based upon the compute pool
    * e.g. /cloudapi/ecloud/computepools/{id}/usage/storage
    * @return the compute pool storage usage detail
    */
   ComputePoolStorageUsageDetail getComputePoolStorageUsage(URI uri);

   /**
    * The Get Resources Performance Statistics call returns references to obtain
    * the individual performance statistics reports in a compute pool.
    * @param uri the uri of the call based upon the compute pool
    * e.g. /cloudapi/ecloud/computepools/{id}/performanceStatistics
    * @return
    */
   ComputePoolPerformanceStatistics getComputePoolPerformanceStatistics(URI uri);

   /**
    * The Get Resources Performance Statistics Processor Daily call returns daily
    * information regarding processor performance for a specified compute pool
    *
    * returns statistics for the previous seven days.
    *
    * The default endTime is midnight the beginning of the current day and the default
    * startTime is midnight seven days prior to the endTime.
    * For example, if the call is made at 2011-07-12T14:48:00Z, then startTime is 2011-07-05T00:00:00Z
    * and endTime is 2011-07-12T00:00:00Z.
    * @param uri uri the uri of the call based upon the compute pool
    * e.g. /cloudapi/ecloud/computepools/{id}/usage/cpu/performanceStatistics/daily
    * @return
    */
   PerformanceStatistics getDailyCpuPerformanceStatistics(URI uri);
}
