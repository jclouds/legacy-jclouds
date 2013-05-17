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
package org.jclouds.cloudwatch;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.easymock.EasyMock;
import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.features.MetricApi;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code CloudWatch}.
 *
 * @author Jeremy Whitlock
 */
@Test(testName = "CloudWatchTest")
public class CloudWatchTest {

   /**
    * Tests {@link CloudWatch#listMetrics(CloudWatchApi, String, org.jclouds.cloudwatch.options.ListMetricsOptions)}
    * where a single response returns all results.
    *
    * @throws Exception if anything goes wrong
    */
   @Test
   public void testSinglePageResult() throws Exception {
      CloudWatchApi api = createMock(CloudWatchApi.class);
      MetricApi metricApi = createMock(MetricApi.class);
      ListMetricsOptions options = new ListMetricsOptions();
      IterableWithMarker<Metric> response = IterableWithMarkers.from(ImmutableSet.of(createMock(Metric.class)), null);
      
      expect(api.getMetricApiForRegion(null))
            .andReturn(metricApi)
            .atLeastOnce();

      expect(metricApi.list(options))
            .andReturn(response)
            .once();

      EasyMock.replay(api, metricApi);

      Assert.assertEquals(1, Iterables.size(CloudWatch.listMetrics(api, null, options)));
   }

   /**
    * Tests {@link CloudWatch#listMetrics(CloudWatchApi, String, org.jclouds.cloudwatch.options.ListMetricsOptions)}
    * where retrieving all results requires multiple requests.
    *
    * @throws Exception if anything goes wrong
    */
   @Test
   public void testMultiPageResult() throws Exception {
      CloudWatchApi api = createMock(CloudWatchApi.class);
      MetricApi metricApi = createMock(MetricApi.class);
      ListMetricsOptions options = new ListMetricsOptions();
      IterableWithMarker<Metric> response1 = IterableWithMarkers.from(ImmutableSet.of(createMock(Metric.class)), "NEXTTOKEN");
      IterableWithMarker<Metric> response2 = IterableWithMarkers.from(ImmutableSet.of(createMock(Metric.class)), null);

      // Using EasyMock.eq("") because EasyMock makes it impossible to pass null as a String value here
      expect(api.getMetricApiForRegion(EasyMock.eq("")))
            .andReturn(metricApi)
            .atLeastOnce();
      
      expect(metricApi.list(anyObject(ListMetricsOptions.class)))
            .andReturn(response1)
            .once();
      expect(metricApi.list(anyObject(ListMetricsOptions.class)))
            .andReturn(response2)
            .once();

      EasyMock.replay(api, metricApi);

      Assert.assertEquals(2, Iterables.size(CloudWatch.listMetrics(api, "", options)));
   }

   /**
    * Tests {@link CloudWatch#putMetricData(CloudWatchApi, String, Iterable, String)} where the set of metrics is
    * greater than 10.
    *
    * @throws Exception if anything goes wrong
    */
   @Test
   public void testPutMetricData() throws Exception {
      CloudWatchApi api = createMock(CloudWatchApi.class);
      MetricApi metricApi = createMock(MetricApi.class);
      Set<MetricDatum> metrics = Sets.newLinkedHashSet();
      String namespace = "JCLOUDS/Test";

      for (int i = 0; i < 11; i++) {
         metrics.add(MetricDatum.builder().metricName("foo").build());
      }

      // Using EasyMock.eq("") because EasyMock makes it impossible to pass null as a String value here
      expect(api.getMetricApiForRegion(EasyMock.eq("")))
            .andReturn(metricApi)
            .atLeastOnce();
      
      for (List<MetricDatum> slice : Iterables.partition(metrics, 10)) {
         metricApi.putMetricsInNamespace(slice, namespace);
      }

      EasyMock.replay(api, metricApi);

      CloudWatch.putMetricData(api, "", metrics, namespace);

      EasyMock.verify(metricApi);
   }

}
