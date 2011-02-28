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
import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit", sequential = true)
public class RetryablePredicateTest {

   @Test
   void testAlwaysTrue() {
      RetryablePredicate<String> predicate = new RetryablePredicate<String>(Predicates.<String> alwaysTrue(), 3, 1,
               TimeUnit.SECONDS);
      Date startPlusThird = new Date(System.currentTimeMillis() + 1000);
      predicate.apply("");
      Date now = new Date();
      assert now.compareTo(startPlusThird) < 0 : String.format("%s should be less than %s", now, startPlusThird);
   }

   @Test
   void testAlwaysFalseMillis() {
      RetryablePredicate<String> predicate = new RetryablePredicate<String>(Predicates.<String> alwaysFalse(), 3, 1,
               TimeUnit.SECONDS);
      Date startPlus3Thirds = new Date(System.currentTimeMillis() + 3000);
      Date startPlus4Thirds = new Date(System.currentTimeMillis() + 4000);
      predicate.apply("");
      Date now = new Date();
      assert now.compareTo(startPlus3Thirds) >= 0 : String.format("%s should be less than %s", startPlus3Thirds, now);
      assert now.compareTo(startPlus4Thirds) <= 0 : String
               .format("%s should be greater than %s", startPlus4Thirds, now);

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
      Date startPlus3 = new Date(System.currentTimeMillis() + 3000);

      predicate.apply("");
      Date now = new Date();
      assert now.compareTo(startPlus) >= 0 : String.format("%s should be greater than %s", now, startPlus);
      assert now.compareTo(startPlus3) <= 0 : String.format("%s should be greater than %s", startPlus3, now);
   }

   @Test
   void testThirdTimeTrueLimitedMaxInterval() {
      RetryablePredicate<String> predicate = new RetryablePredicate<String>(new ThirdTimeTrue(), 3, 1, 1,
               TimeUnit.SECONDS);

      Date startPlus = new Date(System.currentTimeMillis() + 1000);
      Date startPlus2 = new Date(System.currentTimeMillis() + 2000);

      predicate.apply("");
      Date now = new Date();
      assert now.compareTo(startPlus) >= 0 : String.format("%s should be greater than %s", now, startPlus);
      assert now.compareTo(startPlus2) <= 0 : String.format("%s should be greater than %s", startPlus2, now);
   }
}