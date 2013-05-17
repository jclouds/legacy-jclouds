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
package org.jclouds.ovf;

import org.jclouds.javax.annotation.Nullable;

/**
 * Metadata about a virtual machine or grouping of them
 * 
 * @author Adrian Cole
 */
public class Section<T extends Section<T>> {

   public static <T extends Section<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromSection(this);
   }

   public static class Builder<T extends Section<T>> {
      protected String info;

      /**
       * @see Section#getInfo
       */
      public Builder<T> info(String info) {
         this.info = info;
         return this;
      }

      public Section<T> build() {
         return new Section<T>(info);
      }

      public Builder<T> fromSection(Section<T> in) {
         return info(in.getInfo());
      }
   }

   protected final String info;

   public Section(@Nullable String info) {
      this.info = info;
   }

   /**
    * 
    * @return ovf info
    */
   public String getInfo() {
      return info;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((info == null) ? 0 : info.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Section<?> other = (Section<?>) obj;
      if (info == null) {
         if (other.info != null)
            return false;
      } else if (!info.equals(other.info))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[info=" + getInfo() + "]";
   }

}
