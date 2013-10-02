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
public class ParseBadVirtualGuest extends BaseItemParserTest<VirtualGuest> {

   @Override
   public String resource() {
      return "/virtual_guest_bad_halted.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public VirtualGuest expected() {
      return VirtualGuest
               .builder()
               .id(413348).accountId(93750).billingItemId(-1)
               .createDate(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-09-28T01:52:45-08:00"))
               .domain("jclouds.org").fullyQualifiedDomainName("foo-ef4.jclouds.org")
               .hostname("foo-ef4").maxCpu(0).maxCpuUnits("CORE").maxMemory(256)
               .statusId(1001).startCpus(0)
               //TODO: maybe powerState can be flattened like billingItemId
               .powerState(new PowerState(VirtualGuest.State.HALTED)).build();
   }

   protected Injector injector() {
      return Guice.createInjector(new SoftLayerParserModule(), new GsonModule());
   }

}
