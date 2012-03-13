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

import static org.jclouds.predicates.RetryablePredicateTest.assertCallTimes;
import static org.testng.Assert.fail;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.predicates.RetryablePredicateTest.RepeatedAttemptsPredicate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true)
public class RetryableNumTimesPredicateTest {
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
      RetryableNumTimesPredicate<Supplier<String>> predicate = new RetryableNumTimesPredicate<Supplier<String>>(
               new Predicate<Supplier<String>>() {

                  @Override
                  public boolean apply(Supplier<String> input) {
                     return "goo".equals(input.get());
                  }

               }, 3, 1L, TimeUnit.SECONDS);
      
      stopwatch.start();
      assert !predicate.apply(new Supplier<String>() {

         @Override
         public String get() {
            throw new RuntimeException(ex);
         }

      });
      long duration = stopwatch.elapsedMillis();
      assertOrdered(duration, SLOW_BUILD_SERVER_GRACE);
   }

   @Test
   void testAlwaysTrue() {
      // will call once immediately
      RetryableNumTimesPredicate<String> predicate = new RetryableNumTimesPredicate<String>(Predicates.<String> alwaysTrue(), 
               1, 1L, TimeUnit.SECONDS);
      stopwatch.start();
      predicate.apply("");
      long duration = stopwatch.elapsedMillis();
      assertOrdered(duration, SLOW_BUILD_SERVER_GRACE);
   }

   @Test
   void testAlwaysFalseMillis() {
      // maxAttempts=3; period=1; maxPeriod defaults to 1*10
      // will call at 0, 1, 1+(1*1.5) = 2.5secs
      RetryableNumTimesPredicate<String> predicate = new RetryableNumTimesPredicate<String>(Predicates.<String> alwaysFalse(), 
               3, 1L, TimeUnit.SECONDS);
      stopwatch.start();
      predicate.apply("");
      long duration = stopwatch.elapsedMillis();
      assertOrdered(2500-EARLY_RETURN_GRACE, duration, 2500+SLOW_BUILD_SERVER_GRACE);
   }

   @Test
   void testThirdTimeTrue() {
      // maxAttempts=3; period=1; maxPeriod defaults to 1*10
      // will call at 0, 1, 1+(1*1.5)
      RepeatedAttemptsPredicate rawPredicate = new RepeatedAttemptsPredicate(2);
      RetryableNumTimesPredicate<String> predicate = new RetryableNumTimesPredicate<String>(rawPredicate, 
               4, 1, TimeUnit.SECONDS);

      stopwatch.start();
      predicate.apply("");
      long duration = stopwatch.elapsedMillis();
      
      assertOrdered(2500-EARLY_RETURN_GRACE, duration, 2500+SLOW_BUILD_SERVER_GRACE);
      assertCallTimes(rawPredicate.callTimes, 0, 1000, 1000+1500);
   }

   @Test
   void testThirdTimeTrueLimitedMaxInterval() {
      // maxAttempts=3; period=1; maxPeriod=1
      // will call at 0, 1, 1+1
      RepeatedAttemptsPredicate rawPredicate = new RepeatedAttemptsPredicate(2);
      RetryableNumTimesPredicate<String> predicate = new RetryableNumTimesPredicate<String>(rawPredicate, 
               3, 1L, 1L, TimeUnit.SECONDS);

      stopwatch.start();
      predicate.apply("");
      long duration = stopwatch.elapsedMillis();
      
      assertOrdered(2000-EARLY_RETURN_GRACE, duration, 2000+SLOW_BUILD_SERVER_GRACE);
      assertCallTimes(rawPredicate.callTimes, 0, 1000, 2000);
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
