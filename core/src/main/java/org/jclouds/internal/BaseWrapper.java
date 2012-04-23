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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.Wrapper;
import org.jclouds.location.Provider;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ForwardingObject;
import com.google.common.reflect.TypeToken;

/**
 * @author Adrian Cole
 */
@Singleton
public abstract class BaseWrapper extends ForwardingObject implements Wrapper {

   private final Context wrapped;
   private final TypeToken<? extends Context> wrappedType;

   protected BaseWrapper(@Provider Context wrapped, @Provider TypeToken<? extends Context> wrappedType) {
      this.wrapped = checkNotNull(wrapped, "wrapped");
      this.wrappedType = checkNotNull(wrappedType, "wrappedType");
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public <C extends Context> C unwrap(TypeToken<C> type) {
      checkArgument(checkNotNull(type, "type").isAssignableFrom(wrappedType), "wrapped type: %s not assignable from %s", wrappedType, type);
      return (C) wrapped;
   }
   
   @Override
   public <C extends Context> C unwrap(Class<C> clazz) {
      return unwrap (TypeToken.of(checkNotNull(clazz, "clazz")));
   }
   
   @Override
   public TypeToken<? extends Context> getWrappedType() {
      return wrappedType;
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public <C extends Context> C unwrap() throws ClassCastException {
      return (C) unwrap(getWrappedType());
   }
   
   @Override
   protected Context delegate() {
      return wrapped;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      BaseWrapper that = BaseWrapper.class.cast(o);
      return equal(this.delegate(), that.delegate()) && equal(this.getWrappedType(), that.getWrappedType());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(delegate(), getWrappedType());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("wrapped", delegate()).add("wrappedType", getWrappedType());
   }


}
