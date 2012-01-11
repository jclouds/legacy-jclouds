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

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.jclouds.cloudstack.domain.ConfigurationEntry;
import org.testng.annotations.Test;
import org.testng.collections.Sets;

import javax.annotation.Nullable;
import java.util.Set;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.cloudstack.options.ListConfigurationEntriesOptions.Builder.name;
import static org.testng.Assert.assertEquals;

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
      for (ConfigurationEntry entry : entries) {
         checkConfigurationEntry(entry);
         categories.add(entry.getCategory());
      }

      assert categories.containsAll(ImmutableSet.<Object>of("Network", "Advanced", "Premium",
         "Storage", "Usage", "Snapshots", "Account Defaults", "Console Proxy", "Alert"));
   }

   @Test
   public void testUpdateConfigurationEntry() {
      assert globalAdminEnabled;

      Set<ConfigurationEntry> entries = globalAdminClient
         .getConfigurationClient().listConfigurationEntries();

      long expungeDelay = Long.parseLong(getValueByName(entries, "expunge.delay"));
      assert expungeDelay > 0;

      globalAdminClient.getConfigurationClient()
         .updateConfigurationEntry("expunge.delay", "" + (expungeDelay + 1));

      long newDelay = Long.parseLong(getOnlyElement(globalAdminClient.getConfigurationClient()
         .listConfigurationEntries(name("expunge.delay"))).getValue());
      assertEquals(newDelay, expungeDelay + 1);

      globalAdminClient.getConfigurationClient()
         .updateConfigurationEntry("expunge.delay", "" + expungeDelay);
   }

   private void checkConfigurationEntry(ConfigurationEntry entry) {
      assertEquals(entry, getEntryByName(globalAdminClient.getConfigurationClient()
         .listConfigurationEntries(name(entry.getName())), entry.getName()));
      assert entry.getCategory() != null : entry;
      assert entry.getDescription() != null : entry;
      assert entry.getName() != null : entry;
   }

   private String getValueByName(Set<ConfigurationEntry> entries, String name) {
      return getEntryByName(entries, name).getValue();
   }

   private ConfigurationEntry getEntryByName(Set<ConfigurationEntry> entries, final String name) {
      return Iterables.find(entries, new Predicate<ConfigurationEntry>() {
         @Override
         public boolean apply(@Nullable ConfigurationEntry entry) {
            return entry != null && Objects.equal(name, entry.getName());
         }
      });
   }
}
