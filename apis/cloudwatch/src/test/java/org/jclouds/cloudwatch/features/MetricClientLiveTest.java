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
package org.jclouds.cloudwatch.features;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import junit.framework.Assert;
import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.EC2Constants;
import org.jclouds.cloudwatch.domain.GetMetricStatistics;
import org.jclouds.cloudwatch.domain.GetMetricStatisticsResponse;
import org.jclouds.cloudwatch.domain.ListMetricsResponse;
import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.domain.Namespaces;
import org.jclouds.cloudwatch.domain.StatisticSet;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.cloudwatch.internal.BaseCloudWatchClientLiveTest;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Jeremy Whitlock, Adrian Cole
 */
@Test(groups = "live", testName = "MetricClientLiveTest")
public class MetricClientLiveTest extends BaseCloudWatchClientLiveTest {

   @Test
   protected void testPutMetricData() throws Exception {
      String metricName = "TestMetricName" + System.currentTimeMillis();
      String namespace = "JCLOUDS/Test";
      Date metricTimestamp = new Date();
      // CloudWatch rounds metric timestamps down to the closest minute
      Date metricTimestampInCloudWatch =
            new Date(metricTimestamp.getTime() - (metricTimestamp.getTime() % (60 * 1000)));
      StatisticSet ss = StatisticSet.builder()
                                    .maximum(4.0)
                                    .minimum(1.0)
                                    .sampleCount(4.0)
                                    .sum(10.0)
                                    .build();
      MetricDatum metricDatum = MetricDatum.builder()
                                           .metricName(metricName + "_1")
                                           .statisticSet(ss)
                                           .dimension(new Dimension("BaseMetricName", metricName))
                                           .dimension(new Dimension("TestDimension2", "TEST2"))
                                           .unit(Unit.COUNT)
                                           .timestamp(metricTimestamp)
                                           .build();
      MetricDatum metricDatum2 = MetricDatum.builder()
                                           .metricName(metricName + "_2")
                                           .dimension(new Dimension("BaseMetricName", metricName))
                                           .unit(Unit.COUNT)
                                           .timestamp(metricTimestamp)
                                           .value(10.0)
                                           .build();

      client().putMetricData(ImmutableSet.of(metricDatum, metricDatum2), namespace);

      ListMetricsOptions lmo = ListMetricsOptions.builder().namespace(namespace)
                                                 .dimension(new Dimension("BaseMetricName", metricName))
                                                 .build();
      boolean success = new RetryablePredicate<ListMetricsOptions>(new Predicate<ListMetricsOptions>() {
         @Override
         public boolean apply(ListMetricsOptions options) {
            return Iterables.size(client().listMetrics(options)) == 2;
         }
      }, 20, 1, TimeUnit.MINUTES).apply(lmo);

      if (!success) {
         Assert.fail("Unable to gather the created CloudWatch data within the time (20m) allotted.");
      }

      ListMetricsResponse lmr = client().listMetrics(lmo);
      Date endTime = new Date(metricTimestampInCloudWatch.getTime() + (60 * 1000)); // Pad a minute just in case
      Date startTime = new Date(metricTimestampInCloudWatch.getTime() - (60 * 1000)); // Pad a minute just in case

      for (Metric m : lmr) {
         GetMetricStatistics gms = GetMetricStatistics.builder()
                                                      .dimensions(m.getDimensions())
                                                      .namespace(namespace)
                                                      .metricName(m.getMetricName())
                                                      .endTime(endTime)
                                                      .statistic(Statistics.MAXIMUM)
                                                      .statistic(Statistics.MINIMUM)
                                                      .statistic(Statistics.SAMPLE_COUNT)
                                                      .statistic(Statistics.SUM)
                                                      .period(60)
                                                      .startTime(startTime)
                                                      .unit(Unit.COUNT)
                                                      .build();
         GetMetricStatisticsResponse gmr = client().getMetricStatistics(gms);

         Assert.assertEquals(1, Iterables.size(gmr));

         Datapoint datapoint = gmr.iterator().next();

         Assert.assertEquals(datapoint.getTimestamp(), metricTimestampInCloudWatch);
         Assert.assertNull(datapoint.getCustomUnit());
         Assert.assertEquals(Unit.COUNT, datapoint.getUnit());
         Assert.assertNull(datapoint.getAverage());

         if (m.getDimensions().size() == 1) {
            Assert.assertEquals(10.0, datapoint.getMaximum());
            Assert.assertEquals(10.0, datapoint.getMinimum());
            Assert.assertEquals(10.0, datapoint.getSum());
            Assert.assertEquals(1.0, datapoint.getSamples());
         } else {
            Assert.assertEquals(4.0, datapoint.getMaximum());
            Assert.assertEquals(1.0, datapoint.getMinimum());
            Assert.assertEquals(10.0, datapoint.getSum());
            Assert.assertEquals(4.0, datapoint.getSamples());
         }
      }
   }

   // TODO: change this test to retrieve pre-seeded custom metrics
   @Test
   protected void testGetMetricStatistics() {
      ListMetricsResponse metricsResponse = client().listMetrics();

      // Walk through all datapoints in all metrics until we find a metric datapoint that returns statistics
      if (metricsResponse.size() > 0) {
         for (Metric metric : metricsResponse) {
            Set<Dimension> dimensions = metric.getDimensions();
            boolean testRan = false;

            for (Dimension dimension : dimensions) {
               Date endTime = new Date();
               Calendar cal = Calendar.getInstance();

               cal.add(Calendar.MINUTE, -60 * 24); // 24 hours

               GetMetricStatistics options =
                     GetMetricStatistics.builder()
                                                 .dimension(dimension)
                                                 .endTime(endTime)
                                                 .metricName(metric.getMetricName())
                                                 .namespace(metric.getNamespace())
                                                 .period(300)
                                                 .startTime(cal.getTime())
                                                 .statistics(ImmutableSet.of(Statistics.MAXIMUM,
                                                                             Statistics.MINIMUM))
                                                 .unit(Unit.PERCENT).build();
               GetMetricStatisticsResponse response = client().getMetricStatistics(options);

               if (response.size() > 0) {
                  checkNotNull(response.getLabel());

                  for (Datapoint datapoint : response) {
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
      response = client().listMetrics();

      performDefaultMetricsTests(response);

      if (response.size() > 0) {
         Metric metric = response.iterator().next();

         testMetricName = metric.getMetricName();
         testNamespace = metric.getNamespace();

         if (metric.getDimensions().size() > 0) {
            Dimension dimension = metric.getDimensions().iterator().next();

            testDimensionName = dimension.getName();
            testDimensionValue = dimension.getValue();
         }

         if (testDimensionName == null) {
            for (Metric metric1 : response) {
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
      response = client().listMetrics(ListMetricsOptions.builder().nextToken(response.getNextToken()).build());

      performDefaultMetricsTests(response);

      // Test with a Namespace
      response = client().listMetrics(ListMetricsOptions.builder().namespace(testNamespace).build());

      performDefaultMetricsTests(response);

      for (Metric metric : response) {
         checkArgument(metric.getNamespace().equals(testNamespace),
                       "All metrics should have the " + testNamespace + " Namespace.");
      }

      // Test with a MetricName
      response = client().listMetrics(ListMetricsOptions.builder().metricName(testMetricName).build());

      performDefaultMetricsTests(response);

      for (Metric metric : response) {
         checkArgument(metric.getMetricName().equals(testMetricName),
                       "All metrics should have the " + testMetricName + " MetricName.");
      }

      // Test with a Dimension
      if (testDimensionName != null) {
         Dimension testDimension = new Dimension(testDimensionName, testDimensionValue);

         response = client().listMetrics(ListMetricsOptions.builder().dimension(testDimension).build());

         performDefaultMetricsTests(response);

         for (Metric metric : response) {
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
      if (response.size() < 500) {
         checkArgument(response.getNextToken() == null,
                       "NextToken should be null for response with fewer than 500 metrics.");
      }

      for (Metric metric : response) {
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

   protected MetricClient client() {
      return context.getApi().getMetricClientForRegion(null);
   }
}
