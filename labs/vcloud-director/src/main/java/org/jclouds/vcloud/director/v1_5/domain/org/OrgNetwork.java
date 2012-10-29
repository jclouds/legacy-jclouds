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
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.domain.org;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.network.IpAddresses;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

@XmlRootElement(name = "OrgNetwork")
public class OrgNetwork extends Network {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromOrgNetwork(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Network.Builder<T> {
      private Reference networkPool;
      private IpAddresses allowedExternalIpAddresses;

      /**
       * @see OrgNetwork#getNetworkPool()
       */
      public T networkPool(Reference networkPool) {
         this.networkPool = networkPool;
         return self();
      }

      /**
       * @see OrgNetwork#getAllowedExternalIpAddresses()
       */
      public T allowedExternalIpAddresses(IpAddresses allowedExternalIpAddresses) {
         this.allowedExternalIpAddresses = allowedExternalIpAddresses;
         return self();
      }

      @Override
      public OrgNetwork build() {
         return new OrgNetwork(this);
      }
      
      public T fromOrgNetwork(OrgNetwork in) {
         return fromEntityType(in).configuration(in.getConfiguration())
               .networkPool(in.getNetworkPool())
               .allowedExternalIpAddresses(in.getAllowedExternalIpAddresses());
      }
   }
   
   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   protected OrgNetwork() {
      // For JAXB
   }

   private OrgNetwork(Builder<?> b) {
      super(b);
      networkPool = b.networkPool;
      allowedExternalIpAddresses = b.allowedExternalIpAddresses;
   }

   @XmlElement(name = "NetworkPool")
   private Reference networkPool;
   @XmlElement(name = "AllowedExternalIpAddresses")
   private IpAddresses allowedExternalIpAddresses;

   /**
    * @return optional network pool
    */
   public Reference getNetworkPool() {
      return networkPool;
   }

   /**
    * @return optional network pool
    */
   public IpAddresses getAllowedExternalIpAddresses() {
      return allowedExternalIpAddresses;
   }

   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      OrgNetwork that = OrgNetwork.class.cast(o);
      return super.equals(that) && 
            equal(networkPool, that.networkPool) &&
            equal(allowedExternalIpAddresses, that.allowedExternalIpAddresses);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), 
            networkPool, 
            allowedExternalIpAddresses);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("networkPool", networkPool)
            .add("allowedExternalIpAddresses", allowedExternalIpAddresses);
   }
}
