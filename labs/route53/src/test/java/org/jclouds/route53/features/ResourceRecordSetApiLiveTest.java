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
package org.jclouds.route53.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.util.List;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.route53.domain.ResourceRecordSet;
import org.jclouds.route53.domain.Zone;
import org.jclouds.route53.internal.BaseRoute53ApiLiveTest;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ResourceRecordSetApiLiveTest")
public class ResourceRecordSetApiLiveTest extends BaseRoute53ApiLiveTest {

   private void checkResourceRecordSet(ResourceRecordSet resourceRecordSet) {
      checkNotNull(resourceRecordSet.getName(), "Id cannot be null for a ResourceRecordSet %s", resourceRecordSet);
      checkNotNull(resourceRecordSet.getType(), "Type cannot be null for a ResourceRecordSet %s", resourceRecordSet);
      checkNotNull(resourceRecordSet.getTTL(),
            "While TTL can be null for a ResourceRecordSet, its Optional wrapper cannot %s", resourceRecordSet);
   }

   @Test
   protected void testListResourceRecordSets() {
      IterableWithMarker<Zone> zones = context.getApi().getZoneApi().list().get(0);
      if (zones.isEmpty())
         throw new SkipException("no zones in context: " + context);

      Zone zone = zones.first().get();
      List<ResourceRecordSet> records = api(zone.getId()).list().concat().toImmutableList();
      assertEquals(zone.getResourceRecordSetCount(), records.size());

      for (ResourceRecordSet resourceRecordSet : records) {
         checkResourceRecordSet(resourceRecordSet);
      }
   }

   protected ResourceRecordSetApi api(String zoneId) {
      return context.getApi().getResourceRecordSetApiForZone(zoneId);
   }
}
