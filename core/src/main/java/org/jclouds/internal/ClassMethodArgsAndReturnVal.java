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

import java.lang.reflect.Method;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * 
 * @author Adrian Cole
 */
public final class ClassMethodArgsAndReturnVal extends ClassMethodArgs {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromClassMethodArgsAndReturnVal(this);
   }

   public final static class Builder extends ClassMethodArgs.Builder<Builder> {

      private Object returnVal;

      /**
       * @see ClassMethodArgsAndReturnVal#getReturnVal()
       */
      public Builder returnVal(Object returnVal) {
         this.returnVal = returnVal;
         return this;
      }

      @Override
      public ClassMethodArgsAndReturnVal build() {
         return new ClassMethodArgsAndReturnVal(this);
      }

      public Builder fromClassMethodArgsAndReturnVal(ClassMethodArgsAndReturnVal in) {
         return fromClassMethodArgs(in).returnVal(in.getReturnVal());
      }
   }

   private final Object returnVal;

   private ClassMethodArgsAndReturnVal(Class<?> clazz, Method method, Object[] args, Object returnVal) {
      super(clazz, method, args);
      this.returnVal = returnVal;
   }

   private ClassMethodArgsAndReturnVal(Builder builder) {
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
      ClassMethodArgsAndReturnVal that = ClassMethodArgsAndReturnVal.class.cast(o);
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
