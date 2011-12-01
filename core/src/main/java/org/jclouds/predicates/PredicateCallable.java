package org.jclouds.predicates;

import java.util.concurrent.Callable;

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
