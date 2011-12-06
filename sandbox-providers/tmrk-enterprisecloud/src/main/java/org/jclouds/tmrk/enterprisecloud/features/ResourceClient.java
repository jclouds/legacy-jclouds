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
import org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.ComputePoolCpuUsage;
import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummary;
import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummaryList;

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
    * @param uri the uri of the call based upon the environment
    * e.g. /cloudapi/ecloud/computepools/environments/{id}/resourcesummarylist
    * @return the summary list
    */
   ComputePoolResourceSummaryList getResourceSummaries(URI uri);

   /**
    * The Get Resources Summary call returns resource summary information regarding
    * a specified compute pool defined in an environment.
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
    * @param uri the uri of the call based upon the compute pool
    * e.g. /cloudapi/ecloud/computepools/{id}/usage/cpu
    * @return The cpu usage for the compute pool
    */
   ComputePoolCpuUsage getComputePoolCpuUsage(URI uri);

}
