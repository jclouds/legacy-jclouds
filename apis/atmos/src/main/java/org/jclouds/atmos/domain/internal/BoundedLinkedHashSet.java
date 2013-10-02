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
package org.jclouds.atmos.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashSet;

import org.jclouds.atmos.domain.BoundedSet;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class BoundedLinkedHashSet<T> extends LinkedHashSet<T> implements BoundedSet<T> {

   protected final String token;

   public BoundedLinkedHashSet(Iterable<T> contents, @Nullable String token) {
      Iterables.addAll(this, checkNotNull(contents, "contents"));
      this.token = token;
   }

   @Nullable
   public String getToken() {
      return token;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((token == null) ? 0 : token.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      BoundedLinkedHashSet<?> other = (BoundedLinkedHashSet<?>) obj;
      if (token == null) {
         if (other.token != null)
            return false;
      } else if (!token.equals(other.token))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[token=" + token + ", contents=" + super.toString() + "]";
   }

}
