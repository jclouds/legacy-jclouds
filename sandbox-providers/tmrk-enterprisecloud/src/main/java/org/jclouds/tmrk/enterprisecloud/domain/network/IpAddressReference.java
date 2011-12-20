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
package org.jclouds.tmrk.enterprisecloud.domain.network;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseNamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;

import javax.xml.bind.annotation.XmlElement;
import java.net.URI;
import java.util.Map;

/**
 * <xs:complexType name="IpAddressReferenceType">
 * @author Jason King
 * 
 */
public class IpAddressReference extends BaseNamedResource<IpAddressReference> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromNetworkReference(this);
   }

   public static class Builder extends BaseNamedResource.Builder<IpAddressReference> {

      private NamedResource network;
      private NamedResource host;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.IpAddressReference#getNetwork
       */
      public Builder network(NamedResource network) {
         this.network = network;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.IpAddressReference#getHost
       */
      public Builder host(NamedResource host) {
         this.host = host;
         return this;
      }

      @Override
      public IpAddressReference build() {
         return new IpAddressReference(href, type, name, network, host);
      }

      public Builder fromNetworkReference(IpAddressReference in) {
         return fromNamedResource(in).network(in.getNetwork()).host(in.getHost());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<IpAddressReference> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromNamedResource(BaseNamedResource<IpAddressReference> in) {
         return Builder.class.cast(super.fromNamedResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

       /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

       /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }
   }

   @XmlElement(name = "Network", required = false)
   private NamedResource network;

   @XmlElement(name = "Host", required = false)
   private NamedResource host;

   private IpAddressReference(URI href, String type, String name,
                              @Nullable NamedResource network, @Nullable NamedResource host) {
      super(href, type, name);
      this.network = network;
      this.host = host;
   }

   private IpAddressReference() {
       //For JAXB
   }

   public NamedResource getNetwork() {
      return network;
   }

   public NamedResource getHost() {
      return host;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      IpAddressReference that = (IpAddressReference) o;

      if (host != null ? !host.equals(that.host) : that.host != null)
         return false;
      if (network != null ? !network.equals(that.network) : that.network != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (network != null ? network.hashCode() : 0);
      result = 31 * result + (host != null ? host.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", network="+network+", host="+host;
   }
}