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
package org.jclouds.blobstore.options;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;

/**
 * Contains options supported by BlobStores.listAll.
 *  
 * @see ListOptions for recommended usage patterns
 * 
 * @author Aled Sage
 * @since 1.3
 */
@Beta
public class ListAllOptions implements Cloneable {

   public static final ImmutableListAllOptions NONE = new ImmutableListAllOptions(new ListAllOptions());

   private boolean eager = false;

   public ListAllOptions() {
   }

   ListAllOptions(boolean eagerness) {
      this.eager = eagerness;
   }

   public static class ImmutableListAllOptions extends ListAllOptions {
      private final ListAllOptions delegate;

      public ImmutableListAllOptions(ListAllOptions delegate) {
         this.delegate = delegate;
      }

      @Override
      public boolean isEager() {
         return delegate.isEager();
      }
      
      @Override
      public ListAllOptions eager(boolean val) {
         throw new UnsupportedOperationException();
      }
   }

   public boolean isEager() {
      return eager;
   }

   /**
    * If eager, will connect to container immediately and fail-fast, rather than failing when 
    * first iterating over the list.
    */
   public ListAllOptions eager(boolean val) {
      this.eager = val;
      return this;
   }

   public static class Builder {
      /**
       * @see ListAllOptions#eager(boolean)
       */
      public static ListAllOptions eager(boolean eager) {
         ListAllOptions options = new ListAllOptions();
         return options.eager(eager);
      }
   }

   @Override
   public ListAllOptions clone() {
      return new ListAllOptions(isEager());
   }

   @Override
   public String toString() {
      return "[eager=" + eager + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(eager);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ListAllOptions other = (ListAllOptions) obj;
      return eager == other.eager;
   }
}
