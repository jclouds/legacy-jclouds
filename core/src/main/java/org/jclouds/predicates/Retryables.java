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

import java.util.concurrent.TimeUnit;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;

/** convenience methods to retry application of a predicate, and optionally (reducing quite a bit of boilerplate)
 * get the final result or throw assertion error
 * 
 * @author alex heneveld
 */
@Beta
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
