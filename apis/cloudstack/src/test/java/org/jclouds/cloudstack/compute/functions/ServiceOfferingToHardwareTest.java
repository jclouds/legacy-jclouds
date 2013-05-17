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
package org.jclouds.cloudstack.compute.functions;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Set;

import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.parse.ListServiceOfferingsResponseTest;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests {@code ServiceOfferingToHardware}
 * 
 */
@Test(groups = "unit")
public class ServiceOfferingToHardwareTest {

   static ServiceOfferingToHardware function = new ServiceOfferingToHardware();
   static Hardware one = new HardwareBuilder().ids("1").name("Small Instance")
         .processors(ImmutableList.of(new Processor(1, 500))).ram(512).build();
   static Hardware two = new HardwareBuilder().ids("2").name("Medium Instance")
         .processors(ImmutableList.of(new Processor(1, 1000))).ram(1024).build();

   @Test
   public void test() {

      Set<Hardware> expected = ImmutableSet.of(one, two);

      Set<ServiceOffering> offerings = new ListServiceOfferingsResponseTest().expected();

      Iterable<Hardware> profiles = Iterables.transform(offerings, function);

      assertEquals(profiles.toString(), expected.toString());
   }

}
