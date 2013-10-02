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
package org.jclouds.cloudwatch.features;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.jclouds.util.Predicates2.retry;

import java.util.Date;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jclouds.cloudwatch.domain.Alarm;
import org.jclouds.cloudwatch.domain.AlarmHistoryItem;
import org.jclouds.cloudwatch.domain.ComparisonOperator;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.domain.StatisticValues;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.cloudwatch.internal.BaseCloudWatchApiLiveTest;
import org.jclouds.cloudwatch.options.ListAlarmHistoryOptions;
import org.jclouds.cloudwatch.options.ListAlarmsForMetric;
import org.jclouds.cloudwatch.options.ListAlarmsOptions;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.cloudwatch.options.SaveAlarmOptions;
import org.jclouds.collect.IterableWithMarker;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Jeremy Whitlock
 */
@Test(groups = "live", testName = "AlarmApiLiveTest")
public class AlarmApiLiveTest extends BaseCloudWatchApiLiveTest {

   private String alarmName = "TestAlarmName" + System.currentTimeMillis();
   private String metricName = "TestMetricForAlarms";
   private String namespace = "JCLOUDS/Test";

   @BeforeClass
   protected void beforeClass() throws Exception {
      IterableWithMarker<Metric> metrics = metricApi().list(new ListMetricsOptions().metricName(metricName));

      if (Iterables.size(metrics) == 0) {
         metricApi().putMetricsInNamespace(ImmutableSet.of(
               MetricDatum.builder()
                          .metricName(metricName)
                          .statisticValues(StatisticValues.builder()
                                                          .maximum(4.0)
                                                          .minimum(1.0)
                                                          .sampleCount(4.0)
                                                          .sum(10.0)
                                                          .build())
                          .dimension(new Dimension("BaseMetricName", metricName))
                          .dimension(new Dimension("TestDimension2", "TEST2"))
                          .unit(Unit.COUNT)
                          .timestamp(new Date())
                          .build()
         ), namespace);

         ListMetricsOptions lmo = ListMetricsOptions.Builder.namespace(namespace)
                                                    .metricName(metricName);
         boolean success = retry(new Predicate<ListMetricsOptions>() {
            public boolean apply(ListMetricsOptions options) {
               return Iterables.size(metricApi().list(options)) == 1;
            }
         }, 5, 1, MINUTES).apply(lmo);

         if (!success) {
            Assert.fail("Unable to create the test CloudWatch metric within the time (5m) allotted.");
         }
      }
   }

   @AfterClass
   protected void afterClass() throws Exception {
      IterableWithMarker<Alarm> alarms = api().list(new ListAlarmsOptions().alarmName(alarmName)).get(0);
      if (Iterables.size(alarms) > 0) {
         api().delete(ImmutableSet.of(alarmName));
      }
   }

   @Test
   protected void testAlarmCRUD() throws Exception {
      // Create new alarm
      api().save(new SaveAlarmOptions()
                       .actionsEnabled(true)
                       .alarmDescription("This is a test alarm for jclouds.")
                       .alarmName(alarmName)
                       .comparisonOperator(ComparisonOperator.GREATER_THAN_THRESHOLD)
                       .evaluationPeriods(5)
                       .metricName(metricName)
                       .namespace(namespace)
                       .period(60)
                       .statistic(Statistics.SAMPLE_COUNT)
                       .threshold(1.0));

      // Poll alarms until alarm is found
      ListAlarmsOptions dmo = new ListAlarmsOptions().alarmName(alarmName);
      boolean success = retry(new Predicate<ListAlarmsOptions>() {
         public boolean apply(ListAlarmsOptions options) {
            return Iterables.size(api().list(options).get(0)) == 1;
         }
      }, 5, 1, MINUTES).apply(dmo);

      if (!success) {
         Assert.fail("Unable to create the test CloudWatch alarm within the time (5m) allotted.");
      }

      // Poll all alarms until alarm is found
      success = retry(new Predicate<Void>() {
         public boolean apply(Void arg) {
            for (IterableWithMarker<Alarm> page : api().list()) {
               for (Alarm alarm : page) {
                  if (alarm.getAlarmName().equals(alarmName)) {
                     return true;
                  }
               }
            }
            return false;
         }
      }, 5, 1, MINUTES).apply(null);

      if (!success) {
         Assert.fail("Unable to create the test CloudWatch alarm within the time (5m) allotted.");
      }

      // Poll for alarms for metric until alarm is found
      ListAlarmsForMetric dafmo = new ListAlarmsForMetric()
            .metricName(metricName)
            .namespace(namespace);
      success = retry(new Predicate<ListAlarmsForMetric>() {
         public boolean apply(ListAlarmsForMetric options) {
            for (Alarm alarm : api().listForMetric(options)) {
               if (alarm.getAlarmName().equals(alarmName)) {
                  return true;
               }
            }
            return false;
         }
      }, 5, 1, MINUTES).apply(dafmo);

      if (!success) {
         Assert.fail("Unable to create the test CloudWatch alarm history item within the time (5m) allotted.");
      }

      // Create history item by changing its state
      api().setState(alarmName, "Updating the state.", null, Alarm.State.OK);

      // Poll for alarm history
      ListAlarmHistoryOptions daho = new ListAlarmHistoryOptions().alarmName(alarmName);
      success = retry(new Predicate<ListAlarmHistoryOptions>() {
         public boolean apply(ListAlarmHistoryOptions options) {
            for (IterableWithMarker<AlarmHistoryItem> page : api().listHistory(options)) {
               for (AlarmHistoryItem alarmHistoryItem : page) {
                  if (alarmHistoryItem.getAlarmName().equals(alarmName)) {
                     JsonObject historyData = new JsonParser().parse(alarmHistoryItem.getHistoryData())
                                                              .getAsJsonObject();

                     if (historyData.has("newState") && historyData.getAsJsonObject("newState").has("stateReason") &&
                           historyData.getAsJsonObject("newState").get("stateReason").getAsString()
                                      .equals("Updating the state.")) {
                        return true;
                     }
                  }
               }
            }
            return false;
         }
      }, 5, 1, MINUTES).apply(daho);

      if (!success) {
         Assert.fail("Unable to create the test CloudWatch alarm history item within the time (5m) allotted.");
      }

      success = retry(new Predicate<Void>() {
         public boolean apply(Void arg) {
            for (IterableWithMarker<AlarmHistoryItem> page : api().listHistory()) {
               for (AlarmHistoryItem alarmHistoryItem : page) {
                  if (alarmHistoryItem.getAlarmName().equals(alarmName)) {
                     JsonObject historyData = new JsonParser().parse(alarmHistoryItem.getHistoryData())
                                                              .getAsJsonObject();

                     if (historyData.has("newState") && historyData.getAsJsonObject("newState").has("stateReason") &&
                           historyData.getAsJsonObject("newState").get("stateReason").getAsString()
                                      .equals("Updating the state.")) {
                        return true;
                     }
                  }
               }
            }
            return false;
         }
      }, 5, 1, MINUTES).apply(null);

      if (!success) {
         Assert.fail("Unable to create the test CloudWatch alarm history item within the time (5m) allotted.");
      }

      // Disable alarm actions
      api().disable(ImmutableSet.of(alarmName));

      // Validate
      success = retry(new Predicate<ListAlarmsOptions>() {
         public boolean apply(ListAlarmsOptions options) {
            Iterable<Alarm> alarms = api().list(options).get(0);
            return Iterables.size(alarms) == 1 && !alarms.iterator().next().areActionsEnabled();
         }
      }, 5, 1, MINUTES).apply(dmo);

      if (!success) {
         Assert.fail("Unable to validate the test CloudWatch alarm disablement within the time (5m) allotted.");
      }

      // Enable alarm actions
      api().enable(ImmutableSet.of(alarmName));

      // Validate
      success = retry(new Predicate<ListAlarmsOptions>() {
         public boolean apply(ListAlarmsOptions options) {
            IterableWithMarker<Alarm> alarms = api().list(options).get(0);
            return Iterables.size(alarms) == 1 && alarms.iterator().next().areActionsEnabled();
         }
      }, 5, 1, MINUTES).apply(dmo);

      if (!success) {
         Assert.fail("Unable to validate the test CloudWatch alarm enablement within the time (5m) allotted.");
      }

      // Delete the alarm
      api().delete(ImmutableSet.of(alarmName));

      success = retry(new Predicate<ListAlarmsOptions>() {
         public boolean apply(ListAlarmsOptions options) {
            return Iterables.size(api().list(options).get(0)) == 0;
         }
      }, 5, 1, MINUTES).apply(dmo);

      if (!success) {
         Assert.fail("Unable to delete the test CloudWatch alarm within the time (5m) allotted.");
      }
   }

   protected AlarmApi api() {
      return api.getAlarmApiForRegion(null);
   }

   protected MetricApi metricApi() {
      return api.getMetricApiForRegion(null);
   }

}
