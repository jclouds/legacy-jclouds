/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import org.jclouds.cloudservers.domain.Image;
import org.jclouds.cloudservers.domain.ImageStatus;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.json.config.GsonModule;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

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

   },new GsonModule());
   DateService dateService = i.getInstance(DateService.class);

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_images.json");

      List<Image> expects = ImmutableList.of(Image.builder().id(2).name("CentOS 5.2").build(),
            Image.builder().id(743).name("My Server Backup").build());

      UnwrapOnlyJsonValue<List<Image>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Image>>>() {
            }));
      List<Image> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());

      assertEquals(response, expects);
   }

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_list_images_detail.json");

      UnwrapOnlyJsonValue<List<Image>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Image>>>() {
            }));
      List<Image> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());

      assertEquals(response.get(0).getId(), 2);
      assertEquals(response.get(0).getName(), "CentOS 5.2");
      assertEquals(response.get(0).getCreated(), dateService.iso8601SecondsDateParse("2010-08-10T12:00:00Z"));
      assertEquals(response.get(0).getProgress(), null);
      assertEquals(response.get(0).getServerId(), null);
      assertEquals(response.get(0).getStatus(), ImageStatus.ACTIVE);
      assertEquals(response.get(0).getUpdated(), dateService.iso8601SecondsDateParse("2010-10-10T12:00:00Z"));

      assertEquals(response.get(1).getId(), 743);
      assertEquals(response.get(1).getName(), "My Server Backup");
      assertEquals(response.get(1).getCreated(), dateService.iso8601SecondsDateParse("2009-07-07T09:56:16-05:00"));
      ;
      assertEquals(response.get(1).getProgress(), Integer.valueOf(80));
      assertEquals(response.get(1).getServerId(), Integer.valueOf(12));
      assertEquals(response.get(1).getStatus(), ImageStatus.SAVING);
      assertEquals(response.get(1).getUpdated(), dateService.iso8601SecondsDateParse("2010-10-10T12:00:00Z"));
   }

}
