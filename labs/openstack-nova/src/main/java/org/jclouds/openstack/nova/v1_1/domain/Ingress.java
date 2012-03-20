/**
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
package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Ingress access to a destination protocol on particular ports
 * 
 * @author Adrian Cole
 */
@Beta
public class Ingress {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private IpProtocol ipProtocol;
      private int fromPort;
      private int toPort;

      /**
       * 
       * @see Ingress#getIpProtocol()
       */
      public Builder ipProtocol(IpProtocol ipProtocol) {
         this.ipProtocol = ipProtocol;
         return this;
      }

      /**
       * 
       * @see Ingress#getFromPort()
       */
      public Builder fromPort(int fromPort) {
         this.fromPort = fromPort;
         return this;
      }

      /**
       * 
       * @see Ingress#getToPort()
       */
      public Builder toPort(int toPort) {
         this.toPort = toPort;
         return this;
      }

      public Ingress build() {
         return new Ingress(ipProtocol, fromPort, toPort);
      }
   }

   private final IpProtocol ipProtocol;
   private final int fromPort;
   private final int toPort;

   protected Ingress(IpProtocol ipProtocol, int fromPort, int toPort) {
      this.fromPort = fromPort;
      this.toPort = toPort;
      this.ipProtocol = checkNotNull(ipProtocol, "ipProtocol");
   }

   /**
    * destination IP protocol
    */
   public IpProtocol getIpProtocol() {
      return ipProtocol;
   }

   /**
    * Start of destination port range for the TCP and UDP protocols, or an ICMP type number. An ICMP
    * type number of -1 indicates a wildcard (i.e., any ICMP type number).
    */
   public int getFromPort() {
      return fromPort;
   }

   /**
    * End of destination port range for the TCP and UDP protocols, or an ICMP code. An ICMP code of
    * -1 indicates a wildcard (i.e., any ICMP code).
    */
   public int getToPort() {
      return toPort;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      // allow subtypes
      if (o == null || !(o instanceof Ingress))
         return false;
      Ingress that = Ingress.class.cast(o);
      return equal(this.ipProtocol, that.ipProtocol) && equal(this.fromPort, that.fromPort)
               && equal(this.toPort, that.toPort);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ipProtocol, fromPort, toPort);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("ipProtocol", ipProtocol).add("fromPort", fromPort).add("toPort", toPort);
   }

}
