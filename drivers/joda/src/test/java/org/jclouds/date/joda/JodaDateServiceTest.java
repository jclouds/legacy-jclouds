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
package org.jclouds.date.joda;

import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.jclouds.date.DateService;
import org.jclouds.date.DateServiceTest;
import org.jclouds.date.joda.config.JodaDateServiceModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/* 
 * TODO: Scrap any non-DateService references (eg Joda & Amazon) if/when
 * we confirm that the DateService is fast enough.
 */

/**
 * Compares performance of date operations
 * 
 * @author Adrian Cole
 * @author James Murty
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "performance", singleThreaded = true, timeOut = 2 * 60 * 1000, testName = "JodaDateServiceTest")
public class JodaDateServiceTest extends DateServiceTest {
   @Override
   @BeforeTest
   protected void createDateService() {
      Injector i = Guice.createInjector(new JodaDateServiceModule());
      dateService = i.getInstance(DateService.class);
      assert dateService instanceof JodaDateService;
   }

   @Override
   @Test
   public void testRfc822DateFormat() {
      String dsString = dateService.rfc822DateFormat(testData[0].date);
      assertEquals(dsString, testData[0].rfc822DateString);
   }

   @Override
   @Test(enabled = false)
   public void testRfc822DateParse() {
      Date dsDate = dateService.rfc822DateParse(testData[0].rfc822DateString);
      assertEquals(dsDate, testData[0].date);
   }
}
