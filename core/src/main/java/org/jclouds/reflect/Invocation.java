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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
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

   /**
    * invocation without arguments.
    * 
    * @throws IllegalArgumentException
    *            if in invokable requires arguments
    */
   public static Invocation create(Invokable<?, ?> invokable) {
      checkArgument(
            invokable.getParameters().size() == 0 || (invokable.getParameters().size() == 1 && invokable.isVarArgs()),
            "please specify arguments to %s", invokable);
      return create(invokable, ImmutableList.of());
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
}
