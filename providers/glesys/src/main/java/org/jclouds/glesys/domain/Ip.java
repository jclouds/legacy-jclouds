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
 * Represents an ip address used by a server.
 *
 * @author Adam Lowe
 * @see Server
 * @see ServerDetails
 */
public class Ip {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromIp(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String ip;
      protected int version;
      protected double cost;
      protected String currency;

      /**
       * @see Ip#getIp()
       */
      public T ip(String ip) {
         this.ip = checkNotNull(ip, "ip");
         return self();
      }

      /**
       * @see Ip#getVersion()
       */
      protected T version(int version) {
         this.version = version;
         return self();
      }

      /**
       * @see Ip#getVersion()
       */
      public T version4() {
         return version(4);
      }

      /**
       * @see Ip#getVersion()
       */
      public T version6() {
         return version(6);
      }

      /**
       * @see Ip#getCost()
       */
      public T cost(double cost) {
         this.cost = cost;
         return self();
      }

      /**
       * @see Ip#getCurrency()
       */
      public T currency(String currency) {
         this.currency = currency;
         return self();
      }

      public Ip build() {
         return new Ip(ip, version, cost, currency);
      }

      public T fromIp(Ip in) {
         return this.ip(in.getIp()).version(in.getVersion()).cost(in.getCost());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String ip;
   private final int version;
   private final double cost;
   private final String currency;

   @ConstructorProperties({
         "ipaddress", "version", "cost", "currency"
   })
   protected Ip(String ip, int version, double cost, String currency) {
      this.ip = checkNotNull(ip, "ip");
      this.version = version;
      this.cost = cost;
      this.currency = checkNotNull(currency, "currency");
   }

   /**
    * @return the IP version, ex. 4
    */
   public String getIp() {
      return this.ip;
   }

   /**
    * @return the ip address of the new server
    */
   public int getVersion() {
      return this.version;
   }

   /**
    * @return the cost of the ip address allocated to the new server
    * @see #getCurrency()
    */
   public double getCost() {
      return this.cost;
   }

   /**
    * @return the currency of the cost
    * @see #getCost() 
    */
   public String getCurrency() {
      return currency;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ip, version, cost);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Ip that = Ip.class.cast(obj);
      return Objects.equal(this.ip, that.ip)
            && Objects.equal(this.version, that.version)
            && Objects.equal(this.cost, that.cost)
            && Objects.equal(this.currency, that.currency);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("ip", ip).add("version", version).add("cost", cost).add("currency", currency);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
