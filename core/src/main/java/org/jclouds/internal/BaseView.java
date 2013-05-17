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
package org.jclouds.internal;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.View;
import org.jclouds.location.Provider;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ForwardingObject;
import com.google.common.reflect.TypeToken;

/**
 * @author Adrian Cole
 */
@Singleton
public abstract class BaseView extends ForwardingObject implements View {

   private final Context backend;
   private final TypeToken<? extends Context> backendType;

   protected BaseView(@Provider Context backend, @Provider TypeToken<? extends Context> backendType) {
      this.backend = checkNotNull(backend, "backend");
      this.backendType = checkNotNull(backendType, "backendType");
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public <C extends Context> C unwrap(TypeToken<C> type) {
      checkArgument(checkNotNull(type, "type").isAssignableFrom(backendType), "backend type: %s not assignable from %s", backendType, type);
      return (C) backend;
   }
   
   @Override
   public TypeToken<? extends Context> getBackendType() {
      return backendType;
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public <C extends Context> C unwrap() throws ClassCastException {
      return (C) unwrap(getBackendType());
   }
   
   @Override
   protected Context delegate() {
      return backend;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      BaseView that = BaseView.class.cast(o);
      return equal(this.delegate(), that.delegate()) && equal(this.getBackendType(), that.getBackendType());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(delegate(), getBackendType());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("backend", delegate()).add("backendType", getBackendType());
   }

}
