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
package org.jclouds.openstack.nova.domain;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code CreateImageBinder}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ServerStatusTest {

   @Test
   public void testAllKnownStatusesIsRecognized() {
      List<String> knownStatuses = Arrays.asList(
            "ACTIVE", "BUILD", "REBUILD", "SUSPENDED", "QUEUE_RESIZE",
            "PREP_RESIZE", "RESIZE", "VERIFY_RESIZE",
            "PASSWORD", "RESCUE", "REBOOT",
            "HARD_REBOOT", "DELETE_IP", "UNKNOWN", "DELETED");
      for (String status : knownStatuses) {
         assertNotEquals(ServerStatus.UNRECOGNIZED, ServerStatus.fromValue(status));
      }

      List<String> allStatuses = Lists.newArrayList(knownStatuses);
      allStatuses.add("UNRECOGNIZED");

      Set<ServerStatus> enumValues = Sets.newHashSet(ServerStatus.values());

      assertEquals(enumValues.size(), allStatuses.size());

      for (String status : allStatuses) {
         assertTrue(enumValues.contains(ServerStatus.valueOf(status)));
      }
   }

}

