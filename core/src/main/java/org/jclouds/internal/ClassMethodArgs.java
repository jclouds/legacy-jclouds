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
package org.jclouds.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * 
 * @author Adrian Cole
 */
public class ClassMethodArgs {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromClassMethodArgs(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public abstract static class Builder<B extends Builder<B>> {
      private Class<?> clazz;
      private Method method;
      private Object[] args;

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * @see ClassMethodArgs#getClazz()
       */
      public B clazz(Class<?> clazz) {
         this.clazz = clazz;
         return self();
      }

      /**
       * @see ClassMethodArgs#getMethod()
       */
      public B method(Method method) {
         this.method = method;
         return self();
      }

      /**
       * @see ClassMethodArgs#getArgs()
       */
      public B args(Object[] args) {
         this.args = args;
         return self();
      }

      public ClassMethodArgs build() {
         return new ClassMethodArgs(this);
      }

      public B fromClassMethodArgs(ClassMethodArgs in) {
         return clazz(in.getClazz()).method(in.getMethod()).args(in.getArgs());
      }
   }

   private final Class<?> clazz;
   private final Method method;
   private final Object[] args;

   public ClassMethodArgs(Builder<?> builder) {
      this(builder.clazz, builder.method, builder.args);
   }

   public ClassMethodArgs(Class<?> clazz, Method method, @Nullable Object[] args) {
      this.clazz = checkNotNull(clazz, "clazz");
      this.method = checkNotNull(method, "method");
      this.args = args;
   }

   public Class<?> getClazz() {
      return clazz;
   }

   public Method getMethod() {
      return method;
   }

   @Nullable public Object[] getArgs() {
      return args;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ClassMethodArgs that = ClassMethodArgs.class.cast(o);
      return equal(this.clazz, that.clazz) && equal(this.method, that.method) && equal(this.args, that.args);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(clazz, method, args);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").omitNullValues().add("clazz", clazz).add("method", method)
               .add("args", args != null ? Arrays.asList(args) : null);
   }
}
