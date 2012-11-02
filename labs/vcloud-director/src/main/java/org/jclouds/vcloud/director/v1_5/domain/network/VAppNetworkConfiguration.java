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
package org.jclouds.vcloud.director.v1_5.domain.network;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents a VApp network configuration.
 *
 * <pre>
 * &lt;complexType name="VAppNetworkConfiguration" /&gt;
 * </pre>
 */
@XmlRootElement(name = "NetworkConfiguration")
@XmlType(name = "VAppNetworkConfiguration")
public class VAppNetworkConfiguration extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromVAppNetworkConfiguration(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {

      private String description;
      private NetworkConfiguration configuration;
      private Boolean deployed;
      private String networkName;

      /**
       * @see VAppNetworkConfiguration#getDescription()
       */
      public B description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see VAppNetworkConfiguration#getConfiguration()
       */
      public B configuration(NetworkConfiguration configuration) {
         this.configuration = configuration;
         return self();
      }

      /**
       * @see VAppNetworkConfiguration#isDeployed()
       */
      public B isDeployed(Boolean deployed) {
         this.deployed = deployed;
         return self();
      }

      /**
       * @see VAppNetworkConfiguration#getNetworkName()
       */
      public B networkName(String networkName) {
         this.networkName = networkName;
         return self();
      }

      @Override
      public VAppNetworkConfiguration build() {
         return new VAppNetworkConfiguration(this);
      }

      public B fromVAppNetworkConfiguration(VAppNetworkConfiguration in) {
         return fromResource(in)
               .description(in.getDescription())
               .configuration(in.getConfiguration())
               .isDeployed(in.isDeployed())
               .networkName(in.getNetworkName());
      }
   }

   @XmlElement(name = "Description")
   private String description;
   @XmlElement(name = "Configuration", required = true)
   private NetworkConfiguration configuration;
   @XmlElement(name = "IsDeployed")
   private Boolean deployed;
   @XmlAttribute(required = true)
   private String networkName;

   protected VAppNetworkConfiguration(Builder<?> builder) {
      super(builder);
      this.description = builder.description;
      this.configuration = builder.configuration;
      this.deployed = builder.deployed;
      this.networkName = builder.networkName;
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
