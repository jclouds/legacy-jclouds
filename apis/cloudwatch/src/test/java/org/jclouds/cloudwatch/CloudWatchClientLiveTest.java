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
package org.jclouds.cloudwatch;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseContextLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code CloudWatchClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public class CloudWatchClientLiveTest extends BaseContextLiveTest<RestContext<CloudWatchClient, CloudWatchAsyncClient>> {
   public CloudWatchClientLiveTest() {
      provider = "cloudwatch";
   }

   private CloudWatchClient client;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = context.getApi();
   }

   @Test
   protected void testGetMetricStatisticsInRegion() {
      getEC2MetricStatisticsInRegion(null);
   }

   protected Set<Datapoint> getEC2MetricStatisticsInRegion(String region) {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MINUTE, -60 * 24 * 3); // 3 days

      Set<Datapoint> datapoints = client.getMetricStatisticsInRegion(region, "CPUUtilization", "AWS/EC2",
            cal.getTime(), new Date(), 180, Statistics.AVERAGE, GetMetricStatisticsOptions.Builder.unit(Unit.PERCENT));

      return checkNotNull(datapoints, "Got null response for EC2 datapoints in region ");
   }
}
