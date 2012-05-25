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
package org.jclouds.cloudwatch.binders;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.Assert;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.domain.PutMetricData;
import org.jclouds.cloudwatch.domain.StatisticSet;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Date;

/**
 * Tests behavior of {@link PutMetricDataBinder}.
 *
 * @author Jeremy Whitlock
 */
@Test(groups = "unit")
public class PutMetricDataBinderTest {

   Injector injector = Guice.createInjector();
   PutMetricDataBinder binder = injector.getInstance(PutMetricDataBinder.class);
   HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();

   public void testMetricWithoutTimestamp() throws Exception {
      StatisticSet ss = StatisticSet.builder()
                                    .maximum(4.0)
                                    .minimum(1.0)
                                    .sampleCount(4.0)
                                    .sum(10.0)
                                    .build();
      MetricDatum metricDatum = MetricDatum.builder()
                                           .metricName("TestMetricName")
                                           .statisticSet(ss)
                                           .dimension(new Dimension("TestDimension", "FAKE"))
                                           .unit(Unit.COUNT)
                                           .build();
      PutMetricData pmd = PutMetricData.builder()
                                       .metricDatum(metricDatum)
                                       .namespace("JCLOUDS/Test")
                                       .build();

      request = binder.bindToRequest(request, pmd);

      Assert.assertEquals(request.getPayload().getRawContent(),
                          new StringBuilder()
                                .append("Namespace=JCLOUDS%2FTest")
                                .append("&MetricData.member.1.Dimensions.member.1.Name=TestDimension")
                                .append("&MetricData.member.1.Dimensions.member.1.Value=FAKE")
                                .append("&MetricData.member.1.MetricName=TestMetricName")
                                .append("&MetricData.member.1.StatisticValues.Maximum=4.0")
                                .append("&MetricData.member.1.StatisticValues.Minimum=1.0")
                                .append("&MetricData.member.1.StatisticValues.SampleCount=4.0")
                                .append("&MetricData.member.1.StatisticValues.Sum=10.0")
                                .append("&MetricData.member.1.Unit=")
                                .append(Unit.COUNT.toString())
                                .toString());
   }

   public void testMetricWithMultipleDimensions() throws Exception {
      MetricDatum metricDatum = MetricDatum.builder()
                                           .metricName("TestMetricName")
                                           .dimension(new Dimension("TestDimension", "FAKE"))
                                           .dimension(new Dimension("TestDimension2", "FAKE2"))
                                           .unit(Unit.COUNT)
                                           .timestamp(new Date(10000000l))
                                           .value(5.0)
                                           .build();
      PutMetricData pmd = PutMetricData.builder()
                                       .metricDatum(metricDatum)
                                       .namespace("JCLOUDS/Test")
                                       .build();

      request = binder.bindToRequest(request, pmd);

      Assert.assertEquals(request.getPayload().getRawContent(),
                          new StringBuilder()
                                .append("Namespace=JCLOUDS%2FTest")
                                .append("&MetricData.member.1.Dimensions.member.1.Name=TestDimension")
                                .append("&MetricData.member.1.Dimensions.member.1.Value=FAKE")
                                .append("&MetricData.member.1.Dimensions.member.2.Name=TestDimension2")
                                .append("&MetricData.member.1.Dimensions.member.2.Value=FAKE2")
                                .append("&MetricData.member.1.MetricName=TestMetricName")
                                .append("&MetricData.member.1.Timestamp=1970-01-01T02%3A46%3A40Z")
                                .append("&MetricData.member.1.Unit=")
                                .append(Unit.COUNT.toString())
                                .append("&MetricData.member.1.Value=5.0")
                                .toString());
   }

   public void testMetricWithMultipleDatum() throws Exception {
      StatisticSet ss = StatisticSet.builder()
                                    .maximum(4.0)
                                    .minimum(1.0)
                                    .sampleCount(4.0)
                                    .sum(10.0)
                                    .build();
      MetricDatum metricDatum = MetricDatum.builder()
                                           .metricName("TestMetricName")
                                           .statisticSet(ss)
                                           .dimension(new Dimension("TestDimension", "FAKE"))
                                           .dimension(new Dimension("TestDimension2", "FAKE2"))
                                           .unit(Unit.COUNT)
                                           .timestamp(new Date(10000000l))
                                           .build();
      MetricDatum metricDatum2 = MetricDatum.builder()
                                           .metricName("TestMetricName")
                                           .dimension(new Dimension("TestDimension", "FAKE"))
                                           .unit(Unit.COUNT)
                                           .timestamp(new Date(10000000l))
                                           .value(5.0)
                                           .build();
      PutMetricData pmd = PutMetricData.builder()
                                       .metricData(ImmutableSet.of(metricDatum, metricDatum2))
                                       .namespace("JCLOUDS/Test")
                                       .build();

      request = binder.bindToRequest(request, pmd);

      Assert.assertEquals(request.getPayload().getRawContent(),
                          new StringBuilder()
                                .append("Namespace=JCLOUDS%2FTest")
                                .append("&MetricData.member.1.Dimensions.member.1.Name=TestDimension")
                                .append("&MetricData.member.1.Dimensions.member.1.Value=FAKE")
                                .append("&MetricData.member.1.Dimensions.member.2.Name=TestDimension2")
                                .append("&MetricData.member.1.Dimensions.member.2.Value=FAKE2")
                                .append("&MetricData.member.1.MetricName=TestMetricName")
                                .append("&MetricData.member.1.StatisticValues.Maximum=4.0")
                                .append("&MetricData.member.1.StatisticValues.Minimum=1.0")
                                .append("&MetricData.member.1.StatisticValues.SampleCount=4.0")
                                .append("&MetricData.member.1.StatisticValues.Sum=10.0")
                                .append("&MetricData.member.1.Timestamp=1970-01-01T02%3A46%3A40Z")
                                .append("&MetricData.member.1.Unit=")
                                .append(Unit.COUNT.toString())
                                .append("&MetricData.member.2.Dimensions.member.1.Name=TestDimension")
                                .append("&MetricData.member.2.Dimensions.member.1.Value=FAKE")
                                .append("&MetricData.member.2.MetricName=TestMetricName")
                                .append("&MetricData.member.2.Timestamp=1970-01-01T02%3A46%3A40Z")
                                .append("&MetricData.member.2.Unit=")
                                .append(Unit.COUNT.toString())
                                .append("&MetricData.member.2.Value=5.0")
                                .toString());
   }

}
