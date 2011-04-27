/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import com.google.common.collect.ImmutableList;
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
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code ParseImageListFromJsonResponse}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseImageListFromJsonResponseTest {
   Injector i = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      }

   }, new GsonModule());
   DateService dateService = i.getInstance(DateService.class);

   @Test
   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_images.json");

      List<Image> expects = ImmutableList.of(new Image(1, "CentOS 5.2"), new Image(743, "My Server Backup"));

      UnwrapOnlyJsonValue<List<Image>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Image>>>() {
            }));
      List<Image> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(response, expects);
   }

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_list_images_detail.json");

      UnwrapOnlyJsonValue<List<Image>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Image>>>() {
            }));
      List<Image> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(response.get(0).getId(), 1);
      assertEquals(response.get(0).getName(), "CentOS 5.2");
      assertEquals(response.get(0).getCreated(), dateService.iso8601SecondsDateParse("2010-08-10T12:00:00Z"));
      assertEquals(response.get(0).getProgress(), null);
      assertEquals(response.get(0).getServerRef(), null);
      assertEquals(response.get(0).getStatus(), ImageStatus.ACTIVE);
      assertEquals(response.get(0).getUpdated(), dateService.iso8601SecondsDateParse("2010-10-10T12:00:00Z"));
      assertEquals(response.get(0).getMetadata().get("ImageType"), "Gold");
      assertEquals(response.get(0).getMetadata().get("ImageVersion"), "1.5");
      assertEquals(response.get(0).getMetadata().size(), 2);


      assertEquals(response.get(1).getId(), 743);
      assertEquals(response.get(1).getName(), "My Server Backup");
      assertEquals(response.get(1).getCreated(), dateService.iso8601SecondsDateParse("2009-07-07T09:56:16Z"));

      assertEquals(response.get(1).getProgress(), new Integer(80));
      assertEquals(response.get(1).getStatus(), ImageStatus.SAVING);
      assertEquals(response.get(1).getUpdated(), dateService.iso8601SecondsDateParse("2010-10-10T12:00:00Z"));
      assertEquals(response.get(1).getServerRef(), "http://servers.api.openstack.org/v1.1/1234/servers/12");

      //short form of reference
      assertEquals(response.get(2).getServerRef(), "12");
   }

}
