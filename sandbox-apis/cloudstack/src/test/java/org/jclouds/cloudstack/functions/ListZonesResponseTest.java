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

import java.util.Set;

import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.Zone;
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
public class ListZonesResponseTest {

   Injector i = Guice.createInjector(new GsonModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   public void testAdvanced() {

      Zone expects = Zone.builder().id(1).name("San Jose 1").networkType(NetworkType.ADVANCED).build();

      UnwrapOnlyNestedJsonValue<Set<Zone>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyNestedJsonValue<Set<Zone>>>() {
            }));
      Set<Zone> response = parser
            .apply(new HttpResponse(
                  200,
                  "ok",
                  Payloads
                        .newStringPayload("{ \"listzonesresponse\" : { \"zone\" : [  {\"id\":1,\"name\":\"San Jose 1\",\"networktype\":\"Advanced\"} ] } }")));

      assertEquals(Iterables.getOnlyElement(response), expects);
   }
}
