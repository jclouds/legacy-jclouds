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
package org.jclouds.cloudstack.parse;

import java.util.Set;

import org.jclouds.cloudstack.domain.GuestIPType;
import org.jclouds.cloudstack.domain.NIC;
import org.jclouds.cloudstack.domain.TrafficType;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListVirtualMachinesResponse3xTest extends BaseSetParserTest<VirtualMachine> {

   @Override
   public String resource() {
      return "/listvirtualmachinesresponse3x.json";
   }

   @Override
   @SelectJson("virtualmachine")
   public Set<VirtualMachine> expected() {
      return ImmutableSet.of(VirtualMachine
            .builder()
            .id("fee2ccb3-c1f2-4e7b-8465-42b390e10dff")
            .name("cloudstack-r-611")
            .displayName("cloudstack-r-611")
            .account("jcloud2")
            .domainId("ea66e3a5-d007-42e8-a0de-ec5ce778a1d7")
            .domain("jCloud")
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-22T09:18:28-0700"))
            .state(VirtualMachine.State.RUNNING)
            .isHAEnabled(false)
            .zoneId("1")
            .zoneName("Santa Clara Zone")
            .templateId("5c65f152-a4bc-4405-a756-fd10841a9aa7")
                             .templateName("jclouds-6d4bdc29")
            .templateDisplayText("jclouds live testCreateTemplate")
            .passwordEnabled(false)
            .serviceOfferingId("5305750d-df71-4da9-8cd0-e23c2236a6e2")
            .serviceOfferingName("Micro Instance")
            .cpuCount(1)
            .cpuSpeed(500)
            .memory(256)
            .guestOSId("6dcd58ce-1ec6-432c-af0b-9ab4ca9207d9")
            .rootDeviceId("0")
            .rootDeviceType("IscsiLUN")
                             .publicIP("72.52.126.110")
                             .publicIPId("e202aafb-ab41-4dc0-80e9-9fcd64fbf45c")
            .nics(ImmutableSet.of(NIC.builder().id("48640c5e-90f3-45bd-abd2-a108ca8957ac").
                                  networkId("c0d5db5b-f7d5-44e1-b854-21ecd1a09dbf").netmask("255.255.255.0").gateway("10.1.1.1")
                                  .IPAddress("10.1.1.227").trafficType(TrafficType.GUEST).guestIPType(GuestIPType.ISOLATED)
                                  .isDefault(true).build())).build());
   }

}
