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
package org.jclouds.nirvanix.sdn.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseSessionTokenFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "sdn.ParseSessionTokenFromJsonResponseTest")
public class ParseSessionTokenFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/login.json");

      ParseSessionTokenFromJsonResponse parser = i
            .getInstance(ParseSessionTokenFromJsonResponse.class);
      String response = parser.apply(new HttpResponse(200, "ok", Payloads
            .newInputStreamPayload(is)));
      assertEquals(response, "e4b08449-4501-4b7a-af6a-d4e1e1bd7919");
   }

}
