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
import static org.testng.Assert.assertFalse;

import java.util.Set;

import com.google.common.collect.Sets;
import org.jclouds.cloudwatch.domain.AlarmHistoryItem;
import org.jclouds.cloudwatch.domain.HistoryItemType;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ListAlarmHistoryResponseHandler}.  Implicitly tests behavior of
 * {@code MetricAlarmHandler}.
 *
 * @author Jeremy Whitlock
 */
// NOTE: Without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ListAlarmHistoryResponseHandlerTest")
public class ListAlarmHistoryResponseHandlerTest extends BaseHandlerTest {

   /**
    * Tests parsing all possible XML elements that could be encountered by {@link ListAlarmHistoryResponseHandler}.
    *
    * @throws Exception if something goes wrong
    */
   public void testParseFullResponse() throws Exception {
      IterableWithMarker<AlarmHistoryItem> alarmHistoryItems =
            factory.create(injector.getInstance(ListAlarmHistoryResponseHandler.class))
                   .parse(getClass().getResourceAsStream("/DescribeAlarmHistoryResponse.xml"));

      assertEquals(alarmHistoryItems.toString(), expected().toString());
      assertFalse(alarmHistoryItems.nextMarker().isPresent());
   }

   public IterableWithMarker<AlarmHistoryItem> expected() {
      Set<AlarmHistoryItem> alarmHistoryItems = Sets.newLinkedHashSet();

      for (int i = 1; i <= 2; i++) {
         alarmHistoryItems.add(new AlarmHistoryItem(
               "TestAlarmName" + i,
               "{\"reason\": \"" + i + "\"}",
               i == 1 ?
                     HistoryItemType.ACTION :
                     HistoryItemType.CONFIGURATION_UPDATE,
               "This is test " + i + ".",
               new SimpleDateFormatDateService()
                     .iso8601SecondsDateParse("2013-01-0" + i + "T00:00:00Z")
         ));
      }

      return IterableWithMarkers.from(alarmHistoryItems);
   }

}
