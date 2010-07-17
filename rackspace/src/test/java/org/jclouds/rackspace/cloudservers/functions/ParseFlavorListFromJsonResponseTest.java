/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rackspace.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.Payloads;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseFlavorListFromJsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudFlavors.ParseFlavorListFromJsonResponseTest")
public class ParseFlavorListFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream(
            "/cloudservers/test_list_flavors.json");

      List<Flavor> expects = ImmutableList.of(new Flavor(1, "256 MB Server"),
            new Flavor(2, "512 MB Server"));

      UnwrapOnlyJsonValue<List<Flavor>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Flavor>>>() {
            }));
      List<Flavor> response = parser.apply(new HttpResponse(200, "ok", Payloads
            .newInputStreamPayload(is)));
      assertEquals(response, expects);
   }

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream(
            "/cloudservers/test_list_flavors_detail.json");

      UnwrapOnlyJsonValue<List<Flavor>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Flavor>>>() {
            }));
      List<Flavor> response = parser.apply(new HttpResponse(200, "ok", Payloads
            .newInputStreamPayload(is)));
      assertEquals(response.get(0).getId(), 1);
      assertEquals(response.get(0).getName(), "256 MB Server");
      assertEquals(response.get(0).getDisk(), new Integer(10));
      assertEquals(response.get(0).getRam(), new Integer(256));

      assertEquals(response.get(1).getId(), 2);
      assertEquals(response.get(1).getName(), "512 MB Server");
      assertEquals(response.get(1).getDisk(), new Integer(20));
      assertEquals(response.get(1).getRam(), new Integer(512));

   }

}
