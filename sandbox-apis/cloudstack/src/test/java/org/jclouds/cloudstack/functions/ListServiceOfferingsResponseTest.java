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

import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.StorageType;
import org.jclouds.date.internal.SimpleDateFormatDateService;
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
public class ListServiceOfferingsResponseTest {

   Injector i = Guice.createInjector(new GsonModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   public void test() {
      InputStream is = getClass().getResourceAsStream("/listserviceofferingsresponse.json");

      Set<ServiceOffering> expects = ImmutableSortedSet.<ServiceOffering> of(
            ServiceOffering.builder().id(1).name("Small Instance")
                  .displayText("Small Instance - 500 MhZ CPU, 512 MB RAM - $0.05 per hour").cpuNumber(1).cpuSpeed(500)
                  .memory(512)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-11T15:22:32-0800"))
                  .storageType(StorageType.SHARED).haSupport(false).build(),
            ServiceOffering.builder().id(2).name("Medium Instance")
                  .displayText("Medium Instance, 1 GhZ CPU,  1 GB RAM - $0.10 per hour").cpuNumber(1).cpuSpeed(1000)
                  .memory(1024)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-11T15:22:32-0800"))
                  .storageType(StorageType.SHARED).haSupport(false).build());

      UnwrapOnlyNestedJsonValue<Set<ServiceOffering>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyNestedJsonValue<Set<ServiceOffering>>>() {
            }));
      Set<ServiceOffering> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(Sets.newTreeSet(response), expects);
   }

}
