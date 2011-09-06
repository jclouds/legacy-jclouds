/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rimuhosting.miro.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rimuhosting.miro.config.RimuHostingRestClientModule.RimuIso8601DateAdapter;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseServerFromJsonResponseTest {
   Injector i = Guice.createInjector(new GsonModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(RimuIso8601DateAdapter.class);
         super.configure();
      }

   });

   public void testCancelled() {
      InputStream is = getClass().getResourceAsStream("/cancelled.json");

      ParseServerFromJsonResponse parser = i.getInstance(ParseServerFromJsonResponse.class);

      Server response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(response.getBillingData().getDateSuspended(), null);
      assertEquals(response.getBillingData().getDateCancelled(), new SimpleDateFormatDateService()
               .iso8601SecondsDateParse("2011-04-02T03:30:28Z"));
   }

}
