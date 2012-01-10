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
import org.jclouds.cloudstack.domain.ConfigurationEntry;
import org.testng.annotations.Test;
import org.testng.collections.Sets;

import java.util.Set;

/**
 * Tests behavior of {@code GlobalConfigurationClient}
 *
 * @author Andrei Savu
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalConfigurationClientLiveTest")
public class GlobalConfigurationClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test
   public void testListConfigurationEntries() {
      assert globalAdminEnabled;

      Set<ConfigurationEntry> entries = globalAdminClient
         .getConfigurationClient().listConfigurationEntries();

      Set<String> categories = Sets.newHashSet();
      for(ConfigurationEntry entry : entries) {
         checkConfigurationEntry(entry);
         categories.add(entry.getCategory());
      }

      assert categories.containsAll(ImmutableSet.<Object>of("Network", "Advanced", "Premium",
         "Storage", "Usage", "Snapshots", "Account Defaults", "Console Proxy", "Alert"));
   }

   private void checkConfigurationEntry(ConfigurationEntry entry) {
      assert entry.getCategory() != null : entry;
      assert entry.getDescription() != null : entry;
      assert entry.getName() != null : entry;
   }
}
