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

package org.jclouds.openstack.nova.functions;

import com.google.inject.*;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.nova.domain.Image;
import org.jclouds.openstack.nova.domain.ImageStatus;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.net.UnknownHostException;

import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code ParseImageFromJsonResponse}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseImageFromJsonResponseTest {
   Injector i = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      }

   }, new GsonModule());

   DateService dateService = i.getInstance(DateService.class);

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException {
      Image response = parseImage();

      assertEquals(response.getId(), 2);
      assertEquals(response.getName(), "CentOS 5.2");
      assertEquals(response.getCreated(), dateService.iso8601SecondsDateParse("2010-08-10T12:00:00Z"));
      assertEquals(response.getProgress(), new Integer(80));
      assertEquals(response.getStatus(), ImageStatus.SAVING);
      assertEquals(response.getUpdated(), dateService.iso8601SecondsDateParse(("2010-10-10T12:00:00Z")));
      assertEquals(response.getServerRef(), "http://servers.api.openstack.org/v1.1/1234/servers/12");
      assertEquals(response.getMetadata().get("ImageVersion"), "1.5");
      assertEquals(response.getMetadata().get("ImageType"), "Gold");
      assertEquals(response.getMetadata().size(), 2);
   }

   public static Image parseImage() {
      Injector i = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         }

      }, new GsonModule());

      InputStream is = ParseImageFromJsonResponseTest.class.getResourceAsStream("/test_get_image_details.json");

      UnwrapOnlyJsonValue<Image> parser = i.getInstance(Key.get(new TypeLiteral<UnwrapOnlyJsonValue<Image>>() {
      }));
      Image response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));
      return response;
   }

}
