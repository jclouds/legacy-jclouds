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

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.rest.RestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

/**
 * Tests behavior of {@code CloudWatchApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName ="CloudWatchApiLiveTest", singleThreaded = true)
@Deprecated
public class CloudWatchApiLiveTest extends BaseContextLiveTest<RestContext<CloudWatchApi, CloudWatchAsyncApi>> {
   public CloudWatchApiLiveTest() {
      provider = "cloudwatch";
   }

   private CloudWatchApi api;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      api = context.getApi();
   }


   protected Set<Datapoint> getEC2MetricStatisticsInRegion(String region) {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MINUTE, -60 * 24 * 3); // 3 days

      Set<Datapoint> datapoints = api.getMetricStatisticsInRegion(region, "CPUUtilization", "AWS/EC2",
            cal.getTime(), new Date(), 180, Statistics.AVERAGE, GetMetricStatisticsOptions.Builder.unit(Unit.PERCENT));

      return checkNotNull(datapoints, "Got null response for EC2 datapoints in region ");
   }

   @Override
   protected TypeToken<RestContext<CloudWatchApi, CloudWatchAsyncApi>> contextType() {
      return CloudWatchApiMetadata.CONTEXT_TOKEN;
   }

}
