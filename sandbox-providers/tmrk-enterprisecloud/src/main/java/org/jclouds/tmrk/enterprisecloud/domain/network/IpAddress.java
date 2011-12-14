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
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;

import javax.xml.bind.annotation.XmlElement;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * <xs:complexType name="IpAddressType">
 * @author Jason King
 */
public class IpAddress extends Resource<IpAddress> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromIpAddress(this);
   }

   public static class Builder extends Resource.Builder<IpAddress> {
      private NamedResource host;
      private NamedResource detectedOn;
      private NamedResource rnatAddress;
      
      /**
       * @see IpAddress#getHost
       */
      public Builder host(NamedResource host) {
         this.host = host;
         return this;
      }

      /**
       * @see IpAddress#getDetectedOn
       */
      public Builder detectedOn(NamedResource detectedOn) {
         this.detectedOn = detectedOn;
         return this;
      }

      /**
       * @see IpAddress#getRnatAddress
       */
      public Builder rnatAddress(NamedResource rnatAddress) {
         this.rnatAddress = rnatAddress;
         return this;
      }

      @Override
      public IpAddress build() {
         return new IpAddress(href, type, name, links, actions, host, detectedOn, rnatAddress);
      }

      public Builder fromIpAddress(IpAddress in) {
         return fromResource(in).host(in.getHost()).detectedOn(in.getDetectedOn()).rnatAddress(in.getRnatAddress());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<IpAddress> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<IpAddress> in) {
         return Builder.class.cast(super.fromResource(in));
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
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
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
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder actions(Set<Action> actions) {
         return Builder.class.cast(super.actions(actions));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }

   }

   @XmlElement(name = "Host", required = false)
   private NamedResource host;

   @XmlElement(name = "DetectedOn", required = false)
   private NamedResource detectedOn;
   
   @XmlElement(name = "RnatAddress", required = false)
   private NamedResource rnatAddress;
   
   private IpAddress(URI href, String type, String name,  Set<Link> links,Set<Action> actions,
                    @Nullable NamedResource host,@Nullable NamedResource detectedOn,@Nullable NamedResource rnatAddress) {
      super(href, type, name, links, actions);
      this.host = host;
      this.detectedOn = detectedOn;
      this.rnatAddress = rnatAddress;
   }

   private IpAddress() {
       //For JAXB
   }

   public NamedResource getHost() {
      return host;
   }

   public NamedResource getDetectedOn() {
      return detectedOn;
   }

   public NamedResource getRnatAddress() {
      return rnatAddress;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      IpAddress ipAddress = (IpAddress) o;

      if (detectedOn != null ? !detectedOn.equals(ipAddress.detectedOn) : ipAddress.detectedOn != null)
         return false;
      if (host != null ? !host.equals(ipAddress.host) : ipAddress.host != null)
         return false;
      if (rnatAddress != null ? !rnatAddress.equals(ipAddress.rnatAddress) : ipAddress.rnatAddress != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (host != null ? host.hashCode() : 0);
      result = 31 * result + (detectedOn != null ? detectedOn.hashCode() : 0);
      result = 31 * result + (rnatAddress != null ? rnatAddress.hashCode() : 0);
      return result;
   }

   @Override
    public String string() {
       return super.string()+", host="+host+", detectedOn="+detectedOn+", rnatAddress="+rnatAddress;
    }

}