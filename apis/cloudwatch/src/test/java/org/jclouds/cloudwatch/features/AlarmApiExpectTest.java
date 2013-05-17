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

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.jclouds.cloudwatch.CloudWatchApi;
import org.jclouds.cloudwatch.domain.Alarm;
import org.jclouds.cloudwatch.domain.ComparisonOperator;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.HistoryItemType;
import org.jclouds.cloudwatch.domain.Namespaces;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.cloudwatch.internal.BaseCloudWatchApiExpectTest;
import org.jclouds.cloudwatch.options.ListAlarmHistoryOptions;
import org.jclouds.cloudwatch.options.ListAlarmsForMetric;
import org.jclouds.cloudwatch.options.ListAlarmsOptions;
import org.jclouds.cloudwatch.options.SaveAlarmOptions;
import org.jclouds.cloudwatch.xml.ListAlarmHistoryResponseHandlerTest;
import org.jclouds.cloudwatch.xml.ListAlarmsForMetricResponseHandlerTest;
import org.jclouds.cloudwatch.xml.ListAlarmsResponseHandlerTest;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

/**
 * @author Jeremy Whitlock
 */
@Test(groups = "unit", testName = "AlarmApiExpectTest")
public class AlarmApiExpectTest extends BaseCloudWatchApiExpectTest {

   private DateService dateService = new SimpleDateFormatDateService();
   private HttpRequest deleteAlarmsRequest = alarmRequest(ImmutableMap.of(
         "Action", "DeleteAlarms",
         "AlarmNames.member.1", "TestAlarmName1",
         "AlarmNames.member.2", "TestAlarmName2",
         "Signature", "w9dhEBQCsmOhYKDp9Ht7/Ra6/xZcgAzhF0Bgtj8vpwQ%3D"
   ));
   private HttpRequest putMetricAlarmRequest = alarmRequest(
         ImmutableMap.<String, String>builder()
                     .put("Action", "PutMetricAlarm")
                     .put("ActionsEnabled", "true")
                     .put("AlarmActions.member.1", "TestAlarmAction1")
                     .put("AlarmActions.member.2", "TestAlarmAction2")
                     .put("AlarmDescription", "Test%20alarm%20description.")
                     .put("AlarmName", "TestAlarmName")
                     .put("ComparisonOperator", ComparisonOperator.GREATER_THAN_THRESHOLD.toString())
                     .put("Dimensions.member.1.Name", "TestDimensionName1")
                     .put("Dimensions.member.1.Value", "TestDimensionValue1")
                     .put("Dimensions.member.2.Name", "TestDimensionName2")
                     .put("Dimensions.member.2.Value", "TestDimensionValue2")
                     .put("EvaluationPeriods", "60")
                     .put("InsufficientDataActions.member.1", "TestAlarmAction1")
                     .put("InsufficientDataActions.member.2", "TestAlarmAction2")
                     .put("MetricName", "TestMetricName")
                     .put("Namespace", Namespaces.EBS)
                     .put("OKActions.member.1", "TestAlarmAction1")
                     .put("OKActions.member.2", "TestAlarmAction2")
                     .put("Period", "60")
                     .put("Statistic", Statistics.SAMPLE_COUNT.toString())
                     .put("Threshold", "1.0")
                     .put("Unit", Unit.GIGABYTES_PER_SECOND.toString())
                     .put("Signature", "6RXD%2Bp1393a0maPdMLn%2Bv%2BbIcOJnAViAtbMgcA%2BogWs%3D").build()
   );
   private SaveAlarmOptions saveAlarmOptions = new SaveAlarmOptions()
         .alarmActions(ImmutableSet.of("TestAlarmAction1", "TestAlarmAction2"))
         .alarmDescription("Test alarm description.")
         .alarmName("TestAlarmName")
         .metricName("TestMetricName")
         .actionsEnabled(true)
         .comparisonOperator(ComparisonOperator.GREATER_THAN_THRESHOLD)
         .dimensions(ImmutableSet.of(
               new Dimension("TestDimensionName1",
                             "TestDimensionValue1"),
               new Dimension("TestDimensionName2",
                             "TestDimensionValue2")
         ))
         .evaluationPeriods(60)
         .insufficientDataActions(ImmutableSet.of("TestAlarmAction1", "TestAlarmAction2"))
         .namespace(Namespaces.EBS)
         .okActions(ImmutableSet.of("TestAlarmAction1", "TestAlarmAction2"))
         .period(60)
         .statistic(Statistics.SAMPLE_COUNT)
         .threshold(1.0)
         .unit(Unit.GIGABYTES_PER_SECOND);
   private HttpRequest setAlarmStateRequest = alarmRequest(
         ImmutableMap.<String, String>builder()
                     .put("Action", "SetAlarmState")
                     .put("AlarmName", "TestAlarmName")
                     .put("StateReason", "TestStateReason")
                     .put("StateReasonData", "%7B%22reason%22%3A%20%22Some%20reason%22%7D")
                     .put("StateValue", Alarm.State.OK.toString())
                     .put("Signature", "W3juJzJEoSTPfYnrK7s/Pbj4uA/PGNF7eoxa7NhByqU%3D").build()
   );

   public AlarmApiExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }

   public void testSetAlarmStateIs2xx() throws Exception {
      Payload payload = payloadFromResourceWithContentType("/VoidResponse.xml", "text/xml");
      CloudWatchApi cloudWatchApi = requestSendsResponse(setAlarmStateRequest,
                                                         HttpResponse.builder()
                                                                     .payload(payload)
                                                                     .statusCode(200)
                                                                     .build());

      // Ensure there is no error returned
      cloudWatchApi.getAlarmApiForRegion(null).setState("TestAlarmName", "TestStateReason",
                                                        "{\"reason\": \"Some reason\"}",
                                                        Alarm.State.OK);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testSetAlarmStateIs400() throws Exception {
      Payload payload = payloadFromResourceWithContentType("/InvalidFormatResponse.xml", "text/xml");
      CloudWatchApi cloudWatchApi = requestSendsResponse(setAlarmStateRequest,
                                                         HttpResponse.builder()
                                                                     .payload(payload)
                                                                     .statusCode(400)
                                                                     .build());

      // Ensure an IllegalArgumentException is thrown
      cloudWatchApi.getAlarmApiForRegion(null).setState("TestAlarmName", "TestStateReason",
                                                        "{\"reason\": \"Some reason\"}",
                                                        Alarm.State.OK);
   }

   public void testPutMetricAlarmIs2xx() throws Exception {
      Payload payload = payloadFromResourceWithContentType("/VoidResponse.xml", "text/xml");
      CloudWatchApi cloudWatchApi = requestSendsResponse(putMetricAlarmRequest,
                                                         HttpResponse.builder()
                                                                     .statusCode(200)
                                                                     .payload(payload)
                                                                     .build());

      // Ensure there is no error returned
      cloudWatchApi.getAlarmApiForRegion(null).save(saveAlarmOptions);
   }

   @Test(expectedExceptions = InsufficientResourcesException.class)
   public void testPutMetricAlarmIs400() throws Exception {
      Payload payload = payloadFromResourceWithContentType("/LimitExceededResponse.xml", "text/xml");
      CloudWatchApi cloudWatchApi = requestSendsResponse(putMetricAlarmRequest,
                                                         HttpResponse.builder()
                                                                     .payload(payload)
                                                                     .statusCode(400)
                                                                     .build());

      // Ensure an InsufficientResourcesException is thrown
      cloudWatchApi.getAlarmApiForRegion(null).save(saveAlarmOptions);
   }

   public void testEnableAlarmActions() throws Exception {
      CloudWatchApi cloudWatchApi = requestSendsResponse(alarmRequest(ImmutableMap.of(
            "Action", "EnableAlarmActions",
            "AlarmNames.member.1", "TestAlarmName1",
            "AlarmNames.member.2", "TestAlarmName2",
            "Signature", "Q1VemnXpc57PKMs9NVCX6R%2B/TSDgsGzQwpOHQ70aJuU%3D"
      )), HttpResponse.builder()
                      .statusCode(200)
                      .payload(payloadFromResourceWithContentType("/VoidResponse.xml", "text/xml"))
                      .build());

      // Ensure there is no error returned
      cloudWatchApi.getAlarmApiForRegion(null).enable(ImmutableSet.of(
            "TestAlarmName1",
            "TestAlarmName2"
      ));
   }

   public void testDisableAlarmActions() throws Exception {
      CloudWatchApi cloudWatchApi = requestSendsResponse(alarmRequest(ImmutableMap.of(
            "Action", "DisableAlarmActions",
            "AlarmNames.member.1", "TestAlarmName1",
            "AlarmNames.member.2", "TestAlarmName2",
            "Signature", "tvSfJ%2BgcrHowwUECSniV0TQP2OObpWCuba0S5dd723Y%3D"
      )), HttpResponse.builder()
                      .statusCode(200)
                      .payload(payloadFromResourceWithContentType("/VoidResponse.xml", "text/xml"))
                      .build());

      // Ensure there is no error returned
      cloudWatchApi.getAlarmApiForRegion(null).disable(ImmutableSet.of(
            "TestAlarmName1",
            "TestAlarmName2"
      ));
   }

   public void testDescribeAlarmsForMetric() throws Exception {
      String metricName = "TestMetricName";
      String namespace = Namespaces.EC2;
      int period = 60;
      Statistics statistics = Statistics.SAMPLE_COUNT;
      Unit unit = Unit.SECONDS;
      CloudWatchApi cloudWatchApi = requestSendsResponse(
            alarmRequest(ImmutableMap.<String, String>builder()
                                     .put("Action", "DescribeAlarmsForMetric")
                                     .put("Dimensions.member.1.Name", "TestDimensionName1")
                                     .put("Dimensions.member.1.Value", "TestDimensionValue1")
                                     .put("Dimensions.member.2.Name", "TestDimensionName2")
                                     .put("Dimensions.member.2.Value", "TestDimensionValue2")
                                     .put("MetricName", metricName)
                                     .put("Namespace", namespace)
                                     .put("Period", Integer.toString(period))
                                     .put("Statistic", statistics.toString())
                                     .put("Unit", unit.toString())
                                     .put("Signature", "y%2BpU0Lp6AAO2QSrNld1VQY4DhKVHcyn44dIfnrmJhpg%3D")
                                     .build()),
            HttpResponse.builder()
                        .statusCode(200)
                        .payload(payloadFromResourceWithContentType("/DescribeAlarmsForMetricResponse.xml", "text/xml"))
                        .build()
      );

      assertEquals(new ListAlarmsForMetricResponseHandlerTest().expected().toString(),
                   cloudWatchApi.getAlarmApiForRegion(null)
                                .listForMetric(new ListAlarmsForMetric()
                                                     .dimensions(ImmutableSet.of(
                                                           new Dimension("TestDimensionName1",
                                                                         "TestDimensionValue1"),
                                                           new Dimension("TestDimensionName2",
                                                                         "TestDimensionValue2")
                                                     ))
                                                     .metricName(metricName)
                                                     .namespace(namespace)
                                                     .period(period)
                                                     .statistic(statistics)
                                                     .unit(unit)
                                ).toString());
   }

   public void testDescribeAlarms() throws Exception {
      String actionPrefix = "TestActionPrefix";
      String alarmNamePrefix = "TestAlarmNamePrefix";
      Set<String> alarmNames = ImmutableSet.of("TestAlarmName1", "TestAlarmName2");
      int maxRecords = 10;
      Alarm.State state = Alarm.State.ALARM;
      CloudWatchApi cloudWatchApi = requestSendsResponse(
            alarmRequest(ImmutableMap.<String, String>builder()
                                     .put("Action", "DescribeAlarms")
                                     .put("ActionPrefix", actionPrefix)
                                     .put("AlarmNamePrefix", alarmNamePrefix)
                                     .put("AlarmNames.member.1", "TestAlarmName1")
                                     .put("AlarmNames.member.2", "TestAlarmName2")
                                     .put("MaxRecords", Integer.toString(maxRecords))
                                     .put("StateValue", state.toString())
                                     .put("Signature", "jPP1enbHPfOphIZv796W0KN4/EsLdPp6nK1qpbt5Dog%3D")
                                     .build()),
            HttpResponse.builder()
                        .statusCode(200)
                        .payload(payloadFromResourceWithContentType("/DescribeAlarmsResponse.xml", "text/xml"))
                        .build()
            );

      assertEquals(new ListAlarmsResponseHandlerTest().expected().toString(),
                   cloudWatchApi.getAlarmApiForRegion(null)
                                .list(new ListAlarmsOptions()
                                              .actionPrefix(actionPrefix)
                                              .alarmNamePrefix(alarmNamePrefix)
                                              .alarmNames(alarmNames)
                                              .maxRecords(maxRecords)
                                              .state(state)
                                ).get(0).toString());
   }

   public void testDescribeAlarmHistory() throws Exception {
      String alarmName = "TestAlarmName";
      HistoryItemType historyItemType = HistoryItemType.ACTION;
      int maxRecords = 10;
      String endDateStr = "2013-01-02T00:00:00.000Z";
      String startDateStr = "2013-01-01T00:00:00.000Z";
      CloudWatchApi cloudWatchApi = requestSendsResponse(
            alarmRequest(ImmutableMap.<String, String> builder()
                                     .put("Action", "DescribeAlarmHistory")
                                     .put("AlarmName", alarmName)
                                     .put("EndDate", "2013-01-02T00%3A00%3A00.000Z")
                                     .put("HistoryItemType", historyItemType.toString())
                                     .put("MaxRecords", Integer.toString(maxRecords))
                                     .put("StartDate", "2013-01-01T00%3A00%3A00.000Z")
                                     .put("Signature", "O2u9yIQvCuVpKdUeUDJcswri0YD0sD4%2B/SR5TtYbPeQ%3D")
                                     .build()),
            HttpResponse.builder()
                        .statusCode(200)
                        .payload(payloadFromResourceWithContentType("/DescribeAlarmHistoryResponse.xml", "text/xml"))
                        .build());

      assertEquals(new ListAlarmHistoryResponseHandlerTest().expected().toString(),
                   cloudWatchApi.getAlarmApiForRegion(null)
                                .listHistory(new ListAlarmHistoryOptions()
                                                   .alarmName("TestAlarmName")
                                                   .endDate(dateService.iso8601DateParse(endDateStr))
                                                   .historyItemType(HistoryItemType.ACTION)
                                                   .maxRecords(maxRecords)
                                                   .startDate(dateService.iso8601DateParse(startDateStr))
                                ).get(0).toString());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testDeleteAlarmsIs404() throws Exception {
      CloudWatchApi cloudWatchApi = requestSendsResponse(deleteAlarmsRequest,
                                                         HttpResponse.builder().statusCode(404).build());

      // Ensure a ResourceNotFoundException is thrown
      cloudWatchApi.getAlarmApiForRegion(null).delete(ImmutableSet.of(
            "TestAlarmName1",
            "TestAlarmName2"
      ));
   }

   public void testDeleteAlarmsIs2xx() throws Exception {
      Payload payload = payloadFromResourceWithContentType("/VoidResponse.xml", "text/xml");
      CloudWatchApi cloudWatchApi = requestSendsResponse(deleteAlarmsRequest,
                                                         HttpResponse.builder()
                                                                     .statusCode(200)
                                                                     .payload(payload)
                                                                     .build());

      // Ensure there is no error returned
      cloudWatchApi.getAlarmApiForRegion(null).delete(ImmutableSet.of(
            "TestAlarmName1",
            "TestAlarmName2"
      ));
   }

   private HttpRequest alarmRequest(Map<String, String> arguments) {
      Map<String, String> sortedArguments = Maps.newTreeMap();
      Map<String, String> defaultArguments =
            ImmutableMap.<String, String> builder()
                        .put("SignatureMethod", "HmacSHA256")
                        .put("SignatureVersion", "2")
                        .put("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                        .put("Version", "2010-08-01")
                        .build();

      sortedArguments.putAll(arguments);

      for (Map.Entry<String, String> defaultArgument : defaultArguments.entrySet()) {
         if (!sortedArguments.containsKey(defaultArgument.getKey())) {
            sortedArguments.put(defaultArgument.getKey(), defaultArgument.getValue());
         }
      }

      Map<String, String> realArguments = Maps.newLinkedHashMap(sortedArguments);

      realArguments.put("AWSAccessKeyId", "identity");

      return HttpRequest.builder()
                        .method("POST")
                        .endpoint("https://monitoring.us-east-1.amazonaws.com/")
                        .addHeader("Host", "monitoring.us-east-1.amazonaws.com")
                        .payload(payloadFromStringWithContentType(Joiner.on("&")
                                                                        .withKeyValueSeparator("=")
                                                                        .join(realArguments).trim(),
                                                                  "application/x-www-form-urlencoded"))
                        .build();
   }

}
