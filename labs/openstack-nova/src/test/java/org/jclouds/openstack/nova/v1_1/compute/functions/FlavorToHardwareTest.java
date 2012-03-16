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
package org.jclouds.openstack.nova.v1_1.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.UUID;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.domain.Location;
import org.jclouds.openstack.nova.v1_1.domain.Flavor;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

/**
 * Tests the function used to transform Flavor objects into Hardware objects
 * 
 * @author Matt Stephenson
 */
public class FlavorToHardwareTest {
   @Test
   public void testConversion() {
      UUID id = UUID.randomUUID();
      Flavor flavorToConvert = Flavor.builder().id(id.toString()).name("Test Flavor " + id).ram(262144).disk(10000)
            .vcpus(16).build();

      Hardware converted = new FlavorToHardware(Suppliers.<Location> ofInstance(null)).apply(flavorToConvert);

      assertEquals(converted.getName(), flavorToConvert.getName());
      assertEquals(converted.getId(), flavorToConvert.getId());
      assertEquals(converted.getProviderId(), flavorToConvert.getId());
      assertEquals(converted.getRam(), flavorToConvert.getRam());

      assertNotNull(converted.getProcessors());
      assertFalse(converted.getProcessors().isEmpty());
      assertEquals(converted.getProcessors().iterator().next().getCores(), (double) flavorToConvert.getVcpus());

      assertNotNull(converted.getVolumes());
      assertFalse(converted.getVolumes().isEmpty());
      assertEquals(converted.getVolumes().iterator().next().getSize(), Float.valueOf(flavorToConvert.getDisk()));

   }
}
