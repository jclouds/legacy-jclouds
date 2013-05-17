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

import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.Event;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code EventClient}
 *
 * @author Vijay Kiran
 */
@Test(groups = "live", singleThreaded = true, testName = "EventClientLiveTest")
public class EventClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testlistEventTypes() throws Exception {
      final Set<String> response = client.getEventClient().listEventTypes();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (String type : response) {
         checkEventType(type);
      }
   }

   public void testlistEvents() throws Exception {
      final Set<Event> response = client.getEventClient().listEvents();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (Event event : response) {
         checkEvent(event);
      }
   }

   private void checkEvent(Event event) {
      assert event.getAccount() != null : event;
      assert event.getCreated() != null : event;
      assert event.getDescription() != null : event;
      assert event.getDomain() != null : event;
      assert event.getId() != null : event;
      assert event.getLevel() != null : event;
      assert event.getState() != null : event;
      assert event.getType() != null : event;
      assert event.getUsername() != null : event;
   }

   protected void checkEventType(String eventType) {
      assert eventType != null : eventType;
   }

}
