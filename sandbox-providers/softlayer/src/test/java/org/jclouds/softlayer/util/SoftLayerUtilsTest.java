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
package org.jclouds.softlayer.util;

import com.google.common.collect.ImmutableSet;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNull;

@Test(groups = "unit")
public class SoftLayerUtilsTest {

   @Test
   public void testMatch() {
      VirtualGuest guest1 = new VirtualGuest.Builder().id(1).hostname("host1").domain("domain").build();
      VirtualGuest guest2 = new VirtualGuest.Builder().id(2).hostname("host2").domain("domain").build();

      VirtualGuest result = SoftLayerUtils.findVirtualGuest(ImmutableSet.of(guest1,guest2),"host1","domain");
      assertEquals(guest1,result);
   }

   @Test
   public void testMissing() {
      VirtualGuest guest1 = new VirtualGuest.Builder().id(1).hostname("host1").domain("domain").build();
      VirtualGuest guest2 = new VirtualGuest.Builder().id(2).hostname("host2").domain("domain").build();

      VirtualGuest result = SoftLayerUtils.findVirtualGuest(ImmutableSet.of(guest1,guest2),"missing","domain");
      assertNull(result);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testDuplicate() {
      VirtualGuest guest1 = new VirtualGuest.Builder().id(1).hostname("host1").domain("domain").build();
      VirtualGuest guest2 = new VirtualGuest.Builder().id(2).hostname("host1").domain("domain").build();

      SoftLayerUtils.findVirtualGuest(ImmutableSet.of(guest1,guest2),"host1","domain");
   }

}
