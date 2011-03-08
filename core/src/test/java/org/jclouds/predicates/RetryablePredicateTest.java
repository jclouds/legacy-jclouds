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

package org.jclouds.predicates;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit", sequential = true)
public class RetryablePredicateTest {
   public static int SLOW_BUILD_SERVER_GRACE = 50;

   @Test
   void testFalseOnIllegalStateExeception() {
      ensureImmediateReturnFor(new IllegalStateException());
   }

   @SuppressWarnings("serial")
   @Test
   void testFalseOnExecutionException() {
      ensureImmediateReturnFor(new ExecutionException() {
      });
   }

   @SuppressWarnings("serial")
   @Test
   void testFalseOnTimeoutException() {
      ensureImmediateReturnFor(new TimeoutException() {
      });
   }

   @SuppressWarnings("serial")
   @Test(expectedExceptions = RuntimeException.class)
   void testPropagateOnException() {
      ensureImmediateReturnFor(new Exception() {
      });
   }

   private void ensureImmediateReturnFor(final Exception ex) {
      RetryablePredicate<Supplier<String>> predicate = new RetryablePredicate<Supplier<String>>(
               new Predicate<Supplier<String>>() {

                  @Override
                  public boolean apply(Supplier<String> input) {
                     return "goo".equals(input.get());
                  }

               }, 3, 1, TimeUnit.SECONDS);
      Date startPlusThird = new Date(System.currentTimeMillis() + 1000);
      assert !predicate.apply(new Supplier<String>() {

         @Override
         public String get() {
            throw new RuntimeException(ex);
         }

      });
      Date now = new Date();
      assert now.compareTo(startPlusThird) < 0 : String.format("%s should be less than %s", now.getTime(),
               startPlusThird.getTime());
   }

   @Test
   void testAlwaysTrue() {
      RetryablePredicate<String> predicate = new RetryablePredicate<String>(Predicates.<String> alwaysTrue(), 3, 1,
               TimeUnit.SECONDS);
      Date startPlusThird = new Date(System.currentTimeMillis() + 1000);
      predicate.apply("");
      Date now = new Date();
      assert now.compareTo(startPlusThird) < 0 : String.format("%s should be less than %s", now.getTime(),
               startPlusThird.getTime());
   }

   @Test
   void testAlwaysFalseMillis() {
      RetryablePredicate<String> predicate = new RetryablePredicate<String>(Predicates.<String> alwaysFalse(), 3, 1,
               TimeUnit.SECONDS);
      Date startPlus3Seconds = new Date(System.currentTimeMillis() + 3000);
      Date startPlus4Seconds = new Date(System.currentTimeMillis() + 4000 + SLOW_BUILD_SERVER_GRACE);
      predicate.apply("");
      Date now = new Date();
      assert now.compareTo(startPlus3Seconds) >= 0 : String.format("%s should be less than %s", startPlus3Seconds
               .getTime(), now.getTime());
      assert now.compareTo(startPlus4Seconds) <= 0 : String.format("%s should be greater than %s", startPlus4Seconds
               .getTime(), now.getTime());

   }

   private static class ThirdTimeTrue implements Predicate<String> {

      private int count = 0;

      @Override
      public boolean apply(String input) {
         return count++ == 2;
      }

   }

   @Test
   void testThirdTimeTrue() {
      RetryablePredicate<String> predicate = new RetryablePredicate<String>(new ThirdTimeTrue(), 3, 1, TimeUnit.SECONDS);

      Date startPlus = new Date(System.currentTimeMillis() + 1000);
      Date startPlus3 = new Date(System.currentTimeMillis() + 3000 + SLOW_BUILD_SERVER_GRACE);

      predicate.apply("");
      Date now = new Date();
      assert now.compareTo(startPlus) >= 0 : String.format("%s should be greater than %s", now.getTime(), startPlus
               .getTime());
      assert now.compareTo(startPlus3) <= 0 : String.format("%s should be greater than %s", startPlus3.getTime(), now
               .getTime());
   }

   @Test
   void testThirdTimeTrueLimitedMaxInterval() {
      RetryablePredicate<String> predicate = new RetryablePredicate<String>(new ThirdTimeTrue(), 3, 1, 1,
               TimeUnit.SECONDS);

      Date startPlus = new Date(System.currentTimeMillis() + 1000);
      Date startPlus2 = new Date(System.currentTimeMillis() + 2000 + SLOW_BUILD_SERVER_GRACE);

      predicate.apply("");
      Date now = new Date();
      assert now.compareTo(startPlus) >= 0 : String.format("%s should be greater than %s", now.getTime(), startPlus
               .getTime());
      assert now.compareTo(startPlus2) <= 0 : String.format("%s should be greater than %s", startPlus2.getTime(), now
               .getTime());
   }
}