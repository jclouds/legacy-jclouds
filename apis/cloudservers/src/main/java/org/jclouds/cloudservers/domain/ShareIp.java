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

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class ShareIp
*/
public class ShareIp {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromShareIp(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected boolean configureServer;
      protected int sharedIpGroupId;
   
      /** 
       * @see ShareIp#isConfigureServer()
       */
      public T configureServer(boolean configureServer) {
         this.configureServer = configureServer;
         return self();
      }

      /** 
       * @see ShareIp#getSharedIpGroupId()
       */
      public T sharedIpGroupId(int sharedIpGroupId) {
         this.sharedIpGroupId = sharedIpGroupId;
         return self();
      }

      public ShareIp build() {
         return new ShareIp(configureServer, sharedIpGroupId);
      }
      
      public T fromShareIp(ShareIp in) {
         return this
                  .configureServer(in.isConfigureServer())
                  .sharedIpGroupId(in.getSharedIpGroupId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final boolean configureServer;
   private final int sharedIpGroupId;

   @ConstructorProperties({
      "configureServer", "sharedIpGroupId"
   })
   protected ShareIp(boolean configureServer, int sharedIpGroupId) {
      this.configureServer = configureServer;
      this.sharedIpGroupId = sharedIpGroupId;
   }

   public boolean isConfigureServer() {
      return this.configureServer;
   }

   public int getSharedIpGroupId() {
      return this.sharedIpGroupId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(configureServer, sharedIpGroupId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ShareIp that = ShareIp.class.cast(obj);
      return Objects.equal(this.configureServer, that.configureServer)
               && Objects.equal(this.sharedIpGroupId, that.sharedIpGroupId);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("configureServer", configureServer).add("sharedIpGroupId", sharedIpGroupId);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
