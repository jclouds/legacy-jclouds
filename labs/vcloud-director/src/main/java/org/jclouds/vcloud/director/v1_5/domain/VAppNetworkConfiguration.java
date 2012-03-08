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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * Represents a VApp network configuration.
 *
 * <pre>
 * &lt;complexType name="VAppNetworkConfiguration" /&gt;
 * </pre>
 */
@XmlType(name = "VAppNetworkConfiguration")
public class VAppNetworkConfiguration extends ResourceType<VAppNetworkConfiguration> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromVAppNetworkConfiguration(this);
   }

   public static class Builder extends ResourceType.Builder<VAppNetworkConfiguration> {

      private String description;
      private NetworkConfiguration configuration;
      private Boolean deployed;
      private String networkName;

      /**
       * @see VAppNetworkConfiguration#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see VAppNetworkConfiguration#getConfiguration()
       */
      public Builder configuration(NetworkConfiguration configuration) {
         this.configuration = configuration;
         return this;
      }

      /**
       * @see VAppNetworkConfiguration#isDeployed()
       */
      public Builder isDeployed(Boolean deployed) {
         this.deployed = deployed;
         return this;
      }

      /**
       * @see VAppNetworkConfiguration#getNetworkName()
       */
      public Builder networkName(String networkName) {
         this.networkName = networkName;
         return this;
      }

      @Override
      public VAppNetworkConfiguration build() {
         return new VAppNetworkConfiguration(href, type, links, description, configuration, deployed, networkName);
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResourceType(ResourceType<VAppNetworkConfiguration> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }

      public Builder fromVAppNetworkConfiguration(VAppNetworkConfiguration in) {
         return fromResourceType(in)
               .description(in.getDescription())
               .configuration(in.getConfiguration())
               .isDeployed(in.isDeployed())
               .networkName(in.getNetworkName());
      }
   }

   @XmlElement(name = "Description")
   protected String description;
   @XmlElement(name = "Configuration", required = true)
   protected NetworkConfiguration configuration;
   @XmlElement(name = "IsDeployed")
   protected Boolean deployed;
   @XmlAttribute(required = true)
   protected String networkName;

   public VAppNetworkConfiguration(URI href, String type, Set<Link> links, String description,
                                   NetworkConfiguration configuration, Boolean deployed, String networkName) {
      super(href, type, links);
      this.description = description;
      this.configuration = configuration;
      this.deployed = deployed;
      this.networkName = networkName;
   }

   protected VAppNetworkConfiguration() {
      // For JAXB
   }

   /**
    * Gets the value of the description property.
    */
   public String getDescription() {
      return description;
   }

   /**
    * Gets the value of the configuration property.
    */
   public NetworkConfiguration getConfiguration() {
      return configuration;
   }

   /**
    * Gets the value of the deployed property.
    */
   public Boolean isDeployed() {
      return deployed;
   }

   /**
    * Gets the value of the networkName property.
    */
   public String getNetworkName() {
      return networkName;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VAppNetworkConfiguration that = VAppNetworkConfiguration.class.cast(o);
      return super.equals(that) &&
            equal(this.description, that.description) &&
            equal(this.configuration, that.configuration) &&
            equal(this.deployed, that.deployed) &&
            equal(this.networkName, that.networkName);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), description, configuration, deployed, networkName);
   }

   @Override
   public ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("description", description)
            .add("configuration", configuration)
            .add("deployed", deployed)
            .add("networkName", networkName);
   }
}
