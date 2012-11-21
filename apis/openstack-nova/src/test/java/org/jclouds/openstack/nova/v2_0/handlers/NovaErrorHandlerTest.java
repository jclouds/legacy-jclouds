/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */

package org.jclouds.openstack.nova.v2_0.handlers;

import org.jclouds.rest.InsufficientResourcesException;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Tests handling of errors, particularly retry errors
 */
@Test(groups = "unit", testName = "NovaErrorHandlerTest")
public class NovaErrorHandlerTest {


   public static final String VALID_JSON = "{ 'overLimit' : { 'code' : 413," +
                                           " 'message' : 'OverLimit Retry...', " +
                                           "'details' : 'Error Details...'," +
                                           " 'retryAt' : '2012-11-14T21:51:28UTC' }}";


/*
{ 'overLimit' : { 'code' : 413, 'message' : 'OverLimit Retry...', 'details' : 'Error Details...', 'retryAt' : '2012-11-14T21:51:28UTC' }}
*/

   public void testParseJsonValidly() throws Exception {
      NovaErrorHandler novaErrorHandler = new NovaErrorHandler();
      Date date = novaErrorHandler.parseRetryDate(VALID_JSON);
      assertNotNull(date);
      verifyDateIsAsExpected(date);
   }

   /**
    * Compare the values of the date with those in the {@link #VALID_JSON} payload
    * @param date date to validate
    */
   private void verifyDateIsAsExpected(Date date) {
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(date);
      assertEquals(calendar.get(Calendar.MONTH) + 1, 11);
      assertEquals(calendar.get(Calendar.YEAR), 2012);
      assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 14);
      assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 21);
      assertEquals(calendar.get(Calendar.MINUTE), 51);
      assertEquals(calendar.get(Calendar.SECOND), 28);
      //check it is in UTC
      assertEquals(calendar.get(Calendar.ZONE_OFFSET), 0);
   }

   public void testBuildExceptionFromJson() throws Exception {
      String json = VALID_JSON;
      Exception ex = buildException(json);
      assertEquals(ex.getClass(), RetryAfterException.class);
      RetryAfterException retryEx = (RetryAfterException) ex;
      long t = retryEx.getRetryTime();
      Date d = new Date(t);
      verifyDateIsAsExpected(d);
   }

   private Exception buildException(String json) {
      NovaErrorHandler novaErrorHandler = new NovaErrorHandler();
      return novaErrorHandler.buildRetryAfterException("GET /", json, json, new Exception("stub"));
   }

   public void testEmptyPayload() {
      assertBadJsonHandled("");
   }

   public void testNullPayload() {
      assertBadJsonHandled("null");
   }

   public void testbracePayload() {
      assertBadJsonHandled("{");
   }

   public void testDepth1() {
      assertBadJsonHandled("{ 'overLimit' : {}}");
   }

   public void testNotADate1() {
      assertBadJsonHandled("{ 'overLimit' : { 'retryAt' : 'tuesday' }}");
   }

   public void testNotADate2() {
      assertBadJsonHandled("{ 'overLimit' : { 'retryAt' : 0 }}");
   }

   public void testNotADate3() {
      assertBadJsonHandled("{ 'overLimit' : { 'retryAt' : null }}");
   }


   private void assertBadJsonHandled(String message) {
      Exception ex = buildException(message);
      assertEquals(ex.getClass(), InsufficientResourcesException.class);
   }


}
