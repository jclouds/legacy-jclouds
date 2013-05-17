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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * Class IpPortPair
 * 
 * @author Oleksiy Yarmula
*/
public class IpPortPair implements Comparable<IpPortPair> {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromIpPortPair(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected Ip ip;
      protected int port;
   
      /** 
       * @see IpPortPair#getIp()
       */
      public T ip(Ip ip) {
         this.ip = ip;
         return self();
      }

      /** 
       * @see IpPortPair#getPort()
       */
      public T port(int port) {
         this.port = port;
         return self();
      }

      public IpPortPair build() {
         return new IpPortPair(ip, port);
      }
      
      public T fromIpPortPair(IpPortPair in) {
         return this
                  .ip(in.getIp())
                  .port(in.getPort());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Ip ip;
   private final int port;

   @ConstructorProperties({
      "ip", "port"
   })
   protected IpPortPair(Ip ip, int port) {
      this.ip = checkNotNull(ip, "ip");
      this.port = port;
   }

   public Ip getIp() {
      return this.ip;
   }

   public int getPort() {
      return this.port;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ip, port);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      IpPortPair that = IpPortPair.class.cast(obj);
      return Objects.equal(this.ip, that.ip)
               && Objects.equal(this.port, that.port);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("ip", ip).add("port", port);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(IpPortPair o) {
      if(ip != null && o.getIp() != null) return Longs.compare(ip.getId(), o.getIp().getId());
      return Ints.compare(port, o.getPort());
   }
}
