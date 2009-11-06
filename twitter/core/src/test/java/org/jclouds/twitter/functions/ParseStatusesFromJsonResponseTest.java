/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.twitter.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.SortedSet;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.twitter.domain.Status;
import org.jclouds.twitter.domain.User;
import org.jclouds.util.DateService;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseStatusesFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "twitter.ParseStatusesFromJsonResponseTest")
public class ParseStatusesFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule() {
      @Override
      protected void configure() {
         bind(DateTimeAdapter.class).to(CDateTimeAdapter.class);
         super.configure();
      }
   });
   DateService dateService = new DateService();

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_mentions.json");

      SortedSet<Status> expects = ImmutableSortedSet
               .of(

                        new Status(
                                 dateService.cDateParse("Sat Oct 31 01:45:14 +0000 2009"),
                                 false,
                                 null,
                                 5303839785l,
                                 null,
                                 null,
                                 null,
                                 "<a href=\"http://www.tweetdeck.com/\" rel=\"nofollow\">TweetDeck</a>",
                                 "RT @jclouds: come find out about #cloud storage and how to access it from #java in palo alto this Tuesday: http://is.gd/4IFA9",
                                 false,
                                 new User(
                                          dateService.cDateParse("Sat Apr 26 06:13:08 +0000 2008"),
                                          "Jack of All Trades: Dad to anZel and Arden, VMware, vCloud, Security, Compliance, Former Developer",
                                          0,
                                          474,
                                          false,
                                          199,
                                          false,
                                          14540593,
                                          "Bay Area, CA",
                                          "Jian Zhen",
                                          false,
                                          "C6E2EE",
                                          URI
                                                   .create("http://s.twimg.com/a/1256778767/images/themes/theme2/bg.gif"),
                                          false,
                                          URI
                                                   .create("http://a3.twimg.com/profile_images/64445411/30b8b19_bigger_normal.jpg"),
                                          "1F98C7", "C6E2EE", "DAECF4", "663B12", false, "zhenjl",
                                          1981, "Pacific Time (US & Canada)", URI
                                                   .create("http://zhen.org"), -28800, false)),

                        new Status(
                                 dateService.cDateParse("Sat Oct 31 09:35:27 +0000 2009"),
                                 false,
                                 null,
                                 5310690603l,
                                 null,
                                 null,
                                 null,
                                 "<a href=\"http://www.tweetdeck.com/\" rel=\"nofollow\">TweetDeck</a>",
                                 "RT @jclouds: live multi #cloud demo of jclouds connecting to 3 storage clouds from google appengine http://is.gd/4IXMh",
                                 false,
                                 new User(
                                          dateService.cDateParse("Tue Apr 28 15:29:42 +0000 2009"),
                                          "Some random guy who seems to care about cloud collisions at siliconANGLE.com",
                                          245,
                                          572,
                                          false,
                                          325,
                                          false,
                                          36093693,
                                          "San Francisco ",
                                          "James Watters",
                                          false,
                                          "C6E2EE",
                                          URI
                                                   .create("http://a1.twimg.com/profile_background_images/24067016/17361976.jpg"),
                                          true,
                                          URI
                                                   .create("http://a3.twimg.com/profile_images/445071063/tiktaalik-transitional-fossil_normal.png"),
                                          "1F98C7",
                                          "C6E2EE",
                                          "DAECF4",
                                          "663B12",
                                          false,
                                          "wattersjames",
                                          1964,
                                          "Pacific Time (US & Canada)",
                                          URI
                                                   .create("http://siliconangle.net/ver2/author/jwatters/"),
                                          -28800, false))

               );

      ParseStatusesFromJsonResponse parser = new ParseStatusesFromJsonResponse(i
               .getInstance(Gson.class));
      SortedSet<Status> response = parser.apply(is);
      assertEquals(response, expects);
   }
}
