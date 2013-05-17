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

import java.io.InputStream;
import java.util.Set;

import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code GetMetricStatisticsResponseHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetMetricStatisticsResponseHandlerTest")
public class GetMetricStatisticsResponseHandlerTest extends BaseHandlerTest {
   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/get_metric_statistics.xml");

      Set<Datapoint> expected = expected();

      GetMetricStatisticsResponseHandler handler = injector.getInstance(GetMetricStatisticsResponseHandler.class);
      Set<Datapoint> result = factory.create(handler).parse(is);

      assertEquals(result, expected);
   }

   DateService dateService = new SimpleDateFormatDateService();

   public Set<Datapoint> expected() {

      Set<Datapoint> expected = ImmutableSet.of(new Datapoint(0.17777777777777778, null, null, dateService
               .iso8601SecondsDateParse("2009-01-16T00:00:00Z"), 9.0, null, Unit.PERCENT, null), new Datapoint(0.1,
               null, null, dateService.iso8601SecondsDateParse("2009-01-16T00:01:00Z"), 8.0, null, Unit.PERCENT, null));
      return expected;
   }

}
