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

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromIngress(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected IpProtocol ipProtocol;
      protected int fromPort;
      protected int toPort;
   
      /** 
       * @see Ingress#getIpProtocol()
       */
      public T ipProtocol(IpProtocol ipProtocol) {
         this.ipProtocol = ipProtocol;
         return self();
      }

      /** 
       * @see Ingress#getFromPort()
       */
      public T fromPort(int fromPort) {
         this.fromPort = fromPort;
         return self();
      }

      /** 
       * @see Ingress#getToPort()
       */
      public T toPort(int toPort) {
         this.toPort = toPort;
         return self();
      }

      public Ingress build() {
         return new Ingress(ipProtocol, fromPort, toPort);
      }
      
      public T fromIngress(Ingress in) {
         return this
                  .ipProtocol(in.getIpProtocol())
                  .fromPort(in.getFromPort())
                  .toPort(in.getToPort());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   @Named("ip_protocol")
   private final IpProtocol ipProtocol;
   @Named("from_port")
   private final int fromPort;
   @Named("to_port")
   private final int toPort;

   @ConstructorProperties({
      "ip_protocol", "from_port", "to_port"
   })
   protected Ingress(IpProtocol ipProtocol, int fromPort, int toPort) {
      this.ipProtocol = checkNotNull(ipProtocol, "ipProtocol");
      this.fromPort = fromPort;
      this.toPort = toPort;
   }

   /**
    * destination IP protocol
    */
   public IpProtocol getIpProtocol() {
      return this.ipProtocol;
   }

   /**
    * Start of destination port range for the TCP and UDP protocols, or an ICMP type number. An ICMP
    * type number of -1 indicates a wildcard (i.e., any ICMP type number).
    */
   public int getFromPort() {
      return this.fromPort;
   }

   /**
    * End of destination port range for the TCP and UDP protocols, or an ICMP code. An ICMP code of
    * -1 indicates a wildcard (i.e., any ICMP code).
    */
   public int getToPort() {
      return this.toPort;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ipProtocol, fromPort, toPort);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Ingress that = Ingress.class.cast(obj);
      return Objects.equal(this.ipProtocol, that.ipProtocol)
               && Objects.equal(this.fromPort, that.fromPort)
               && Objects.equal(this.toPort, that.toPort);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("ipProtocol", ipProtocol).add("fromPort", fromPort).add("toPort", toPort);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
