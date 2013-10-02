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
package org.jclouds.cloudwatch.xml;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudwatch.domain.Alarm;
import org.jclouds.cloudwatch.domain.ComparisonOperator;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.Namespaces;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ListAlarmsForMetricResponseHandler}.  Implicitly tests behavior of
 * {@code MetricAlarmHandler}.
 *
 * @author Jeremy Whitlock
 */
// NOTE: Without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ListAlarmsForMetricResponseHandlerTest")
public class ListAlarmsForMetricResponseHandlerTest extends BaseHandlerTest {

   private final DateService dateService = new SimpleDateFormatDateService();

   /**
    * Tests parsing all possible XML elements that could be encountered by {@link ListAlarmsForMetricResponseHandler}.
    *
    * @throws Exception if something goes wrong
    */
   public void testParseFullResponse() throws Exception {
      Iterable<Alarm> metricAlarms =
            factory.create(injector.getInstance(ListAlarmsForMetricResponseHandler.class))
                   .parse(getClass().getResourceAsStream("/DescribeAlarmsForMetricResponse.xml"));

      assertEquals(metricAlarms.toString(), expected().toString());
   }

   public Iterable<Alarm> expected() {
      Set<Alarm> alarms = Sets.newLinkedHashSet();

      for (int i = 1; i <= 2; i++) {
         alarms.add(new Alarm(
               i == 1,
               ImmutableSet.of("TestAction1", "TestAction2"),
               "TestAlarmARN" + i,
               dateService.iso8601SecondsDateParse("2013-01-0" + i + "T00:00:00Z"),
               "This is test alarm " + i + ".",
               "TestAlarmName" + i,
               i == 1 ?
                     ComparisonOperator.GREATER_THAN_THRESHOLD :
                     ComparisonOperator.LESS_THAN_THRESHOLD,
               ImmutableSet.of(
                     new Dimension("TestDimensionName1", "TestDimensionValue1"),
                     new Dimension("TestDimensionName2", "TestDimensionValue2")
               ),
               60 * i,
               ImmutableSet.of("TestAction1", "TestAction2"),
               "TestMetricName" + i,
               Namespaces.EC2,
               ImmutableSet.of("TestAction1", "TestAction2"),
               60 * i,
               "This is state reason " + i + ".",
               Optional.of("{\"reason\": \"" + i + "\"}"),
               dateService.iso8601SecondsDateParse("2013-01-0" + i + "T00:00:00Z"),
               i == 1 ?
                     Alarm.State.INSUFFICIENT_DATA :
                     Alarm.State.OK,
               i == 1 ?
                     Statistics.SAMPLE_COUNT :
                     Statistics.AVERAGE,
               Double.valueOf(Integer.toString(i)),
               i == 1 ?
                     Optional.of(Unit.SECONDS) :
                     Optional.of(Unit.COUNT_PER_SECOND)
         ));
      }

      return alarms;
   }

}
