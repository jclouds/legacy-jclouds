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
package org.jclouds.cloudstack.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.cloudstack.domain.GuestIPType;
import org.jclouds.cloudstack.domain.NIC;
import org.jclouds.cloudstack.domain.TrafficType;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListVirtualMachinesResponseTest {

   Injector i = Guice.createInjector(new GsonModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   public void test() {
      InputStream is = getClass().getResourceAsStream("/listvirtualmachinesresponse.json");

      VirtualMachine expects = VirtualMachine
            .builder()
            .id(54)
            .name("i-3-54-VM")
            .displayName("i-3-54-VM")
            .account("adrian")
            .domainId(1)
            .domain("ROOT")
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-16T14:28:37-0800"))
            .state(VirtualMachine.State.STARTING)
            .isHAEnabled(false)
            .zoneId(1)
            .zoneName("San Jose 1")
            .templateId(2)
            .templateName("CentOS 5.3(64-bit) no GUI (XenServer)")
            .templateDisplayText("CentOS 5.3(64-bit) no GUI (XenServer)")
            .passwordEnabled(false)
            .serviceOfferingId(1)
            .serviceOfferingName("Small Instance")
            .cpuCount(1)
            .cpuSpeed(500)
            .memory(512)
            .guestOSId(11)
            .rootDeviceId(0)
            .rootDeviceType("NetworkFilesystem")
            .jobId(63l)
            .jobStatus(0)
            .nics(ImmutableSet.of(NIC.builder().id(72).networkId(204).netmask("255.255.255.0").gateway("10.1.1.1")
                  .IPAddress("10.1.1.18").trafficType(TrafficType.GUEST).guestIPType(GuestIPType.VIRTUAL)
                  .isDefault(true).build())).hypervisor("XenServer").build();

      UnwrapOnlyNestedJsonValue<Set<VirtualMachine>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyNestedJsonValue<Set<VirtualMachine>>>() {
            }));
      Set<VirtualMachine> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(Iterables.getOnlyElement(response), expects);
   }

}
