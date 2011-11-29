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
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseNamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

/**
 * <xs:complexType name="NetworkReference">
 * @author Jason King
 * 
 */
public class NetworkReference extends BaseNamedResource<NetworkReference> {
    @XmlEnum
    public static enum NetworkType {

      @XmlEnumValue("Dmz")
      DMZ,

      @XmlEnumValue("Internal")
      INTERNAL;

      public String value() {
         return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }
   }

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

   public static class Builder extends BaseNamedResource.Builder<NetworkReference> {

      private NetworkType networkType;

      /**
       * @see NetworkReference#getNetworkType
       */
      public Builder networkType(NetworkType networkType) {
         this.networkType = networkType;
         return this;
      }

      @Override
      public NetworkReference build() {
         return new NetworkReference(href, type, name, networkType);
      }

      public Builder fromNetworkReference(NetworkReference in) {
         return fromNamedResource(in).networkType(in.getNetworkType());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<NetworkReference> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromNamedResource(BaseNamedResource<NetworkReference> in) {
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

   @XmlElement(name = "NetworkType")
   private NetworkType networkType;

   private NetworkReference(URI href, String type, String name,@Nullable NetworkType networkType) {
      super(href, type, name);
      this.networkType = networkType;
   }

   private NetworkReference() {
       //For JAXB
   }

   @Nullable
   public NetworkType getNetworkType() {
      return networkType;
   }


   @Override
   public String string() {
      return super.string()+", networkType="+networkType;
   }
}