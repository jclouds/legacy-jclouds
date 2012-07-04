/*
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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A Floating IP is an IP address that can be created and associated with a
 * Server instance. Floating IPs can also be disassociated and deleted from a
 * Server instance.
 * 
 * @author Jeremy Daggett
 * @author chamerling
*/
public class FloatingIP implements Comparable<FloatingIP> {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromFloatingIP(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String id;
      protected String ip;
      protected String fixedIp;
      protected String instanceId;
   
      /** 
       * @see FloatingIP#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /** 
       * @see FloatingIP#getIp()
       */
      public T ip(String ip) {
         this.ip = ip;
         return self();
      }

      /** 
       * @see FloatingIP#getFixedIp()
       */
      public T fixedIp(String fixedIp) {
         this.fixedIp = fixedIp;
         return self();
      }

      /** 
       * @see FloatingIP#getInstanceId()
       */
      public T instanceId(String instanceId) {
         this.instanceId = instanceId;
         return self();
      }

      public FloatingIP build() {
         return new FloatingIP(id, ip, fixedIp, instanceId);
      }
      
      public T fromFloatingIP(FloatingIP in) {
         return this
                  .id(in.getId())
                  .ip(in.getIp())
                  .fixedIp(in.getFixedIp())
                  .instanceId(in.getInstanceId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String ip;
   @Named("fixed_ip")
   private final String fixedIp;
   @Named("instance_id")
   private final String instanceId;

   @ConstructorProperties({
      "id", "ip", "fixed_ip", "instance_id"
   })
   protected FloatingIP(String id, String ip, @Nullable String fixedIp, @Nullable String instanceId) {
      this.id = checkNotNull(id, "id");
      this.ip = checkNotNull(ip, "ip");
      this.fixedIp = fixedIp;
      this.instanceId = instanceId;
   }

   public String getId() {
      return this.id;
   }

   public String getIp() {
      return this.ip;
   }

   @Nullable
   public String getFixedIp() {
      return this.fixedIp;
   }

   @Nullable
   public String getInstanceId() {
      return this.instanceId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, ip, fixedIp, instanceId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      FloatingIP that = FloatingIP.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.ip, that.ip)
               && Objects.equal(this.fixedIp, that.fixedIp)
               && Objects.equal(this.instanceId, that.instanceId);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("ip", ip).add("fixedIp", fixedIp).add("instanceId", instanceId);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(FloatingIP o) {
      return this.id.compareTo(o.getId());
   }
}
