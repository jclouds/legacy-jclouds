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

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.EC2Constants;
import org.jclouds.cloudwatch.domain.GetMetricStatisticsResponse;
import org.jclouds.cloudwatch.domain.ListMetricsResponse;
import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.cloudwatch.domain.Namespaces;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptionsV2;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.rest.RestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
   protected void testGetMetricStatisticsV2() {
      ListMetricsResponse metricsResponse = client.listMetrics(ListMetricsOptions.builder().build());

      // Walk through all datapoints in all metrics until we find a metric datapoint that returns statistics
      if (metricsResponse.getMetrics().size() > 0) {
         for (Metric metric : metricsResponse.getMetrics()) {
            Set<Dimension> dimensions = metric.getDimensions();
            boolean testRan = false;

            for (Dimension dimension : dimensions) {
               Date endTime = new Date();
               Calendar cal = Calendar.getInstance();

               cal.add(Calendar.MINUTE, -60 * 24); // 24 hours

               GetMetricStatisticsOptionsV2 options =
                     GetMetricStatisticsOptionsV2.builder()
                                                 .dimension(dimension)
                                                 .endTime(endTime)
                                                 .metricName(metric.getMetricName())
                                                 .namespace(metric.getNamespace())
                                                 .period(300)
                                                 .startTime(cal.getTime())
                                                 .statistics(ImmutableSet.of(Statistics.MAXIMUM,
                                                                             Statistics.MINIMUM))
                                                 .unit(Unit.PERCENT).build();
               GetMetricStatisticsResponse response = client.getMetricStatistics(options);

               if (response.getDatapoints().size() > 0) {
                  checkNotNull(response.getLabel());

                  for (Datapoint datapoint : response.getDatapoints()) {
                     checkArgument(datapoint.getAverage() == null);
                     checkNotNull(datapoint.getMaximum());
                     checkNotNull(datapoint.getMinimum());
                  }

                  testRan = true;
                  break;
                }
            }

            if (testRan) {
               break;
            }
         }
      }
   }

   @Test
   protected void testListMetrics() {
      ListMetricsResponse response;
      String testNamespace = Namespaces.EC2;
      String testMetricName = EC2Constants.MetricName.CPU_UTILIZATION;
      String testDimensionName = EC2Constants.Dimension.INSTANCE_TYPE;
      String testDimensionValue = "t1.micro";

      // Test an empty request (pulls all stored metric options across all products)
      response = client.listMetrics(ListMetricsOptions.builder().build());

      performDefaultMetricsTests(response);

      if (response.getMetrics().size() > 0) {
         Metric metric = response.getMetrics().iterator().next();

         testMetricName = metric.getMetricName();
         testNamespace = metric.getNamespace();

         if (metric.getDimensions().size() > 0) {
            Dimension dimension = metric.getDimensions().iterator().next();

            testDimensionName = dimension.getName();
            testDimensionValue = dimension.getValue();
         }

         if (testDimensionName == null) {
            for (Metric metric1 : response.getMetrics()) {
               Set<Dimension> dimensions = metric1.getDimensions();

               if (dimensions.size() > 0) {
                  Dimension dimension = metric.getDimensions().iterator().next();

                  testDimensionName = dimension.getName();
                  testDimensionValue = dimension.getValue();

                  break;
               }
            }
         }
      }

      // Test with a NextToken, even if it's null
      response = client.listMetrics(ListMetricsOptions.builder().nextToken(response.getNextToken()).build());

      performDefaultMetricsTests(response);

      // Test with a Namespace
      response = client.listMetrics(ListMetricsOptions.builder().namespace(testNamespace).build());

      performDefaultMetricsTests(response);

      for (Metric metric : response.getMetrics()) {
         checkArgument(metric.getNamespace().equals(testNamespace),
                       "All metrics should have the " + testNamespace + " Namespace.");
      }

      // Test with a MetricName
      response = client.listMetrics(ListMetricsOptions.builder().metricName(testMetricName).build());

      performDefaultMetricsTests(response);

      for (Metric metric : response.getMetrics()) {
         checkArgument(metric.getMetricName().equals(testMetricName),
                       "All metrics should have the " + testMetricName + " MetricName.");
      }

      // Test with a Dimension
      if (testDimensionName != null) {
         Dimension testDimension = new Dimension(testDimensionName, testDimensionValue);

         response = client.listMetrics(ListMetricsOptions.builder()
                                                         .dimension(testDimension).build());

         performDefaultMetricsTests(response);

         for (Metric metric : response.getMetrics()) {
            Set<Dimension> dimensions = metric.getDimensions();

            checkArgument(dimensions.size() == 1, "There should only be one Dimension.");

            Dimension dimension = dimensions.iterator().next();

            checkArgument(dimension.equals(testDimension),
                          "The retrieved Dimension and test Dimension should be equal.");
         }
      }
   }

   private void performDefaultMetricsTests(ListMetricsResponse response) {
      // If there are less than 500 metrics, NextToken should be null
      if (response.getMetrics().size() < 500) {
         checkArgument(response.getNextToken() == null,
                       "NextToken should be null for response with fewer than 500 metrics.");
      }

      for (Metric metric : response.getMetrics()) {
         Set<Dimension> dimensions = metric.getDimensions();

         checkArgument(dimensions.size() <= 10, "Dimensions set cannot be greater than 10 items.");

         for (Dimension dimension : dimensions) {
            checkNotNull(dimension.getName(), "Name cannot be null for a Dimension.");
            checkNotNull(dimension.getValue(), "Value cannot be null for a Dimension.");
         }

         checkNotNull(metric.getMetricName(), "MetricName cannot be null for a Metric.");
         checkNotNull(metric.getNamespace(), "Namespace cannot be null for a Metric.");
      }
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

   @Override
   protected TypeToken<RestContext<CloudWatchClient, CloudWatchAsyncClient>> contextType() {
      return CloudWatchApiMetadata.CONTEXT_TOKEN;
   }

}
