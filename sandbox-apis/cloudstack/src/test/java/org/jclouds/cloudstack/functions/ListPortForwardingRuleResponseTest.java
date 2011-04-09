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

import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListPortForwardingRuleResponseTest {

   Injector i = Guice.createInjector(new GsonModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   public void test() {
      InputStream is = getClass().getResourceAsStream("/listportforwardingrulesresponse.json");

      Set<PortForwardingRule> expects = ImmutableSortedSet.<PortForwardingRule> of(PortForwardingRule.builder().id(15)
               .privatePort(22).protocol("tcp").publicPort(2022).virtualMachineId(3).virtualMachineName("i-3-3-VM")
               .IPAddressId(3).IPAddress("72.52.126.32").state("Active").build(), PortForwardingRule.builder().id(18)
               .privatePort(22).protocol("tcp").publicPort(22).virtualMachineId(89).virtualMachineName("i-3-89-VM")
               .IPAddressId(34).IPAddress("72.52.126.63").state("Active").build());

      UnwrapOnlyNestedJsonValue<Set<PortForwardingRule>> parser = i.getInstance(Key
               .get(new TypeLiteral<UnwrapOnlyNestedJsonValue<Set<PortForwardingRule>>>() {
               }));
      Set<PortForwardingRule> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(Sets.newTreeSet(response), expects);
   }

}
