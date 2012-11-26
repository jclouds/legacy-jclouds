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

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.rest.InsufficientResourcesException;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Tests handling of errors, particularly retry errors
 */
@Test(groups = "unit", testName = "NovaErrorHandlerTest")
public class NovaErrorHandlerTest {


   /**
    * Reponse received from Rackspace UK on November 14, 2012.
    */
   public static final String RACKSPACE_UK_JSON = "{ 'overLimit' : { 'code' : 413," +
                                                  " 'message' : 'OverLimit Retry...', " +
                                                  "'details' : 'Error Details...'," +
                                                  " 'retryAt' : '2012-11-14T21:51:28UTC' }}";

   public static final Date RACKSPACE_UK_DATE;

   static {
      Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      calendar.set(2012, Calendar.NOVEMBER, 14, 21, 51, 28);
      RACKSPACE_UK_DATE = calendar.getTime();
   }

   /**
    * Folsom response. This contains a delta in seconds to retry after, not a fixed time.
    *
    */
   public static final String FOLSOM_JSON = "{ 'overLimit': " +
                                            "{" +
                                            " 'message': 'This request was rate-limited.', " +
                                            " 'retryAfter': '54', " +
                                            " 'details': 'Only 1 POST request(s) can be made to \\'*\\' every minute.'" +
                                            " }" +
                                            "}";

   /**
    * The value of the retryAfter field in the folsom sample
    */
   public static final int FOLSOM_RETRY_DELAY = 54;

   /**
    * Folsom response with a retryAt field inserted -at a different date. This can be used to verify
    * that the retryAfter field is picked up first
    */
   public static final String FOLSOM_JSON_WITH_RETRY_AT = "{ 'overLimit': " +
                                            "{" +
                                            " 'message': 'This request was rate-limited.', " +
                                            " 'retryAfter': '54', " +
                                            " 'retryAt' : '2012-11-14T21:51:28UTC'," +
                                            " 'details': 'Only 1 POST request(s) can be made to \\'*\\' every minute.'" +
                                            " }" +
                                            "}";


   public void testParseRackspaceUKResponse() throws Exception {
      NovaErrorHandler novaErrorHandler = createErrorHandler();
      Date date = novaErrorHandler.parseRetryAtField(RACKSPACE_UK_JSON);
      assertNotNull(date);verifyDateIsAsExpected(date);
   }

   public void testBuildExceptionFromRackspaceUK() throws Exception {
      //build the exception with a current time matching the response time
      validateRetryAtExceptionGeneration(RACKSPACE_UK_DATE, 0);
   }

   public void testBuildExceptionFromRackspaceUKPositiveTime() throws Exception {
      //build the exception with a current time  the response time
      Date earlierDate = new Date(RACKSPACE_UK_DATE.getTime() - 20000);
      validateRetryAtExceptionGeneration(earlierDate, 20);
   }

   public void testBuildExceptionFromRackspaceUKNegativeTime() throws Exception {
      //build the exception with a current time  the response time
      validateRetryAtExceptionGeneration(new Date(RACKSPACE_UK_DATE.getTime()+20000), 0);
   }

   /**
    *  Parse a Folsom-formatted JSON response and expect it to be valid.
    *  The Folsom format automatically come as a delta; there is no need to
    *  calculate the offset. To verify this fact, a null current time is passed in -
    *  any attempt to reference it will trigger an exception.
    */
   public void testBuildExceptionFromFolsom() throws Exception {
      Exception ex = buildException(FOLSOM_JSON, null);
      RetryLaterException retryEx = convertToRetryLaterException(ex);
      assertAfterTime(retryEx, FOLSOM_RETRY_DELAY);
   }

   /**
    *  Parse a Folsom-formatted JSON response and expect it to be valid. 
    *  The Folsom format automatically come as a delta; there is no need to
    *  calculate the offset. To verify this fact, a null current time is passed in -
    *  any attempt to reference it will trigger an exception.
    */
   public void testBuildExceptionFromFolsomWithRetryAt() throws Exception {
      Exception ex = buildException(FOLSOM_JSON_WITH_RETRY_AT, RACKSPACE_UK_DATE);
      RetryLaterException retryEx = convertToRetryLaterException(ex);
      assertAfterTime(retryEx, FOLSOM_RETRY_DELAY);
   }

   public void testNegativeRetryAfter() throws Exception {
      Exception ex = buildException(retryAfterJSON("-1"), null);
      RetryLaterException retryEx = convertToRetryLaterException(ex);
      assertAfterTime(retryEx, 0);
   }

   public void testEmptyPayload() {
      assertBadJsonHandledAsInsufficientResources("");
   }

   public void testNullPayload() {
      assertBadJsonHandledAsInsufficientResources("null");
   }

   public void testbracePayload() {
      assertBadJsonHandledAsInsufficientResources("{");
   }

   public void testNoFieldHandled() {
      assertBadJsonHandledAsInsufficientResources("{ 'overLimit' : {}}");
   }

   public void testRetryAtTuesday() {
      assertBadJsonHandledAsInsufficientResources(retryAtJSON("'tuesday'"));
   }


   public void testRetryAtZero() {
      assertBadJsonHandledAsInsufficientResources(retryAtJSON("0"));
   }

   public void testRetryAtNull() {
      assertBadJsonHandledAsInsufficientResources(retryAtJSON("null"));
   }


   /**
    * Create an error handler with the injected date service
    * @return an error handler
    */
   private NovaErrorHandler createErrorHandler() {
      return new NovaErrorHandler(new SimpleDateFormatDateService());
   }

   /**
    * Compare the values of the date with those in the {@link #RACKSPACE_UK_JSON} payload
    * @param date date to validate
    */
   private void verifyDateIsAsExpected(Date date) {
      assertNotNull(date,"Null date");
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(date);
      assertEquals(calendar.get(Calendar.MONTH), Calendar.NOVEMBER);
      assertEquals(calendar.get(Calendar.YEAR), 2012);
      assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 14);
      assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 21);
      assertEquals(calendar.get(Calendar.MINUTE), 51);
      assertEquals(calendar.get(Calendar.SECOND), 28);
      //check it is in UTC
      assertEquals(calendar.get(Calendar.ZONE_OFFSET), 0);
   }

   private void validateRetryAtExceptionGeneration(Date date, int expectedAtValue) throws Exception {
      Exception ex = buildException(RACKSPACE_UK_JSON, date);
      RetryLaterException retryEx = convertToRetryLaterException(ex);
      assertAfterTime(retryEx, expectedAtValue);
   }

   /**
    * If the supplied exception is an {@link RetryLaterException} then it is type cast and return.
    * If it is not, the exception is thrown up for the test runner to report
    * @param ex incoming exception
    * @return the cast exception
    * @throws Exception if the ex paramter is of the wrong type
    */
   private RetryLaterException convertToRetryLaterException(Exception ex) throws Exception {
      if (ex.getClass() != RetryLaterException.class) {
         throw ex;
      }
      RetryLaterException retryEx = (RetryLaterException) ex;
      return (RetryLaterException) ex;
   }

   /**
    * Assert that the time interval that the exception should use is as excpected
    * @param retryEx the exception
    * @param expectedAfterInterval the expected interval
    */
   private void assertAfterTime(RetryLaterException retryEx, int expectedAfterInterval) {
      assertEquals(retryEx.getRetryAfter(), expectedAfterInterval, retryEx.toString());
   }

   /**
    * Build an exception
    * @param json JSON to parse
    * @param now the current time -only needed if the JSON will have a retryAt field parsed.
    * @return the generated exception
    */
   private Exception buildException(String json, Date now) {
      NovaErrorHandler novaErrorHandler = createErrorHandler();
      return novaErrorHandler.buildRetryException("GET /", json, json, new Exception("stub"), now);
   }

   /**
    * Generate a retryAfter payloaded JSON with the given string as its value.
    * @param val the value to insert. This is not quoted for JSON; use quotes if a string type is desired.
    * @return the created JSON (which may not parse, depending on the arguments)
    */
   private String retryAfterJSON(String val) {
      return "{ 'overLimit' : { 'retryAfter' : " + val + " }}";
   }


   /**
    * Generate a retryAt payloaded JSON with the given string as its value.
    * @param val the value to insert. This is not quoted for JSON; use quotes if a string type is desired.
    * @return the created JSON (which may not parse, depending on the arguments)
    */
   private String retryAtJSON(String s) {
      return "{ 'overLimit' : { 'retryAt' : " + s + " }}";
   }

   /**
    * Assert that the bad json is converted to an {@link InsufficientResourcesException} and not
    * somehow parsed.
    * @param json the JSON to parse
    */
   private void assertBadJsonHandledAsInsufficientResources(String json) {
      Exception ex = buildException(json, RACKSPACE_UK_DATE);
      assertEquals(ex.getClass(), InsufficientResourcesException.class);
   }


}
