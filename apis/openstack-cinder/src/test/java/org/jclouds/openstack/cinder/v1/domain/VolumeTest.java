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
package org.jclouds.openstack.cinder.v1.domain;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "VolumeTest")
public class VolumeTest {
   public void testVolumeForId() {
      Volume volume = Volume.forId("60761c60-0f56-4499-b522-ff13e120af10");
      
      assertEquals(volume.getId(), "60761c60-0f56-4499-b522-ff13e120af10");
      assertEquals(volume.getStatus(), Volume.Status.UNRECOGNIZED);
      assertEquals(volume.getZone(), "nova");
   }
}
