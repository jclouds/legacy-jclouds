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
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;

/**
 * The allowed arguments for archive manipulation, such as archivesize
 *
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#archive_allowedarguments" />
 */
public class ArchiveAllowedArguments {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromArchiveAllowedArguments(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected List<Integer> archiveSizes = ImmutableList.of();

      /**
       * @see ArchiveAllowedArguments#getSizes()
       */
      public T archiveSizes(List<Integer> archiveSizes) {
         this.archiveSizes = ImmutableList.copyOf(checkNotNull(archiveSizes, "archiveSizes"));
         return self();
      }

      public T archiveSizes(Integer... in) {
         return archiveSizes(ImmutableList.copyOf(in));
      }

      public ArchiveAllowedArguments build() {
         return new ArchiveAllowedArguments(archiveSizes);
      }

      public T fromArchiveAllowedArguments(ArchiveAllowedArguments in) {
         return this.archiveSizes(in.getSizes());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final List<Integer> archiveSizes;

   @ConstructorProperties({
         "archivesize"
   })
   protected ArchiveAllowedArguments(List<Integer> archiveSizes) {
      this.archiveSizes = ImmutableList.copyOf(checkNotNull(archiveSizes, "archiveSizes"));
   }

   /**
    * @return the list of allowed archive sizes, in GB
    */
   public List<Integer> getSizes() {
      return this.archiveSizes;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(archiveSizes);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ArchiveAllowedArguments that = ArchiveAllowedArguments.class.cast(obj);
      return Objects.equal(this.archiveSizes, that.archiveSizes);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("archiveSizes", archiveSizes);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
