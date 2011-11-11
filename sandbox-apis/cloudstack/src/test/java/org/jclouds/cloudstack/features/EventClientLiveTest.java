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

import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.testng.annotations.Test;

/**
 * Tests behavior of {@code EventClient}
 *
 * @author Vijay Kiran
 */
@Test(groups = "live", singleThreaded = true, testName = "EventClientLiveTest")
public class EventClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testlistEventTypes() throws Exception {
      Set<String> response = client.getEventClient().listEventTypes();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (String type : response) {
         checkEventType(type);
      }
   }

   protected void checkEventType(String eventType) {
      assert eventType != null : eventType;
   }

}
