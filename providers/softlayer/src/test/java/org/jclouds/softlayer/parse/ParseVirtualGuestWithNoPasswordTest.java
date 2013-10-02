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
package org.jclouds.softlayer.parse;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.softlayer.config.SoftLayerParserModule;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.PowerState;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseVirtualGuestWithNoPasswordTest")
public class ParseVirtualGuestWithNoPasswordTest extends BaseItemParserTest<VirtualGuest> {

   @Override
   public String resource() {
      return "/virtual_guest_no_password.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public VirtualGuest expected() {
      return VirtualGuest
               .builder()
               .id(416788).accountId(93750).billingItemId(7185261)
               .createDate(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-10-02T03:24:43-08:00"))
               .dedicatedAccountHostOnly(true).domain("me.org").fullyQualifiedDomainName("node1000360500.me.org")
               .hostname("node1000360500").maxCpu(1).maxCpuUnits("CORE").maxMemory(1024)
               .modifyDate(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-10-02T03:25:33-08:00"))
               .primaryBackendIpAddress("10.37.102.194").primaryIpAddress("173.192.29.186").startCpus(1).statusId(1001)
               .uuid("96fe22ad-8182-924d-ce51-a037e477dd83")
               .operatingSystem(OperatingSystem.builder().id(913960).build())
               .datacenter(Datacenter.builder().id(3).name("dal01").longName("Dallas").build())
               //TODO: maybe powerState can be flattened like billingItemId
               .powerState(new PowerState(VirtualGuest.State.HALTED)).build();
   }

   protected Injector injector() {
      return Guice.createInjector(new SoftLayerParserModule(), new GsonModule());
   }

}
