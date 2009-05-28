/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.util;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.jclouds.aws.PerformanceTest;
import org.joda.time.DateTime;
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
@Test(sequential = true, timeOut = 2 * 60 * 1000, testName = "s3.DateTest")
public class DateServiceTest extends PerformanceTest {
   Injector i = Guice.createInjector();

   DateService dateService = i.getInstance(DateService.class);

   protected TestData[] testData;

   protected class TestData {
      public final String iso8601DateString;
      public final String rfc822DateString;
      public final DateTime date;

      TestData(String iso8601, String rfc822, DateTime dateTime) {
         this.iso8601DateString = iso8601;
         this.rfc822DateString = rfc822;
         this.date = dateTime;
      }
   }

   public DateServiceTest() {
      // Constant time test values, each TestData item must contain matching times!
      testData = new TestData[] {
               new TestData("2009-03-12T02:00:07.000Z", "Thu, 12 Mar 2009 02:00:07 GMT",
                        new DateTime(1236823207000l)),
               new TestData("2009-03-14T04:00:07.000Z", "Sat, 14 Mar 2009 04:00:07 GMT",
                        new DateTime(1237003207000l)),
               new TestData("2009-03-16T06:00:07.000Z", "Mon, 16 Mar 2009 06:00:07 GMT",
                        new DateTime(1237183207000l)),
               new TestData("2009-03-18T08:00:07.000Z", "Wed, 18 Mar 2009 08:00:07 GMT",
                        new DateTime(1237363207000l)),
               new TestData("2009-03-20T10:00:07.000Z", "Fri, 20 Mar 2009 10:00:07 GMT",
                        new DateTime(1237543207000l)) };
   }

   @Test
   public void testIso8601DateParse() throws ExecutionException, InterruptedException {
      DateTime dsDate = dateService.iso8601DateParse(testData[0].iso8601DateString);
      assertEquals(dsDate, testData[0].date);
   }

   @Test
   public void testRfc822DateParse() throws ExecutionException, InterruptedException {
      DateTime dsDate = dateService.rfc822DateParse(testData[0].rfc822DateString);
      assertEquals(dsDate, testData[0].date);
   }

   @Test
   public void testIso8601DateFormat() throws ExecutionException, InterruptedException {
      String dsString = dateService.iso8601DateFormat(testData[0].date);
      assertEquals(dsString, testData[0].iso8601DateString);
   }

   @Test
   public void testRfc822DateFormat() throws ExecutionException, InterruptedException {
      String dsString = dateService.rfc822DateFormat(testData[0].date);
      assertEquals(dsString, testData[0].rfc822DateString);
   }

   @Test
   void testIso8601DateFormatResponseTime() throws ExecutionException, InterruptedException {
      for (int i = 0; i < LOOP_COUNT; i++)
         dateService.iso8601DateFormat();
   }

   @Test
   void testRfc822DateFormatResponseTime() throws ExecutionException, InterruptedException {
      for (int i = 0; i < LOOP_COUNT; i++)
         dateService.rfc822DateFormat();
   }

   @Test
   void testFormatIso8601DateCorrectnessInParallel() throws Throwable {
      List<Runnable> tasks = new ArrayList<Runnable>(testData.length);
      for (final TestData myData : testData) {
         tasks.add(new Runnable() {
            public void run() {
               String dsString = dateService.iso8601DateFormat(myData.date);
               assertEquals(dsString, myData.iso8601DateString);
            }
         });
      }
      executeMultiThreadedCorrectnessTest(tasks);
   }

   @Test
   void testFormatIso8601DatePerformanceInParallel() throws Throwable {
      List<Runnable> tasks = new ArrayList<Runnable>(testData.length);
      for (final TestData myData : testData) {
         tasks.add(new Runnable() {
            public void run() {
               dateService.iso8601DateFormat(myData.date);
            }
         });
      }
      executeMultiThreadedPerformanceTest("testFormatIso8601DatePerformanceInParallel", tasks);
   }

   @Test
   void testFormatIso8601DatePerformanceInParallel_SdfAlternative() throws Throwable {
      List<Runnable> tasks = new ArrayList<Runnable>(testData.length);
      for (final TestData myData : testData) {
         tasks.add(new Runnable() {
            public void run() {
               dateService.sdfIso8601DateFormat(myData.date);
            }
         });
      }
      executeMultiThreadedPerformanceTest(
               "testFormatIso8601DatePerformanceInParallel_SdfAlternative", tasks);
   }

   @Test
   void testParseIso8601DateSerialResponseTime() throws ExecutionException, InterruptedException {
      for (int i = 0; i < LOOP_COUNT; i++)
         dateService.iso8601DateParse(testData[0].iso8601DateString);
   }

   @Test
   void testParseIso8601DateSerialResponseTime_JodaAlternative() throws ExecutionException,
            InterruptedException {
      for (int i = 0; i < LOOP_COUNT; i++)
         dateService.jodaIso8601DateParse(testData[0].iso8601DateString);
   }

   @Test
   void testParseIso8601DateCorrectnessInParallel() throws Throwable {
      List<Runnable> tasks = new ArrayList<Runnable>(testData.length);
      for (final TestData myData : testData) {
         tasks.add(new Runnable() {
            public void run() {
               DateTime dsDate = dateService.iso8601DateParse(myData.iso8601DateString);
               assertEquals(dsDate, myData.date);
            }
         });
      }
      executeMultiThreadedCorrectnessTest(tasks);
   }

   @Test
   void testParseIso8601DatePerformanceInParallel() throws Throwable {
      List<Runnable> tasks = new ArrayList<Runnable>(testData.length);
      for (final TestData myData : testData) {
         tasks.add(new Runnable() {
            public void run() {
               dateService.iso8601DateParse(myData.iso8601DateString);
            }
         });
      }
      executeMultiThreadedPerformanceTest("testParseIso8601DatePerformanceInParallel", tasks);
   }

   @Test
   void testParseIso8601DatePerformanceInParallel_JodaAlternative() throws Throwable {
      List<Runnable> tasks = new ArrayList<Runnable>(testData.length);
      for (final TestData myData : testData) {
         tasks.add(new Runnable() {
            public void run() {
               dateService.jodaIso8601DateParse(myData.iso8601DateString);
            }
         });
      }
      executeMultiThreadedPerformanceTest(
               "testParseIso8601DatePerformanceInParallel_JodaAlternative", tasks);
   }

}