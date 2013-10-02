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
import org.jclouds.cloudwatch.domain.Alarm;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ListAlarmsOptions}.
 *
 * @author Jeremy Whitlock
 */
@Test(groups = "unit")
public class ListAlarmsOptionsTest {

   public void testEmptyOptions() throws Exception {
      Multimap<String, String> formParameters = new ListAlarmsOptions().buildFormParameters();

      assertFalse(formParameters.containsKey("ActionPrefix"));
      assertFalse(formParameters.containsKey("AlarmNamePrefix"));
      assertFalse(formParameters.containsKey("AlarmNames.member.1"));
      assertFalse(formParameters.containsKey("MaxRecords"));
      assertFalse(formParameters.containsKey("NextToken"));
      assertFalse(formParameters.containsKey("StateValue"));
   }

   public void testPopulatedOptions() throws Exception {
      String actionPrefix = "TestActionPrefix";
      String alarmNamePrefix = "TestAlarmNamePrefix";
      Set<String> alarmNames = ImmutableSet.of(
            "TestAlarmName1",
            "TestAlarmName2"
      );
      int maxRecords = 5;
      Alarm.State state = Alarm.State.ALARM;
      int alarmNameIndex = 1;
      Multimap<String, String> formParameters = new ListAlarmsOptions()
            .actionPrefix(actionPrefix)
            .alarmNamePrefix(alarmNamePrefix)
            .alarmNames(alarmNames)
            .maxRecords(maxRecords)
            .state(state)
            .buildFormParameters();

      assertEquals(formParameters.get("ActionPrefix"), ImmutableSet.of(actionPrefix));
      assertEquals(formParameters.get("AlarmNamePrefix"), ImmutableSet.of(alarmNamePrefix));
      assertEquals(formParameters.get("MaxRecords"), ImmutableSet.of(Integer.toString(maxRecords)));
      assertEquals(formParameters.get("StateValue"), ImmutableSet.of(state.toString()));

      for (String alarmName : alarmNames) {
         assertEquals(formParameters.get("AlarmNames.member." + alarmNameIndex), ImmutableSet.of(alarmName));
         alarmNameIndex++;
      }
   }

}
