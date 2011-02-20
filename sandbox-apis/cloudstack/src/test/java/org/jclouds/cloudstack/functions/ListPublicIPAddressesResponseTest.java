/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

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
public class ListPublicIPAddressesResponseTest {

   Injector i = Guice.createInjector(new GsonModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   public void test() {
      InputStream is = getClass().getResourceAsStream("/listpublicipaddressesresponse.json");

      PublicIPAddress expects = PublicIPAddress.builder().id(30).IPAddress("72.52.126.59").allocated(
               new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-19T21:15:01-0800")).zoneId(1)
               .zoneName("San Jose 1").isSourceNAT(false).account("adrian").domainId(1).domain("ROOT")
               .usesVirtualNetwork(true).isStaticNAT(false).associatedNetworkId(204).networkId(200).state(
                        PublicIPAddress.State.ALLOCATED).build();

      UnwrapOnlyNestedJsonValue<Set<PublicIPAddress>> parser = i.getInstance(Key
               .get(new TypeLiteral<UnwrapOnlyNestedJsonValue<Set<PublicIPAddress>>>() {
               }));
      Set<PublicIPAddress> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(Iterables.getOnlyElement(response), expects);
   }

}
