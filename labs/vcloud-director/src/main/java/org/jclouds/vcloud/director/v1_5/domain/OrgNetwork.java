/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

@XmlRootElement(name = "OrgNetwork")
public class OrgNetwork extends NetworkType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromOrgNetwork(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends NetworkType.Builder<B> {

      private ReferenceType networkPool;
      private IpAddresses allowedExternalIpAddresses;

      /**
       * @see OrgNetwork#getNetworkPool()
       */
      public B networkPool(ReferenceType networkPool) {
         this.networkPool = networkPool;
         return self();
      }

      /**
       * @see OrgNetwork#getAllowedExternalIpAddresses()
       */
      public B allowedExternalIpAddresses(IpAddresses allowedExternalIpAddresses) {
         this.allowedExternalIpAddresses = allowedExternalIpAddresses;
         return self();
      }

      @Override
      public OrgNetwork build() {
         return new OrgNetwork(this);
      }

      public B fromOrgNetwork(OrgNetwork in) {
         return fromNetworkType(in).configuration(in.getConfiguration())
               .networkPool(in.getNetworkPool())
               .allowedExternalIpAddresses(in.getAllowedExternalIpAddresses());
      }
   }

   protected OrgNetwork() {
      // For JAXB
   }

   protected OrgNetwork(Builder<?> builder) {
      super(builder);
      this.networkPool = builder.networkPool;
      this.allowedExternalIpAddresses = builder.allowedExternalIpAddresses;
   }

   @XmlElement(name = "NetworkPool")
   private ReferenceType networkPool;
   @XmlElement(name = "AllowedExternalIpAddresses")
   private IpAddresses allowedExternalIpAddresses;

   /**
    * @return optional network pool
    */
   public ReferenceType getNetworkPool() {
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
      return super.equals(that) && equal(networkPool, that.networkPool) &&
            equal(allowedExternalIpAddresses, that.allowedExternalIpAddresses);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), networkPool, allowedExternalIpAddresses);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("networkPool", networkPool)
            .add("allowedExternalIpAddresses", allowedExternalIpAddresses);
   }
}
