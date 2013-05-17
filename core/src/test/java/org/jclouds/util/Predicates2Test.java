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
package org.jclouds.util;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true)
public class Predicates2Test {
   // Grace must be reasonably big; Thread.sleep can take a bit longer to wake up sometimes...
   public static int SLOW_BUILD_SERVER_GRACE = 250;

   // Sometimes returns sooner than timer would predict (e.g. observed 2999ms, when expected 3000ms)
   public static int EARLY_RETURN_GRACE = 10;

   private Stopwatch stopwatch;

   @BeforeMethod
   public void setUp() {
      stopwatch = new Stopwatch();
   }
   
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
      Predicate<Supplier<String>> predicate = retry(
               new Predicate<Supplier<String>>() {
                  public boolean apply(Supplier<String> input) {
                     return "goo".equals(input.get());
                  }
               }, 3, 1, SECONDS);
      
      stopwatch.start();
      assert !predicate.apply(new Supplier<String>() {

         @Override
         public String get() {
            throw new RuntimeException(ex);
         }

      });
      long duration = stopwatch.elapsed(MILLISECONDS);
      assertOrdered(duration, SLOW_BUILD_SERVER_GRACE);
   }

   @Test
   void testAlwaysTrue() {
      // will call once immediately
      Predicate<String> predicate = retry(Predicates.<String> alwaysTrue(), 3, 1, SECONDS);
      stopwatch.start();
      predicate.apply("");
      long duration = stopwatch.elapsed(MILLISECONDS);
      assertOrdered(duration, SLOW_BUILD_SERVER_GRACE);
   }

   @Test
   void testAlwaysFalseMillis() {
      // maxWait=3; period=1; maxPeriod defaults to 1*10
      // will call at 0, 1, 1+(1*1.5), 3
      Predicate<String> predicate = retry(Predicates.<String> alwaysFalse(), 3, 1, SECONDS);
      stopwatch.start();
      predicate.apply("");
      long duration = stopwatch.elapsed(MILLISECONDS);
      assertOrdered(3000-EARLY_RETURN_GRACE, duration, 3000+SLOW_BUILD_SERVER_GRACE);
   }

   @Test
   void testThirdTimeTrue() {
      // maxWait=4; period=1; maxPeriod defaults to 1*10
      // will call at 0, 1, 1+(1*1.5)
      RepeatedAttemptsPredicate rawPredicate = new RepeatedAttemptsPredicate(2);
      Predicate<String> predicate = retry(rawPredicate, 4, 1, SECONDS);

      stopwatch.start();
      predicate.apply("");
      long duration = stopwatch.elapsed(MILLISECONDS);
      
      assertOrdered(2500-EARLY_RETURN_GRACE, duration, 2500+SLOW_BUILD_SERVER_GRACE);
      assertCallTimes(rawPredicate.callTimes, 0, 1000, 1000+1500);
   }

   @Test
   void testThirdTimeTrueLimitedMaxInterval() {
      // maxWait=3; period=1; maxPeriod=1
      // will call at 0, 1, 1+1
      RepeatedAttemptsPredicate rawPredicate = new RepeatedAttemptsPredicate(2);
      Predicate<String> predicate = retry(rawPredicate, 3, 1, 1, SECONDS);

      stopwatch.start();
      predicate.apply("");
      long duration = stopwatch.elapsed(MILLISECONDS);
      
      assertOrdered(2000-EARLY_RETURN_GRACE, duration, 2000+SLOW_BUILD_SERVER_GRACE);
      assertCallTimes(rawPredicate.callTimes, 0, 1000, 2000);
   }
   
   public static class RepeatedAttemptsPredicate implements Predicate<String> {
      final List<Long> callTimes = Lists.newArrayList();
      private final int succeedOnAttempt;
      private final Stopwatch stopwatch;
      private int count = 0;
      
      RepeatedAttemptsPredicate(int succeedOnAttempt) {
         this.succeedOnAttempt = succeedOnAttempt;
         this.stopwatch = new Stopwatch();
         stopwatch.start();
      }
      @Override
      public boolean apply(String input) {
         callTimes.add(stopwatch.elapsed(MILLISECONDS));
         return count++ == succeedOnAttempt;
      }
   }
   
   @Test(enabled=false) // not a test, but picked up as such because public
   public static void assertCallTimes(List<Long> actual, Integer... expected) {
      Assert.assertEquals(actual.size(), expected.length);
      for (int i = 0; i < expected.length; i++) {
         long callTime = actual.get(i);
         assertOrdered(expected[i]-EARLY_RETURN_GRACE, callTime, expected[i]+SLOW_BUILD_SERVER_GRACE);
      }
   }
   
   private static void assertOrdered(long... values) {
      long prevVal = values[0];
      for (long val : values) {
         if (val < prevVal) {
            fail(String.format("%s should be ordered", Arrays.toString(values)));
         }
      }
   }
}
