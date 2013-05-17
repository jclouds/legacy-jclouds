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
package org.jclouds.cim.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ResourceAllocationSettingDataHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ResourceAllocationSettingDataHandlerTest")
public class ResourceAllocationSettingDataHandlerTest extends BaseHandlerTest {

   public void testNormal() {
      InputStream is = getClass().getResourceAsStream("/resourceallocation.xml");

      ResourceAllocationSettingData result = factory.create(
               injector.getInstance(ResourceAllocationSettingDataHandler.class)).parse(is);

      ResourceAllocationSettingData expects = ResourceAllocationSettingData.builder().allocationUnits("Gigabytes")
               .caption("1234568").description("Hard Disk").elementName("D:\\").hostResource("data").instanceID("6")
               .resourceType(ResourceAllocationSettingData.ResourceType.PARTITIONABLE_UNIT).virtualQuantity(50l)
               .build();
      assertEquals(result.toString(), expects.toString());
   }

   public void testHosting() {
      InputStream is = getClass().getResourceAsStream("/resourceallocation-hosting.xml");

      ResourceAllocationSettingData result = factory.create(
               injector.getInstance(ResourceAllocationSettingDataHandler.class)).parse(is);

      ResourceAllocationSettingData expects = ResourceAllocationSettingData.builder().elementName("1 virtual CPU(s)")
               .allocationUnits("hertz * 10^6").instanceID("1").resourceType(ResourceAllocationSettingData.ResourceType.PROCESSOR)
               .virtualQuantity(1l).description("Number of Virtual CPUs").virtualQuantityUnits("count").build();
      assertEquals(result.toString(), expects.toString());

   }
}
