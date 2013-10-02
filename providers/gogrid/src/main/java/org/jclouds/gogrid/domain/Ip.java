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
package org.jclouds.gogrid.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.primitives.Longs;

/**
 * Class Ip
 * 
 * @author Oleksiy Yarmula
*/
public class Ip implements Comparable<Ip> {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromIp(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected long id;
      protected String ip;
      protected String subnet;
      protected boolean isPublic;
      protected IpState state;
      protected Option datacenter;
   
      /** 
       * @see Ip#getId()
       */
      public T id(long id) {
         this.id = id;
         return self();
      }

      /** 
       * @see Ip#getIp()
       */
      public T ip(String ip) {
         this.ip = ip;
         return self();
      }

      /** 
       * @see Ip#getSubnet()
       */
      public T subnet(String subnet) {
         this.subnet = subnet;
         return self();
      }

      /** 
       * @see Ip#isPublic()
       */
      public T isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return self();
      }

      /** 
       * @see Ip#getState()
       */
      public T state(IpState state) {
         this.state = state;
         return self();
      }

      /** 
       * @see Ip#getDatacenter()
       */
      public T datacenter(Option datacenter) {
         this.datacenter = datacenter;
         return self();
      }

      public Ip build() {
         return new Ip(id, ip, subnet, isPublic, state, datacenter);
      }
      
      public T fromIp(Ip in) {
         return this
                  .id(in.getId())
                  .ip(in.getIp())
                  .subnet(in.getSubnet())
                  .isPublic(in.isPublic())
                  .state(in.getState())
                  .datacenter(in.getDatacenter());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final long id;
   private final String ip;
   private final String subnet;
   private final boolean isPublic;
   private final IpState state;
   private final Option datacenter;

   @ConstructorProperties({
      "id", "ip", "subnet", "public", "state", "datacenter"
   })
   protected Ip(long id, String ip, @Nullable String subnet, boolean isPublic, @Nullable IpState state, @Nullable Option datacenter) {
      this.id = id;
      this.ip = checkNotNull(ip, "ip");
      this.subnet = subnet;
      this.isPublic = isPublic;
      this.state = state == null ? IpState.UNRECOGNIZED : state;
      this.datacenter = datacenter;
   }

   public long getId() {
      return this.id;
   }

   public String getIp() {
      return this.ip;
   }

   @Nullable
   public String getSubnet() {
      return this.subnet;
   }

   public boolean isPublic() {
      return this.isPublic;
   }

   public IpState getState() {
      return this.state;
   }

   @Nullable
   public Option getDatacenter() {
      return this.datacenter;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, ip, subnet, isPublic, state, datacenter);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Ip that = Ip.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.ip, that.ip)
               && Objects.equal(this.subnet, that.subnet)
               && Objects.equal(this.isPublic, that.isPublic)
               && Objects.equal(this.state, that.state)
               && Objects.equal(this.datacenter, that.datacenter);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("ip", ip).add("subnet", subnet).add("isPublic", isPublic).add("state", state).add("datacenter", datacenter);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Ip o) {
      return Longs.compare(id, o.getId());
   }

}
