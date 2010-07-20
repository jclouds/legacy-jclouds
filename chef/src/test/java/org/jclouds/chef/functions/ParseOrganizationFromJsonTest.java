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

import org.jclouds.chef.domain.Organization;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.io.Payloads;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseOrganizationFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseOrganizationFromJsonTest")
public class ParseOrganizationFromJsonTest {

   private ParseJson<Organization> handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule());
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<Organization>>() {
      }));
   }

   public void test() {

      Organization org = new Organization("opscode");
      org.setFullName("Opscode, Inc.");
      org.setOrgType("Business");
      org.setClientname("opscode-validator");

      String toParse = "{\"name\": \"opscode\",\"full_name\": \"Opscode, Inc.\", \"org_type\": \"Business\",\"clientname\": \"opscode-validator\" }";

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads.newStringPayload(toParse))), org);
   }
}
