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
package org.jclouds.cloudwatch.options;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.jclouds.cloudwatch.domain.ComparisonOperator;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.Namespaces;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code SaveAlarmOptions}.
 *
 * @author Jeremy Whitlock
 */
@Test(groups = "unit")
public class SaveAlarmOptionsTest {

   public void testEmptyOptions() throws Exception {
      Multimap<String, String> formParameters = new SaveAlarmOptions().buildFormParameters();

      assertFalse(formParameters.containsKey("ActionsEnabled"));
      assertFalse(formParameters.containsKey("AlarmActions.member.1"));
      assertFalse(formParameters.containsKey("AlarmDescription"));
      assertFalse(formParameters.containsKey("AlarmName"));
      assertFalse(formParameters.containsKey("ComparisonOperator"));
      assertFalse(formParameters.containsKey("Dimensions.member.1.Name"));
      assertFalse(formParameters.containsKey("Dimensions.member.1.Value"));
      assertFalse(formParameters.containsKey("EvaluationPeriods"));
      assertFalse(formParameters.containsKey("InsufficientDataActions.member.1"));
      assertFalse(formParameters.containsKey("MetricName"));
      assertFalse(formParameters.containsKey("Namespace"));
      assertFalse(formParameters.containsKey("OKActions.member.1"));
      assertFalse(formParameters.containsKey("Period"));
      assertFalse(formParameters.containsKey("Statistic"));
      assertFalse(formParameters.containsKey("Threshold"));
      assertFalse(formParameters.containsKey("Unit"));
   }

   public void testPopulatedOptions() throws Exception {
      boolean actionsEnabled = false;
      Set<String> alarmActions = ImmutableSet.of(
            "TestAlarmAction1",
            "TestAlarmAction2"
      );
      String alarmDescription = "TestAlarmDescription";
      String alarmName = "TestAlarmName";
      ComparisonOperator comparisonOperator = ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD;
      Set<Dimension> dimensions = ImmutableSet.of(
            new Dimension("TestDimension1", "TestValue1"),
            new Dimension("TestDimension2", "TestValue2")
      );
      int evaluationPeriods = 360;
      Set<String> insufficientDataActions = ImmutableSet.of(
            "TestInsufficientDataAction1",
            "TestInsufficientDataAction2"
      );
      String metricName = "TestMetricName";
      String namespace = Namespaces.AUTO_SCALING;
      Set<String> okActions = ImmutableSet.of(
            "TestOKAction1",
            "TestOKAction2"
      );
      int period = 300;
      Statistics statistic = Statistics.SAMPLE_COUNT;
      double threshold = 1.0;
      Unit unit = Unit.BITS;
      Multimap<String, String> formParameters = new SaveAlarmOptions()
            .actionsEnabled(actionsEnabled)
            .alarmActions(alarmActions)
            .alarmDescription(alarmDescription)
            .alarmName(alarmName)
            .comparisonOperator(comparisonOperator)
            .dimensions(dimensions)
            .evaluationPeriods(evaluationPeriods)
            .insufficientDataActions(insufficientDataActions)
            .metricName(metricName)
            .namespace(namespace)
            .okActions(okActions)
            .period(period)
            .statistic(statistic)
            .threshold(threshold)
            .unit(unit)
            .buildFormParameters();
      int alarmActionIndex = 1;
      int dimensionIndex = 1;
      int insufficientDataActionIndex = 1;
      int okActionIndex = 1;

      for (String alarmAction : alarmActions) {
         assertEquals(formParameters.get("AlarmActions.member." + alarmActionIndex), ImmutableSet.of(alarmAction));
         alarmActionIndex++;
      }

      for (Dimension dimension : dimensions) {
         assertEquals(formParameters.get("Dimensions.member." + dimensionIndex + ".Name"),
                      ImmutableSet.of(dimension.getName()));
         assertEquals(formParameters.get("Dimensions.member." + dimensionIndex + ".Value"),
                      ImmutableSet.of(dimension.getValue()));
         dimensionIndex++;
      }

      for (String insufficientDataAction : insufficientDataActions) {
         assertEquals(formParameters.get("InsufficientDataActions.member." + insufficientDataActionIndex),
                      ImmutableSet.of(insufficientDataAction));
         insufficientDataActionIndex++;
      }

      for (String okAction : okActions) {
         assertEquals(formParameters.get("OKActions.member." + okActionIndex), ImmutableSet.of(okAction));
         okActionIndex++;
      }

      assertEquals(formParameters.get("ActionsEnabled"), ImmutableSet.of(Boolean.toString(actionsEnabled)));
      assertEquals(formParameters.get("AlarmDescription"), ImmutableSet.of(alarmDescription));
      assertEquals(formParameters.get("AlarmName"), ImmutableSet.of(alarmName));
      assertEquals(formParameters.get("ComparisonOperator"), ImmutableSet.of(comparisonOperator.toString()));
      assertEquals(formParameters.get("EvaluationPeriods"), ImmutableSet.of(Integer.toString(evaluationPeriods)));
      assertEquals(formParameters.get("MetricName"), ImmutableSet.of(metricName));
      assertEquals(formParameters.get("Namespace"), ImmutableSet.of(namespace));
      assertEquals(formParameters.get("Period"), ImmutableSet.of(Integer.toString(period)));
      assertEquals(formParameters.get("Statistic"), ImmutableSet.of(statistic.toString()));
      assertEquals(formParameters.get("Threshold"), ImmutableSet.of(Double.toString(threshold)));
      assertEquals(formParameters.get("Unit"), ImmutableSet.of(unit.toString()));
   }

}