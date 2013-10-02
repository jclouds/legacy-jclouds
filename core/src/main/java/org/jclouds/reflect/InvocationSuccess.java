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
package org.jclouds.reflect;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * Holds the context of a successful call to {@link com.google.common.reflect.Invokable#invoke(Object, Object...)}
 * 
 * @author Adrian Cole
 */
@Beta
public final class InvocationSuccess {
   public static InvocationSuccess create(Invocation invocation, @Nullable Object result) {
      return new InvocationSuccess(invocation, Optional.fromNullable(result));
   }

   private final Invocation invocation;
   private final Optional<Object> result;

   private InvocationSuccess(Invocation invocation, Optional<Object> result) {
      this.invocation = checkNotNull(invocation, "invocation");
      this.result = checkNotNull(result, "result");
   }

   /**
    * what was invocation
    */
   public Invocation getInvocation() {
      return invocation;
   }

   public Optional<Object> getResult() {
      return result;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InvocationSuccess that = InvocationSuccess.class.cast(o);
      return equal(this.invocation, that.invocation) && equal(this.result.orNull(), that.result.orNull());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(invocation, result.orNull());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").omitNullValues().add("invocation", invocation).add("result", result.orNull());
   }
}
