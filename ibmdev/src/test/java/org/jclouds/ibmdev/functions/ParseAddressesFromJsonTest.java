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

import java.io.IOException;
import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.ibmdev.domain.Address;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseAddresssFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "ibmdev.ParseAddressesFromJsonTest")
public class ParseAddressesFromJsonTest {

   private ParseAddressesFromJson handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule() {
         @Override
         protected void configure() {
            bind(DateAdapter.class).to(LongDateAdapter.class);
            super.configure();
         }
      });
      handler = injector.getInstance(ParseAddressesFromJson.class);
   }

   public void test() {
      Address address1 = new Address(2, 1, "129.33.196.243", 1217l, 1l);
      Address address2 = new Address(3, 2, "129.33.196.244", 1218l, null);
      Set<? extends Address> compare = handler.apply(new HttpResponse(
               ParseAddressesFromJsonTest.class.getResourceAsStream("/addresses.json")));
      assert (compare.contains(address1));
      assert (compare.contains(address2));
   }
}
