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
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The load balancer source IP addresses are useful for customers who are automating the deployment of infrastructure 
 * and need to determine the IP addresses of requests coming from our load balancers for the purpose of creating more 
 * robust firewall rules.
 * 
 * @author Everett Toews
 */
public class SourceAddresses {

   private final String ipv6Public;
   private final String ipv4Public;
   private final String ipv4Servicenet;

   protected SourceAddresses(String ipv6Public, String ipv4Public, String ipv4Servicenet) {
      this.ipv6Public = ipv6Public;
      this.ipv4Public = ipv4Public;
      this.ipv4Servicenet = ipv4Servicenet;
   }

   public String getIPV6Public() {
      return this.ipv6Public;
   }

   public String getIPV4Public() {
      return this.ipv4Public;
   }

   public String getIPV4Servicenet() {
      return this.ipv4Servicenet;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ipv6Public, ipv4Public, ipv4Servicenet);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      SourceAddresses that = SourceAddresses.class.cast(obj);
      return Objects.equal(this.ipv6Public, that.ipv6Public) && Objects.equal(this.ipv4Public, that.ipv4Public)
            && Objects.equal(this.ipv4Servicenet, that.ipv4Servicenet);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).add("ipv6Public", ipv6Public).add("ipv4Public", ipv4Public)
            .add("ipv4Servicenet", ipv4Servicenet);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static class Builder {
      private String ipv6Public;
      private String ipv4Public;
      private String ipv4Servicenet;

      /** 
       * @see SourceAddresses#getIPV6Public()
       */
      public Builder ipv6Public(String ipv6Public) {
         this.ipv6Public = ipv6Public;
         return this;
      }

      /** 
       * @see SourceAddresses#getIPV4Public()
       */
      public Builder ipv4Public(String ipv4Public) {
         this.ipv4Public = ipv4Public;
         return this;
      }

      /** 
       * @see SourceAddresses#getIPV4Servicenet()
       */
      public Builder ipv4Servicenet(String ipv4Servicenet) {
         this.ipv4Servicenet = ipv4Servicenet;
         return this;
      }

      public SourceAddresses build() {
         return new SourceAddresses(ipv6Public, ipv4Public, ipv4Servicenet);
      }

      public Builder from(SourceAddresses in) {
         return this.ipv6Public(in.getIPV6Public()).ipv4Public(in.getIPV4Public())
               .ipv4Servicenet(in.getIPV4Servicenet());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().from(this);
   }
}
