/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.cloudstack.features;

import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.domain.DiskOffering;
import org.jclouds.cloudstack.domain.JobResult;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.NetworkOfferingAvailabilityType;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.StorageType;
import org.jclouds.cloudstack.domain.UsageRecord;
import org.jclouds.cloudstack.options.GenerateUsageRecordsOptions;
import org.jclouds.cloudstack.options.ListUsageRecordsOptions;
import org.jclouds.cloudstack.options.UpdateDiskOfferingOptions;
import org.jclouds.cloudstack.options.UpdateNetworkOfferingOptions;
import org.jclouds.cloudstack.options.UpdateServiceOfferingOptions;
import org.jclouds.logging.Logger;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import static com.google.common.collect.Iterables.getFirst;
import static org.jclouds.cloudstack.domain.NetworkOfferingAvailabilityType.OPTIONAL;
import static org.jclouds.cloudstack.domain.NetworkOfferingAvailabilityType.REQUIRED;
import static org.jclouds.cloudstack.options.CreateDiskOfferingOptions.Builder.diskSizeInGB;
import static org.jclouds.cloudstack.options.CreateServiceOfferingOptions.Builder.highlyAvailable;
import static org.testng.Assert.*;

/**
 * Tests behavior of {@code GlobalUsageClient}
 *
 * @author Richard Downer
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalUsageClientLiveTest")
public class GlobalUsageClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test(groups = "live", enabled = true)
   public void testListUsage() {
      Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      Date end = c.getTime();
      c.add(Calendar.MONTH, -1);
      Date start = c.getTime();

      JobResult result = globalAdminClient.getUsageClient().generateUsageRecords(start, end, GenerateUsageRecordsOptions.NONE);
      assertNotNull(result);
      assertTrue(result.getSuccess(), result.getDisplayText());

      Set<UsageRecord> records = globalAdminClient.getUsageClient().listUsageRecords(start, end, ListUsageRecordsOptions.NONE);
      assertNotNull(records);
      assertTrue(records.size() > 0);
   }

}
