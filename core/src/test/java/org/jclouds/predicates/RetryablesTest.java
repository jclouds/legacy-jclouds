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
package org.jclouds.predicates;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.predicates.Retryables.retry;
import static org.jclouds.predicates.Retryables.retryGettingResultOrFailing;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

@Test
public class RetryablesTest {

   public static class FindX implements PredicateWithResult<String,Character> {
      Character result;
      Throwable lastFailure;
      int attempts=0;
      @Override
      public boolean apply(String input) {
         try {
            result = input.charAt(attempts++);
            return (result=='x');
         } catch (Exception e) {
            lastFailure = e;
            return false;
         }
      }
      public Character getResult() {
         return result;
      }
      public Throwable getLastFailure() {
         return lastFailure;
      }
   }

   public void testPredicateWithResult() {
      FindX findX = new FindX();
      assertFalse(findX.apply("hexy"));
      assertEquals((char)findX.getResult(), 'h');
      assertFalse(findX.apply("hexy"));
      assertTrue(findX.apply("hexy"));
      assertEquals((char)findX.getResult(), 'x');
      
      assertFalse(findX.apply("hexy"));
      assertNull(findX.getLastFailure());
      //now we get error
      assertFalse(findX.apply("hexy"));
      assertNotNull(findX.getLastFailure());
      assertEquals((char)findX.getResult(), 'y');
   }

   public void testRetry() {
      FindX findX = new FindX();
      assertTrue(retry(findX, "hexy", 1000, 1, MILLISECONDS));
      assertEquals(findX.attempts, 3);
      assertEquals((char)findX.getResult(), 'x');
      assertNull(findX.getLastFailure());

      //now we'll be getting errors
      assertFalse(retry(findX, "hexy", 100, 1, MILLISECONDS));
      assertEquals((char)findX.getResult(), 'y');
      assertNotNull(findX.getLastFailure());
   }

   public void testRetryGetting() {
      FindX findX = new FindX();
      assertEquals((char)retryGettingResultOrFailing(findX, "hexy", 1000, "shouldn't happen"), 'x');

      //now we'll be getting errors
      boolean secondRetrySucceeds=false;
      try {
         retryGettingResultOrFailing(findX, "hexy", 100, "expected");
         secondRetrySucceeds = true;
      } catch (AssertionError e) {
         assertTrue(e.toString().contains("expected"));
      }
      if (secondRetrySucceeds) fail("should have thrown");
      assertNotNull(findX.getLastFailure());
      assertFalse(findX.getLastFailure().toString().contains("expected"));
   }

   //using PredicateCallable we can repeat the above test, with the job expressed more simply
   public static class FindXSimpler extends PredicateCallable<Character> {
      String input = "hexy";
      int attempts=0;
      public Character call() {
         return input.charAt(attempts++);
      }
      public boolean isAcceptable(Character result) {
         return result=='x';
      }
   }

   public void testSimplerPredicateCallableRetryGetting() {
      FindXSimpler findX = new FindXSimpler();
      assertEquals((char)retryGettingResultOrFailing(findX, null, 1000, "shouldn't happen"), 'x');

      //now we'll be getting errors
      boolean secondRetrySucceeds=false;
      try {
         retryGettingResultOrFailing(findX, null, 100, "expected");
         secondRetrySucceeds = true;
      } catch (AssertionError e) {
         assertTrue(e.toString().contains("expected"));
      }
      if (secondRetrySucceeds) fail("should have thrown");
      assertNotNull(findX.getLastFailure());
      assertFalse(findX.getLastFailure().toString().contains("expected"));
   }
   
}
