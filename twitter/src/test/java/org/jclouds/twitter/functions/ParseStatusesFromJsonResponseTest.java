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

package org.jclouds.twitter.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.SortedSet;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.config.GsonModule;
import org.jclouds.twitter.domain.Location;
import org.jclouds.twitter.domain.Status;
import org.jclouds.twitter.domain.User;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseStatusesFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "twitter.ParseStatusesFromJsonResponseTest")
public class ParseStatusesFromJsonResponseTest {

   Injector i = Guice.createInjector(new GsonModule());

   DateService dateService = new SimpleDateFormatDateService();

   public void testApplyInputStreamDetails() throws IOException {
      InputStream is = getClass().getResourceAsStream("/test_mentions.json");

      SortedSet<Status> expects = ImmutableSortedSet
            .of(

            new Status(
                  dateService.cDateParse("Tue Jun 29 20:41:15 +0000 2010"),
                  false,
                  new Location("Point", new double[] { 153.08691298, -26.38658779 }),
                  15138751340l,
                  "adrianfcole",
                  15112459535l,
                  21744326,
                  null,
                  "@adrianfcole hehe, yes. Still going :) hope you're keeping well!",
                  false,
                  new User(
                        dateService.cDateParse("Sat Jul 26 08:08:17 +0000 2008"),
                        "London-based South African software geek & amateur photog. Since Nov 2009, travelling the world with @sunflowerkate on an extended honeymoon",
                        21,
                        315,
                        true,
                        405,
                        true,
                        false,
                        15608907,
                        "Travelling around the world",
                        "Andrew Newdigate",
                        "en",
                        false,
                        "FFF04D",
                        true,
                        URI
                              .create("http://a1.twimg.com/profile_background_images/62032362/_MG_8095_6_7HDR_tonemapped.jpg"),
                        false, URI
                              .create("http://a1.twimg.com/profile_images/593267212/many_moon_honeymoon_normal.jpg"),
                        "0099CC", "fff8ad", "f6ffd1", "333333", false, "suprememoocow", 987, "Kuala Lumpur", URI
                              .create("http://newdigate.me"), -28800, false))

            );

      ParseJson<SortedSet<Status>> parser = i.getInstance(Key.get(new TypeLiteral<ParseJson<SortedSet<Status>>>() {
      }));
      SortedSet<Status> response = parser.apply(is);
      assertEquals(response, expects);
   }
}
