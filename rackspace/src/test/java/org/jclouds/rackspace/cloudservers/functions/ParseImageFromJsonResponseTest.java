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

import org.jclouds.date.DateService;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.ImageStatus;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseImageFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ParseImageFromJsonResponseTest")
public class ParseImageFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());
   DateService dateService = i.getInstance(DateService.class);

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/cloudservers/test_get_image_details.json");

      ParseImageFromJsonResponse parser = new ParseImageFromJsonResponse(i.getInstance(Gson.class));
      Image response = parser.apply(is);
      assertEquals(response.getId(), 2);
      assertEquals(response.getName(), "CentOS 5.2");
      assertEquals(response.getCreated(), dateService
               .iso8601SecondsDateParse("2010-08-10T12:00:00Z"));
      assertEquals(response.getProgress(), new Integer(80));
      assertEquals(response.getServerId(), new Integer(12));
      assertEquals(response.getStatus(), ImageStatus.SAVING);
      assertEquals(response.getUpdated(), dateService
               .iso8601SecondsDateParse(("2010-10-10T12:00:00Z")));
   }
}
