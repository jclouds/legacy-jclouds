/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
package com.amazon.s3;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

/**
 * Compares performance of date operations
 * 
 * @author Adrian Cole
 * @author James Murty
 */
@Test(sequential = true, timeOut = 2 * 60 * 1000, testName = "s3.DateTest")
public class DateServiceTest extends org.jclouds.aws.util.DateServiceTest {

   @Test
   void testAmazonParseDateSerialResponseTime() {
      for (int i = 0; i < LOOP_COUNT; i++)
         AWSAuthConnection.httpDate();
   }

   @Test
   void testFormatAmazonDatePerformanceInParallel() throws Throwable {
      List<Runnable> tasks = new ArrayList<Runnable>(testData.length);
      tasks.add(new Runnable() {
         public void run() {
            AWSAuthConnection.httpDate();
         }
      });
      executeMultiThreadedPerformanceTest("testFormatAmazonDatePerformanceInParallel", tasks);
   }

}