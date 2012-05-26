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
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.easymock.EasyMock;
import org.jclouds.cloudwatch.domain.ListMetricsResponse;
import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.features.MetricClient;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

/**
 * Tests behavior of {@code CloudWatch}.
 *
 * @author Jeremy Whitlock
 */
public class CloudWatchTest {

   /**
    * Tests {@link CloudWatch#listMetrics(CloudWatchClient, String, org.jclouds.cloudwatch.options.ListMetricsOptions)}
    * where a single response returns all results.
    *
    * @throws Exception if anything goes wrong
    */
   @Test
   public void testSinglePageResult() throws Exception {
      CloudWatchClient client = createMock(CloudWatchClient.class);
      MetricClient metricClient = createMock(MetricClient.class);
      ListMetricsOptions options = ListMetricsOptions.builder().build();
      ListMetricsResponse response = new ListMetricsResponse(ImmutableSet.of(createMock(Metric.class)), null);
      
      expect(client.getMetricClientForRegion(null))
            .andReturn(metricClient)
            .atLeastOnce();

      expect(metricClient.listMetrics(options))
            .andReturn(response)
            .once();

      EasyMock.replay(client, metricClient);

      Assert.assertEquals(1, Iterables.size(CloudWatch.listMetrics(client, null, options)));
   }

   /**
    * Tests {@link CloudWatch#listMetrics(CloudWatchClient, String, org.jclouds.cloudwatch.options.ListMetricsOptions)}
    * where retrieving all results requires multiple requests.
    *
    * @throws Exception if anything goes wrong
    */
   @Test
   public void testMultiPageResult() throws Exception {
      CloudWatchClient client = createMock(CloudWatchClient.class);
      MetricClient metricClient = createMock(MetricClient.class);
      ListMetricsOptions options = ListMetricsOptions.builder().build();
      ListMetricsResponse response1 = new ListMetricsResponse(ImmutableSet.of(createMock(Metric.class)), "NEXTTOKEN");
      ListMetricsResponse response2 = new ListMetricsResponse(ImmutableSet.of(createMock(Metric.class)), null);

      // Using EasyMock.eq("") because EasyMock makes it impossible to pass null as a String value here
      expect(client.getMetricClientForRegion(EasyMock.eq("")))
            .andReturn(metricClient)
            .atLeastOnce();
      
      expect(metricClient.listMetrics(anyObject(ListMetricsOptions.class)))
            .andReturn(response1)
            .once();
      expect(metricClient.listMetrics(anyObject(ListMetricsOptions.class)))
            .andReturn(response2)
            .once();

      EasyMock.replay(client, metricClient);

      Assert.assertEquals(2, Iterables.size(CloudWatch.listMetrics(client, "", options)));
   }

   /**
    * Tests {@link CloudWatch#putMetricData(CloudWatchClient, String, Iterable, String)} where the set of metrics is
    * greater than 10.
    *
    * @throws Exception if anything goes wrong
    */
   @Test
   public void testPutMetricData() throws Exception {
      CloudWatchClient client = createMock(CloudWatchClient.class);
      MetricClient metricClient = createMock(MetricClient.class);
      Set<MetricDatum> metrics = Sets.newLinkedHashSet();
      String namespace = "JCLOUDS/Test";

      for (int i = 0; i < 11; i++) {
         metrics.add(MetricDatum.builder().build());
      }

      // Using EasyMock.eq("") because EasyMock makes it impossible to pass null as a String value here
      expect(client.getMetricClientForRegion(EasyMock.eq("")))
            .andReturn(metricClient)
            .atLeastOnce();


      metricClient.putMetricData(metrics, namespace);

      expectLastCall().times(2);

      EasyMock.replay(client, metricClient);

      CloudWatch.putMetricData(client, "", metrics, namespace);

      EasyMock.verify(metricClient);
   }

}
