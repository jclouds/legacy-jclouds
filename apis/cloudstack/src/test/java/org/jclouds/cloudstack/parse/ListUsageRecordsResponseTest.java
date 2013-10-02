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
package org.jclouds.cloudstack.parse;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.jclouds.cloudstack.config.CloudStackParserModule;
import org.jclouds.cloudstack.domain.UsageRecord;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListUsageRecordsResponseTest extends BaseSetParserTest<UsageRecord> {

   @Override
   public String resource() {
      return "/listusagerecordsresponse.json";
   }

   @Override
   @SelectJson("usagerecord")
   public Set<UsageRecord> expected() {
      Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      c.set(Calendar.YEAR, 2011);
      c.set(Calendar.MONTH, Calendar.DECEMBER);
      c.set(Calendar.DAY_OF_MONTH, 15);
      c.set(Calendar.HOUR_OF_DAY, 0);
      c.set(Calendar.MINUTE, 0);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);
      Date start = c.getTime();
      c.add(Calendar.DAY_OF_MONTH, 1);
      c.add(Calendar.SECOND, -1);
      Date end = c.getTime();

      return ImmutableSet.of(UsageRecord.builder()
               .accountName("admin").accountId("2").domainId("1").zoneId("1")
               .description("Template Id:203 Size:3117171712")
               .usage("24 Hrs").usageType(UsageRecord.UsageType.TEMPLATE).rawUsageHours(24)
            .templateId("0").id("203").startDate(start).endDate(end).build());

   }
   
   @Override
   protected Injector injector() {
      return Guice.createInjector(new GsonModule(), new CloudStackParserModule());
   }

}
