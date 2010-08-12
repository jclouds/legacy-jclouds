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

package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.Sandbox;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseSandboxFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseSandboxFromJsonTest")
public class ParseSandboxFromJsonTest {

   private ParseJson<Sandbox> handler;
   private DateService dateService;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ChefParserModule(), new GsonModule());
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<Sandbox>>() {
      }));
      dateService = injector.getInstance(DateService.class);
   }

   public void test() {
      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads.newPayload(ParseSandboxFromJsonTest.class
            .getResourceAsStream("/sandbox.json")))), new Sandbox("1-8c27b0ea4c2b7aaedbb44cfbdfcc11b2", false,
            dateService.iso8601SecondsDateParse("2010-07-07T03:36:00+00:00"), ImmutableSet.<String> of(),
            "f9d6d9b72bae465890aae87969f98a9c", "f9d6d9b72bae465890aae87969f98a9c"));
   }
}
