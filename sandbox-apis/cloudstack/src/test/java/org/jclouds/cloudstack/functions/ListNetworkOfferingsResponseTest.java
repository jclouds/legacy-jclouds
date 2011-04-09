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

import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.TrafficType;
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
public class ListNetworkOfferingsResponseTest {

   Injector i = Guice.createInjector(new GsonModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   public void test() {
      InputStream is = getClass().getResourceAsStream("/listnetworkofferingsresponse.json");

      Set<NetworkOffering> expects = ImmutableSortedSet.<NetworkOffering> of(
            NetworkOffering.builder().id(6).name("DefaultVirtualizedNetworkOffering").displayText("Virtual Vlan")
                  .trafficType(TrafficType.GUEST).isDefault(true).supportsVLAN(false).availability("Required")
                  .networkRate(200).build(), NetworkOffering.builder().id(7).name("DefaultDirectNetworkOffering")
                  .displayText("Direct").trafficType(TrafficType.PUBLIC).isDefault(true).supportsVLAN(false)
                  .availability("Required").networkRate(200).build());

      UnwrapOnlyNestedJsonValue<Set<NetworkOffering>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyNestedJsonValue<Set<NetworkOffering>>>() {
            }));
      Set<NetworkOffering> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(Sets.newTreeSet(response), expects);
   }

}
