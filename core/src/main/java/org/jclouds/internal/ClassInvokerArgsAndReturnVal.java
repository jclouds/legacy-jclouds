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

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.reflect.Invokable;

/**
 * 
 * @author Adrian Cole
 */
public final class ClassInvokerArgsAndReturnVal extends ClassInvokerArgs {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromClassInvokerArgsAndReturnVal(this);
   }

   public final static class Builder extends ClassInvokerArgs.Builder<Builder> {

      private Object returnVal;

      /**
       * @see ClassInvokerArgsAndReturnVal#getReturnVal()
       */
      public Builder returnVal(Object returnVal) {
         this.returnVal = returnVal;
         return this;
      }

      @Override
      public ClassInvokerArgsAndReturnVal build() {
         return new ClassInvokerArgsAndReturnVal(this);
      }

      public Builder fromClassInvokerArgsAndReturnVal(ClassInvokerArgsAndReturnVal in) {
         return fromClassInvokerArgs(in).returnVal(in.getReturnVal());
      }
   }

   private final Object returnVal;

   private ClassInvokerArgsAndReturnVal(Class<?> clazz, Invokable<?, ?> invoker, List<Object> args, Object returnVal) {
      super(clazz, invoker, args);
      this.returnVal = returnVal;
   }

   private ClassInvokerArgsAndReturnVal(Builder builder) {
      super(builder);
      this.returnVal = builder.returnVal;
   }

   public Object getReturnVal() {
      return returnVal;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ClassInvokerArgsAndReturnVal that = ClassInvokerArgsAndReturnVal.class.cast(o);
      return super.equals(that) && equal(this.returnVal, that.returnVal);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), returnVal);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("returnVal", returnVal);
   }

}
