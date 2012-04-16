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

import java.io.Closeable;

import javax.inject.Singleton;

import org.jclouds.Wrapper;
import org.jclouds.location.Provider;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.reflect.TypeToken;

/**
 * @author Adrian Cole
 */
@Singleton
public abstract class BaseWrapper implements Wrapper {

   private final Closeable wrapped;
   private final TypeToken<? extends Closeable> wrappedType;


   protected BaseWrapper(@Provider Closeable wrapped, @Provider TypeToken<? extends Closeable> wrappedType) {
      this.wrapped = checkNotNull(wrapped, "wrapped");
      this.wrappedType = checkNotNull(wrappedType, "wrappedType");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <C extends Closeable> C unwrap(TypeToken<C> type) {
      checkArgument(checkNotNull(type, "type").isAssignableFrom(wrappedType), "wrapped type: %s not assignable from %s", wrappedType, type);
      return (C) wrapped;
   }

   @Override
   public TypeToken<? extends Closeable> getWrappedType() {
      return wrappedType;
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public <C extends Closeable> C unwrap() throws ClassCastException {
      return (C) unwrap(getWrappedType());
   }
   
   public Closeable getWrapped() {
      return wrapped;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      BaseWrapper that = BaseWrapper.class.cast(o);
      return equal(this.getWrapped(), that.getWrapped()) && equal(this.getWrappedType(), that.getWrappedType());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getWrapped(), getWrappedType());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("wrapped", getWrapped()).add("wrappedType", getWrappedType());
   }


}
