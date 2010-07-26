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

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.config.RackspaceParserModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseFlavorFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ParseFlavorFromJsonResponseTest")
public class ParseFlavorFromJsonResponseTest {
   Injector i = Guice.createInjector(new RackspaceParserModule(), new GsonModule());

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/cloudservers/test_get_flavor_details.json");

      UnwrapOnlyJsonValue<Flavor> parser = i.getInstance(Key.get(new TypeLiteral<UnwrapOnlyJsonValue<Flavor>>() {
      }));
      Flavor response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));
      assertEquals(response.getId(), 1);
      assertEquals(response.getName(), "256 MB Server");
      assertEquals(response.getRam(), new Integer(256));
      assertEquals(response.getDisk(), new Integer(10));
   }

}
