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

import org.jclouds.date.DateService;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.ImageStatus;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseImageListFromJsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudImages.ParseImageListFromJsonResponseTest")
public class ParseImageListFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   DateService dateService = i.getInstance(DateService.class);

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/cloudservers/test_list_images.json");

      List<Image> expects = ImmutableList.of(new Image(2, "CentOS 5.2"), new Image(743,
               "My Server Backup"));
      ParseImageListFromJsonResponse parser = new ParseImageListFromJsonResponse(i
               .getInstance(Gson.class));
      assertEquals(parser.apply(is), expects);
   }

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/cloudservers/test_list_images_detail.json");

      ParseImageListFromJsonResponse parser = new ParseImageListFromJsonResponse(i
               .getInstance(Gson.class));
      List<Image> response = parser.apply(is);
      assertEquals(response.get(0).getId(), 2);
      assertEquals(response.get(0).getName(), "CentOS 5.2");
      assertEquals(response.get(0).getCreated(), dateService
               .iso8601SecondsDateParse("2010-08-10T12:00:00Z"));
      assertEquals(response.get(0).getProgress(), null);
      assertEquals(response.get(0).getServerId(), null);
      assertEquals(response.get(0).getStatus(), ImageStatus.ACTIVE);
      assertEquals(response.get(0).getUpdated(), dateService
               .iso8601SecondsDateParse("2010-10-10T12:00:00Z"));

      assertEquals(response.get(1).getId(), 743);
      assertEquals(response.get(1).getName(), "My Server Backup");
      assertEquals(response.get(1).getCreated(), dateService
               .iso8601SecondsDateParse("2009-07-07T09:56:16-05:00"));
      ;
      assertEquals(response.get(1).getProgress(), new Integer(80));
      assertEquals(response.get(1).getServerId(), new Integer(12));
      assertEquals(response.get(1).getStatus(), ImageStatus.SAVING);
      assertEquals(response.get(1).getUpdated(), dateService
               .iso8601SecondsDateParse("2010-10-10T12:00:00Z"));
   }

}
