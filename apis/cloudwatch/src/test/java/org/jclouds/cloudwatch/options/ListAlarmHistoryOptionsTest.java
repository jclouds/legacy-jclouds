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

import java.util.Date;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.jclouds.cloudwatch.domain.HistoryItemType;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ListAlarmHistoryOptions}.
 *
 * @author Jeremy Whitlock
 */
@Test(groups = "unit")
public class ListAlarmHistoryOptionsTest {

   private DateService dateService = new SimpleDateFormatDateService();

   public void testEmptyOptions() throws Exception {
      Multimap<String, String> formParameters = new ListAlarmHistoryOptions().buildFormParameters();

      assertFalse(formParameters.containsKey("AlarmName"));
      assertFalse(formParameters.containsKey("EndDate"));
      assertFalse(formParameters.containsKey("HistoryItemType"));
      assertFalse(formParameters.containsKey("MaxRecords"));
      assertFalse(formParameters.containsKey("NextToken"));
      assertFalse(formParameters.containsKey("StartDate"));
   }

   public void testPopulatedOptions() throws Exception {
      String alarmName = "TestAlarmName";
      Date endDate = new Date(new Date().getTime() + 5000);
      HistoryItemType historyItemType = HistoryItemType.ACTION;
      int maxRecords = 5;
      Date startDate = new Date();
      Multimap<String, String> formParameters = new ListAlarmHistoryOptions()
            .alarmName(alarmName)
            .endDate(endDate)
            .historyItemType(historyItemType)
            .maxRecords(maxRecords)
            .startDate(startDate)
            .buildFormParameters();

      assertEquals(formParameters.get("AlarmName"), ImmutableSet.of(alarmName));
      assertEquals(formParameters.get("EndDate"), ImmutableSet.of(dateService.iso8601DateFormat(endDate)));
      assertEquals(formParameters.get("HistoryItemType"), ImmutableSet.of(historyItemType.toString()));
      assertEquals(formParameters.get("MaxRecords"), ImmutableSet.of(Integer.toString(maxRecords)));
      assertEquals(formParameters.get("StartDate"), ImmutableSet.of(dateService.iso8601DateFormat(startDate)));
   }

}
