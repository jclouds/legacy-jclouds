package org.jclouds.predicates;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicate;

public class Retryables {

   public static <Input> boolean retry(Predicate<Input> predicate, Input input, long maxWaitMillis) {
      return new RetryablePredicate<Input>(predicate, maxWaitMillis).apply(input);
   }
   
   public static <Input> boolean retry(Predicate<Input> predicate, Input input, long maxWait, long period, TimeUnit unit) {
      return new RetryablePredicate<Input>(predicate, maxWait, period, unit).apply(input);
   }

   public static <Input> void assertEventually(Predicate<Input> predicate, Input input, 
         long maxWaitMillis, String failureMessage) {
      if (!new RetryablePredicate<Input>(predicate, maxWaitMillis).apply(input))
         throw new AssertionError(failureMessage);
   }

   public static <Input,Result> Result retryGettingResultOrFailing(PredicateWithResult<Input,Result> predicate,
         Input input, long maxWaitMillis, String failureMessage) {
      if (!new RetryablePredicate<Input>(predicate, maxWaitMillis).apply(input))
         throw (AssertionError)new AssertionError(failureMessage).initCause(predicate.getLastFailure());
      return predicate.getResult();
   }
   public static <Input,Result> Result retryGettingResultOrFailing(PredicateWithResult<Input,Result> predicate,
         Input input, long maxWait, long period, TimeUnit unit, String failureMessage) {
      if (!new RetryablePredicate<Input>(predicate, maxWait, period, unit).apply(input))
         throw (AssertionError)new AssertionError(failureMessage).initCause(predicate.getLastFailure());
      return predicate.getResult();
   }

}
