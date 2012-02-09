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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;


/**
 * 
 *                 Represents a VApp network configuration.
 *             
 * 
 * <p>Java class for VAppNetworkConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VAppNetworkConfiguration">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ResourceType">
 *       &lt;sequence>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Configuration" type="{http://www.vmware.com/vcloud/v1.5}NetworkConfigurationType"/>
 *         &lt;element name="IsDeployed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="networkName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VAppNetworkConfiguration", propOrder = {
    "description",
    "configuration",
    "isDeployed"
})
public class VAppNetworkConfiguration<T extends VAppNetworkConfiguration<T>>
    extends ResourceType<T>

{
   public static <T extends VAppNetworkConfiguration<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromVAppNetworkConfiguration(this);
   }

   public static class Builder<T extends VAppNetworkConfiguration<T>> extends ResourceType.Builder<T> {
      
      private String description;
      private NetworkConfiguration configuration;
      private Boolean isDeployed;
      private String networkName;

      /**
       * @see VAppNetworkConfiguration#getDescription()
       */
      public Builder<T> description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see VAppNetworkConfiguration#getConfiguration()
       */
      public Builder<T> configuration(NetworkConfiguration configuration) {
         this.configuration = configuration;
         return this;
      }

      /**
       * @see VAppNetworkConfiguration#getIsDeployed()
       */
      public Builder<T> isDeployed(Boolean isDeployed) {
         this.isDeployed = isDeployed;
         return this;
      }

      /**
       * @see VAppNetworkConfiguration#getNetworkName()
       */
      public Builder<T> networkName(String networkName) {
         this.networkName = networkName;
         return this;
      }


      public VAppNetworkConfiguration<T> build() {
         VAppNetworkConfiguration<T> vAppNetworkConfiguration = new VAppNetworkConfiguration<T>();
         vAppNetworkConfiguration.setDescription(description);
         vAppNetworkConfiguration.setConfiguration(configuration);
         vAppNetworkConfiguration.setIsDeployed(isDeployed);
         vAppNetworkConfiguration.setNetworkName(networkName);
         return vAppNetworkConfiguration;
      }

      
      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder<T> href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder<T> type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder<T> links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder<T> link(Link link) {
         super.link(link);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      public Builder<T> fromResourceType(ResourceType<T> in) {
          return Builder.class.cast(super.fromResourceType(in));
      }
      public Builder<T> fromVAppNetworkConfiguration(VAppNetworkConfiguration<T> in) {
         return fromResourceType(in)
            .description(in.getDescription())
            .configuration(in.getConfiguration())
            .isDeployed(in.isDeployed())
            .networkName(in.getNetworkName());
      }
   }

   private VAppNetworkConfiguration() {
      // For JAXB and builder use
   }



    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "Configuration", required = true)
    protected NetworkConfiguration configuration;
    @XmlElement(name = "IsDeployed")
    protected Boolean isDeployed;
    @XmlAttribute(required = true)
    protected String networkName;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the configuration property.
     * 
     * @return
     *     possible object is
     *     {@link NetworkConfiguration }
     *     
     */
    public NetworkConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Sets the value of the configuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link NetworkConfiguration }
     *     
     */
    public void setConfiguration(NetworkConfiguration value) {
        this.configuration = value;
    }

    /**
     * Gets the value of the isDeployed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeployed() {
        return isDeployed;
    }

    /**
     * Sets the value of the isDeployed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsDeployed(Boolean value) {
        this.isDeployed = value;
    }

    /**
     * Gets the value of the networkName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetworkName() {
        return networkName;
    }

    /**
     * Sets the value of the networkName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetworkName(String value) {
        this.networkName = value;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VAppNetworkConfiguration<?> that = VAppNetworkConfiguration.class.cast(o);
      return equal(description, that.description) && 
           equal(configuration, that.configuration) && 
           equal(isDeployed, that.isDeployed) && 
           equal(networkName, that.networkName);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(description, 
           configuration, 
           isDeployed, 
           networkName);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("description", description)
            .add("configuration", configuration)
            .add("isDeployed", isDeployed)
            .add("networkName", networkName).toString();
   }

}
