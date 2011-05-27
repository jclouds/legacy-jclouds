/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.openstack.nova.domain;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code CreateImageBinder}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ServerTest {
   public void testStatusDoesntAffectEquals() {
      Server server1 = new Server(1, "hello");
      server1.setStatus(ServerStatus.ACTIVE);
      Server server2 = new Server(1, "hello");
      server2.setStatus(ServerStatus.BUILD);
      assertEquals(server1, server2);
   }

   public void testProgressDoesntAffectEquals() {
      Server server1 = new Server(1, "hello");
      server1.setProgress(1);
      Server server2 = new Server(1, "hello");
      server2.setProgress(2);
      assertEquals(server1, server2);
   }

}
