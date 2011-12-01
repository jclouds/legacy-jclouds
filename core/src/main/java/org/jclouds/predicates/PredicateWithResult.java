package org.jclouds.predicates;

import com.google.common.base.Predicate;

public interface PredicateWithResult<Input,Result> extends Predicate<Input> {
   
   Result getResult();
   
   Throwable getLastFailure();
   
}
