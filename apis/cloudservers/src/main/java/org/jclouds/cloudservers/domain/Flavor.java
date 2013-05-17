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
package org.jclouds.cloudservers.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A flavor is an available hardware configuration for a server. Each flavor has a unique
 * combination of disk space and memory capacity.
 * 
 * @author Adrian Cole
*/
public class Flavor {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromFlavor(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String name;
      protected Integer disk;
      protected Integer ram;
   
      /** 
       * @see Flavor#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /** 
       * @see Flavor#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see Flavor#getDisk()
       */
      public T disk(Integer disk) {
         this.disk = disk;
         return self();
      }

      /** 
       * @see Flavor#getRam()
       */
      public T ram(Integer ram) {
         this.ram = ram;
         return self();
      }

      public Flavor build() {
         return new Flavor(id, name, disk, ram);
      }
      
      public T fromFlavor(Flavor in) {
         return this
                  .id(in.getId())
                  .name(in.getName())
                  .disk(in.getDisk())
                  .ram(in.getRam());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String name;
   private final Integer disk;
   private final Integer ram;

   @ConstructorProperties({
      "id", "name", "disk", "ram"
   })
   protected Flavor(int id, String name, @Nullable Integer disk, @Nullable Integer ram) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.disk = disk;
      this.ram = ram;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public Integer getDisk() {
      return this.disk;
   }

   @Nullable
   public Integer getRam() {
      return this.ram;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, disk, ram);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Flavor that = Flavor.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.disk, that.disk)
               && Objects.equal(this.ram, that.ram);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("disk", disk).add("ram", ram);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
