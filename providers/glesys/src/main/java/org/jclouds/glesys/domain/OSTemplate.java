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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Operating system template
 *
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#server_templates" />
 */
public class OSTemplate {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromOSTemplate(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected int minDiskSize;
      protected int minMemSize;
      protected String os;
      protected String platform;

      /**
       * @see OSTemplate#getName()
       */
      public T name(String name) {
         this.name = checkNotNull(name, "name");
         return self();
      }

      /**
       * @see OSTemplate#getMinDiskSize()
       */
      public T minDiskSize(int minDiskSize) {
         this.minDiskSize = minDiskSize;
         return self();
      }

      /**
       * @see OSTemplate#getMinMemSize()
       */
      public T minMemSize(int minMemSize) {
         this.minMemSize = minMemSize;
         return self();
      }

      /**
       * @see OSTemplate#getOs()
       */
      public T os(String os) {
         this.os = checkNotNull(os, "os");
         return self();
      }

      /**
       * @see OSTemplate#getPlatform()
       */
      public T platform(String platform) {
         this.platform = checkNotNull(platform, "platform");
         return self();
      }

      public OSTemplate build() {
         return new OSTemplate(name, minDiskSize, minMemSize, os, platform);
      }

      public T fromOSTemplate(OSTemplate in) {
         return this.name(in.getName())
               .minDiskSize(in.getMinDiskSize())
               .minMemSize(in.getMinMemSize())
               .os(in.getOs())
               .platform(in.getPlatform());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String name;
   private final int minDiskSize;
   private final int minMemSize;
   private final String os;
   private final String platform;

   @ConstructorProperties({
         "name", "minimumdisksize", "minimummemorysize", "operatingsystem", "platform"
   })
   protected OSTemplate(String name, int minDiskSize, int minMemSize, String os, String platform) {
      this.name = checkNotNull(name, "name");
      this.minDiskSize = minDiskSize;
      this.minMemSize = minMemSize;
      this.os = checkNotNull(os, "os");
      this.platform = checkNotNull(platform, "platform");
   }

   /**
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the minimum allowed disk size in GB
    * @see org.jclouds.glesys.domain.AllowedArgumentsForCreateServer#getDiskSizesInGB()
    */
   public int getMinDiskSize() {
      return this.minDiskSize;
   }

   /**
    * @return the minimum allowed memory size in MB
    * @see org.jclouds.glesys.domain.AllowedArgumentsForCreateServer#getMemorySizesInMB()
    */
   public int getMinMemSize() {
      return this.minMemSize;
   }

   /**
    * @return the name of the operating system type ex. "linux"
    */
   public String getOs() {
      return this.os;
   }

   /**
    * @return the name of the platform this template is available in, ex. "Xen"
    */
   public String getPlatform() {
      return this.platform;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, platform);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      OSTemplate that = OSTemplate.class.cast(obj);
      return Objects.equal(this.name, that.name)
            && Objects.equal(this.platform, that.platform);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("name", name).add("minDiskSize", minDiskSize).add("minMemSize", minMemSize).add("os", os).add("platform", platform);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
