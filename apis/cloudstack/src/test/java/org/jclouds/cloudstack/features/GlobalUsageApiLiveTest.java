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
package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.jclouds.cloudstack.domain.JobResult;
import org.jclouds.cloudstack.domain.UsageRecord;
import org.jclouds.cloudstack.internal.BaseCloudStackApiLiveTest;
import org.jclouds.cloudstack.options.GenerateUsageRecordsOptions;
import org.jclouds.cloudstack.options.ListUsageRecordsOptions;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GlobalUsageApi}
 *
 * @author Richard Downer
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalUsageApiLiveTest")
public class GlobalUsageApiLiveTest extends BaseCloudStackApiLiveTest {

   @Test(groups = "live", enabled = true)
   public void testListUsage() {
      skipIfNotGlobalAdmin();

      Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      Date end = c.getTime();
      c.add(Calendar.MONTH, -1);
      Date start = c.getTime();

      JobResult result = globalAdminClient.getUsageClient().generateUsageRecords(start, end, GenerateUsageRecordsOptions.NONE);
      assertNotNull(result);
      assertTrue(result.isSuccess(), result.getDisplayText());

      Set<UsageRecord> records = globalAdminClient.getUsageClient().listUsageRecords(start, end, ListUsageRecordsOptions.NONE);
      assertNotNull(records);
      assertTrue(records.size() >= 0);
   }

}
