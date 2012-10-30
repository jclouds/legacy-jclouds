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
package org.jclouds.concurrent.config;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DescribedFuture<T> implements Future<T> {
   protected final Future<T> delegate;
   private final String description;
   private StackTraceElement[] submissionTrace;

   public DescribedFuture(Future<T> delegate, String description, StackTraceElement[] submissionTrace) {
      this.delegate = delegate;
      this.description = description;
      this.submissionTrace = submissionTrace;
   }

   @Override
   public boolean cancel(boolean arg0) {
      return delegate.cancel(arg0);
   }

   @Override
   public T get() throws InterruptedException, ExecutionException {
      try {
         return delegate.get();
      } catch (ExecutionException e) {
         throw ensureCauseHasSubmissionTrace(e);
      } catch (InterruptedException e) {
         throw ensureCauseHasSubmissionTrace(e);
      }
   }

   @Override
   public T get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException {
      try {
         return delegate.get(arg0, arg1);
      } catch (ExecutionException e) {
         throw ensureCauseHasSubmissionTrace(e);
      } catch (InterruptedException e) {
         throw ensureCauseHasSubmissionTrace(e);
      } catch (TimeoutException e) {
         throw ensureCauseHasSubmissionTrace(e);
      }
   }

   /** This method does the work to ensure _if_ a submission stack trace was provided,
    * it is included in the exception.  most errors are thrown from the frame of the
    * Future.get call, with a cause that took place in the executor's thread.
    * We extend the stack trace of that cause with the submission stack trace.
    * (An alternative would be to put the stack trace as a root cause,
    * at the bottom of the stack, or appended to all traces, or inserted
    * after the second cause, etc ... but since we can't change the "Caused by:"
    * method in Throwable the compromise made here seems best.)
    */
   private <ET extends Exception> ET ensureCauseHasSubmissionTrace(ET e) {
      if (submissionTrace==null) return e;
      if (e.getCause()==null) {
         ExecutionException ee = new ExecutionException("task submitted from the following trace", null);
         e.initCause(ee);
         return e;
      }
      Throwable cause = e.getCause();
      StackTraceElement[] causeTrace = cause.getStackTrace();
      boolean causeIncludesSubmissionTrace = submissionTrace.length >= causeTrace.length;
      for (int i=0; causeIncludesSubmissionTrace && i<submissionTrace.length; i++) {
         if (!causeTrace[causeTrace.length-1-i].equals(submissionTrace[submissionTrace.length-1-i])) {
            causeIncludesSubmissionTrace = false;
         }
      }
      
      if (!causeIncludesSubmissionTrace) {
         cause.setStackTrace(merge(causeTrace, submissionTrace));
      }
      
      return e;
   }

   private StackTraceElement[] merge(StackTraceElement[] t1, StackTraceElement[] t2) {
      StackTraceElement[] t12 = new StackTraceElement[t1.length + t2.length];
      System.arraycopy(t1, 0, t12, 0, t1.length);
      System.arraycopy(t2, 0, t12, t1.length, t2.length);
      return t12;
   }

   @Override
   public boolean isCancelled() {
      return delegate.isCancelled();
   }

   @Override
   public boolean isDone() {
      return delegate.isDone();
   }

   @Override
   public boolean equals(Object obj) {
      return delegate.equals(obj);
   }

   @Override
   public int hashCode() {
      return delegate.hashCode();
   }

   @Override
   public String toString() {
      return description;
   }

}
