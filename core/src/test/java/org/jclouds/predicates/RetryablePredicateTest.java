package org.jclouds.predicates;

import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit", sequential = true, testName = "jclouds.RetryablePredicateTest")
public class RetryablePredicateTest {

   @Test
   void testAlwaysTrue() {
      RetryablePredicate<String> predicate = new RetryablePredicate<String>(Predicates
               .<String> alwaysTrue(), 3, 1, TimeUnit.SECONDS);
      DateTime start = new DateTime();
      predicate.apply("");
      DateTime now = new DateTime();
      assert now.compareTo(start.plusSeconds(1)) < 0 : String.format("%s should be less than %s",
               now,  start.plusSeconds(1));   }

   @Test
   void testAlwaysFalseMillis() {
      RetryablePredicate<String> predicate = new RetryablePredicate<String>(Predicates
               .<String> alwaysFalse(), 3, 1, TimeUnit.SECONDS);
      DateTime start = new DateTime();
      predicate.apply("");
      DateTime now = new DateTime();
      assert now.compareTo(start.plusSeconds(3)) >= 0 : String.format("%s should be less than %s",
               start.plusSeconds(3), now);
      assert now.compareTo(start.plusSeconds(4)) <= 0 : String.format(
               "%s should be greater than %s", start.plusSeconds(6), now);

   }

   private static class SecondTimeTrue implements Predicate<String> {

      private int count = 0;

      @Override
      public boolean apply(String input) {
         return count++ == 1;
      }

   }

   @Test
   void testSecondTimeTrue() {
      RetryablePredicate<String> predicate = new RetryablePredicate<String>(new SecondTimeTrue(),
               3, 1, TimeUnit.SECONDS);

      DateTime start = new DateTime();
      predicate.apply("");
      DateTime now = new DateTime();
      assert now.compareTo(start.plusSeconds(1)) >= 0 : String.format("%s should be greater than %s",
                now,start.plusSeconds(1));
      assert now.compareTo(start.plusSeconds(2)) <= 0 : String.format(
               "%s should be greater than %s", start.plusSeconds(2), now);
   }

}