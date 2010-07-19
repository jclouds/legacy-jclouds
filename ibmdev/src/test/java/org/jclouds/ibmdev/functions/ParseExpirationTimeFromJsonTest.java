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

package org.jclouds.ibmdev.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.io.Payloads;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseExpirationTimeFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "ibmdev.ParseExpirationTimeFromJsonTest")
public class ParseExpirationTimeFromJsonTest {

   private ParseExpirationTimeFromJson handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule() {
         @Override
         protected void configure() {
            bind(DateAdapter.class).to(LongDateAdapter.class);
            super.configure();
         }
      });
      handler = injector.getInstance(ParseExpirationTimeFromJson.class);
   }

   public void test() {
      Date compare = handler.apply(new HttpResponse(200, "ok", Payloads
            .newStringPayload("{ \"expirationTime\":1249876800000 }")));
      assertEquals(compare, new Date(1249876800000l));
   }
}
