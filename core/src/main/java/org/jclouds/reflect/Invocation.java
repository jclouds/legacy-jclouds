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
package org.jclouds.reflect;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.reflect.Invokable;

/**
 * Context needed to call {@link com.google.common.reflect.Invokable#invoke(Object, Object...)}
 * 
 * @author Adrian Cole
 */
@Beta
public final class Invocation {

   /**
    * @param args
    *           as these represent parameters, can contain nulls
    */
   public static Invocation create(Invokable<?, ?> invokable, List<Object> args) {
      return new Invocation(invokable, args);
   }

   private final Invokable<?, ?> invokable;
   private final List<Object> args;

   private Invocation(Invokable<?, ?> invokable, List<Object> args) {
      this.invokable = checkNotNull(invokable, "invokable");
      this.args = checkNotNull(args, "args");
   }

   /**
    * what we can invoke
    */
   public Invokable<?, ?> getInvokable() {
      return invokable;
   }

   /**
    * arguments applied to {@link #getInvokable()} during {@link Invokable#invoke(Object, Object...)}
    * 
    * @param args
    *           as these represent parameters, can contain nulls
    */
   public List<Object> getArgs() {
      return args;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Invocation that = Invocation.class.cast(o);
      return equal(this.invokable, that.invokable) && equal(this.args, that.args);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(invokable, args);
   }

   @Override
   public String toString() {
      return String.format("%s%s", invokable, args);
   }

   /**
    * result of an invocation which is either successful or failed, but not both.
    */
   @Beta
   public final static class Result {
      public static Result success(@Nullable Object result) {
         return new Result(Optional.fromNullable(result), Optional.<Throwable> absent());
      }

      public static Result fail(Throwable throwable) {
         return new Result(Optional.absent(), Optional.of(throwable));
      }

      private final Optional<Object> result;
      private final Optional<Throwable> throwable;

      private Result(Optional<Object> result, Optional<Throwable> throwable) {
         this.result = checkNotNull(result, "result");
         this.throwable = checkNotNull(throwable, "throwable");
      }

      /**
       * result of{@link Invokable#invoke(Object, Object...)}
       */
      public Optional<Object> getResult() {
         return result;
      }

      /**
       * throwable received during {@link Invokable#invoke(Object, Object...)}
       */
      public Optional<Throwable> getThrowable() {
         return throwable;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o)
            return true;
         if (o == null || getClass() != o.getClass())
            return false;
         Result that = Result.class.cast(o);
         return equal(this.result.orNull(), that.result.orNull())
               && equal(this.throwable.orNull(), that.throwable.orNull());
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(result.orNull(), throwable.orNull());
      }

      @Override
      public String toString() {
         return Objects.toStringHelper("").omitNullValues().add("result", result.orNull())
               .add("throwable", throwable.orNull()).toString();
      }
   }

}
