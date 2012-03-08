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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents the network config section of a vApp.
 *
 * <pre>
 * &lt;complexType name="NetworkConfigSection" /&gt;
 * </pre>
 */
@XmlRootElement(name = "NetworkConfigSection")
public class NetworkConfigSection extends SectionType<NetworkConfigSection> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromNetworkConfigSection(this);
   }

   public static class Builder extends SectionType.Builder<NetworkConfigSection> {

      private Set<Link> links = Sets.newLinkedHashSet();
      private Set<VAppNetworkConfiguration> networkConfigs = Sets.newLinkedHashSet();
      private URI href;
      private String type;

      /**
       * @see NetworkConfigSection#getLinks()
       */
      public Builder links(Set<Link> links) {
         this.links = checkNotNull(links, "links");
         return this;
      }

      /**
       * @see NetworkConfigSection#getNetworkConfigs()
       */
      public Builder networkConfigs(Set<VAppNetworkConfiguration> networkConfigs) {
         this.networkConfigs = checkNotNull(networkConfigs, "networkConfigs");
         return this;
      }

      /**
       * @see NetworkConfigSection#getHref()
       */
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see NetworkConfigSection#getType()
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }


      @Override
      public NetworkConfigSection build() {
         return new NetworkConfigSection(info, required, links, networkConfigs, href, type);
      }

      public Builder fromNetworkConfigSection(NetworkConfigSection in) {
         return fromSectionType(in)
               .links(in.getLinks())
               .networkConfigs(in.getNetworkConfigs())
               .href(in.getHref())
               .type(in.getType());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSectionType(SectionType<NetworkConfigSection> in) {
         return Builder.class.cast(super.fromSectionType(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder required(Boolean required) {
         return Builder.class.cast(super.required(required));
      }

   }

   @XmlElement(name = "Link")
   protected Set<Link> links = Sets.newLinkedHashSet();
   @XmlElement(name = "NetworkConfig")
   protected Set<VAppNetworkConfiguration> networkConfigs = Sets.newLinkedHashSet();
   @XmlAttribute
   @XmlSchemaType(name = "anyURI")
   protected URI href;
   @XmlAttribute
   protected String type;

   public NetworkConfigSection(@Nullable String info, @Nullable Boolean required, Set<Link> links, Set<VAppNetworkConfiguration> networkConfigs,
                               URI href, String type) {
      super(info, required);
      this.links = ImmutableSet.copyOf(links);
      this.networkConfigs = ImmutableSet.copyOf(networkConfigs);
      this.href = href;
      this.type = type;
   }

   protected NetworkConfigSection() {
      // For JAXB
   }

   /**
    * Gets the value of the link property.
    */
   public Set<Link> getLinks() {
      return Collections.unmodifiableSet(this.links);
   }

   /**
    * Gets the value of the networkConfig property.
    */
   public Set<VAppNetworkConfiguration> getNetworkConfigs() {
      return Collections.unmodifiableSet(this.networkConfigs);
   }

   /**
    * Gets the value of the href property.
    */
   public URI getHref() {
      return href;
   }

   /**
    * Gets the value of the type property.
    */
   public String getType() {
      return type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NetworkConfigSection that = NetworkConfigSection.class.cast(o);
      return super.equals(that) && equal(links, that.links) && equal(networkConfigs, that.networkConfigs) && equal(href, that.href) && equal(type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), links, networkConfigs, href, type);
   }

   @Override
   public Objects.ToStringHelper string() {
      return super.string().add("links", links).add("networkConfigs", networkConfigs).add("href", href).add("type", type);
   }

}
