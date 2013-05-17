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
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.Namespaces;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ListAlarmsForMetric}.
 *
 * @author Jeremy Whitlock
 */
@Test(groups = "unit")
public class ListAlarmsForMetricTest {

   public void testEmptyOptions() throws Exception {
      Multimap<String, String> formParameters = new ListAlarmsForMetric().buildFormParameters();

      assertFalse(formParameters.containsKey("Dimensions.member.1.Name"));
      assertFalse(formParameters.containsKey("Dimensions.member.1.Value"));
      assertFalse(formParameters.containsKey("MetricName"));
      assertFalse(formParameters.containsKey("Namespace"));
      assertFalse(formParameters.containsKey("Period"));
      assertFalse(formParameters.containsKey("Statistic"));
      assertFalse(formParameters.containsKey("Unit"));
   }

   public void testPopulatedOptions() throws Exception {
      Set<Dimension> dimensions = ImmutableSet.of(
         new Dimension("TestDimension1", "TestValue1"),
         new Dimension("TestDimension2", "TestValue2")
      );
      String metricName = "TestMetricName";
      String namespace = Namespaces.EC2;
      int period = 60;
      Statistics statistic = Statistics.AVERAGE;
      Unit unit = Unit.COUNT;
      Multimap<String, String> formParameters = new ListAlarmsForMetric()
            .dimensions(dimensions)
            .metricName(metricName)
            .namespace(namespace)
            .period(period)
            .statistic(statistic)
            .unit(unit)
            .buildFormParameters();
      int dimensionIndex = 1;

      for (Dimension dimension : dimensions) {
         assertEquals(formParameters.get("Dimensions.member." + dimensionIndex + ".Name"),
                      ImmutableSet.of(dimension.getName()));
         assertEquals(formParameters.get("Dimensions.member." + dimensionIndex + ".Value"),
                      ImmutableSet.of(dimension.getValue()));
         dimensionIndex++;
      }

      assertEquals(formParameters.get("MetricName"), ImmutableSet.of(metricName));
      assertEquals(formParameters.get("Namespace"), ImmutableSet.of(namespace));
      assertEquals(formParameters.get("Period"), ImmutableSet.of(Integer.toString(period)));
      assertEquals(formParameters.get("Statistic"), ImmutableSet.of(statistic.toString()));
      assertEquals(formParameters.get("Unit"), ImmutableSet.of(unit.toString()));
   }

}
