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

import java.util.concurrent.Callable;

import com.google.common.annotations.Beta;

/** Provides a facility to convert an arbitrary Callable to a Predicate, implementing PredicateWithResult,
 * for use e.g. with Retryables.retryGetting... methods */
@Beta
public abstract class PredicateCallable<Result> implements PredicateWithResult<Void, Result>, Callable<Result> {

   Result lastResult;
   Exception lastFailure;
   
   @Override
   public boolean apply(Void input) {
      try {
         lastResult = call();
         onCompletion();
         return isAcceptable(lastResult);
      } catch (Exception e) {
         lastFailure = e;
         onFailure();
         return false;
      }
   }

   protected void onFailure() {
   }

   protected void onCompletion() {
   }
   
   protected boolean isAcceptable(Result result) {
      return result!=null;
   }
   
   @Override
   public Result getResult() {
      return lastResult;
   }

   @Override
   public Throwable getLastFailure() {
      return lastFailure;
   }

}
