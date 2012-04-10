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
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.dmtf.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents the lease settings section for a vApp.
 *
 * <pre>
 * &lt;complexType name="LeaseSettingsSection">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.dmtf.org/ovf/envelope/1}Section_Type">
 *       &lt;sequence>
 *         &lt;element name="Link" type="{http://www.vmware.com/vcloud/v1.5}LinkType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="DeploymentLeaseInSeconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="StorageLeaseInSeconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="DeploymentLeaseExpiration" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="StorageLeaseExpiration" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="href" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "LeaseSettingsSection")
@XmlType(name = "LeaseSettingsSectionType")
public class LeaseSettingsSection extends SectionType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromLeaseSettingsSection(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends SectionType.Builder<B> {
      private Set<Link> links = Sets.newLinkedHashSet();
      private Integer deploymentLeaseInSeconds;
      private Integer storageLeaseInSeconds;
      private Date deploymentLeaseExpiration;
      private Date storageLeaseExpiration;
      private URI href;
      private String type;

      /**
       * @see LeaseSettingsSection#getLinks()
       */
      public B links(Set<Link> links) {
         this.links = checkNotNull(links, "links");
         return self();
      }

      /**
       * @see LeaseSettingsSection#getDeploymentLeaseInSeconds()
       */
      public B deploymentLeaseInSeconds(Integer deploymentLeaseInSeconds) {
         this.deploymentLeaseInSeconds = deploymentLeaseInSeconds;
         return self();
      }

      /**
       * @see LeaseSettingsSection#getStorageLeaseInSeconds()
       */
      public B storageLeaseInSeconds(Integer storageLeaseInSeconds) {
         this.storageLeaseInSeconds = storageLeaseInSeconds;
         return self();
      }

      /**
       * @see LeaseSettingsSection#getDeploymentLeaseExpiration()
       */
      public B deploymentLeaseExpiration(Date deploymentLeaseExpiration) {
         this.deploymentLeaseExpiration = deploymentLeaseExpiration;
         return self();
      }

      /**
       * @see LeaseSettingsSection#getStorageLeaseExpiration()
       */
      public B storageLeaseExpiration(Date storageLeaseExpiration) {
         this.storageLeaseExpiration = storageLeaseExpiration;
         return self();
      }

      /**
       * @see LeaseSettingsSection#getHref()
       */
      public B href(URI href) {
         this.href = href;
         return self();
      }

      /**
       * @see LeaseSettingsSection#getType()
       */
      public B type(String type) {
         this.type = type;
         return self();
      }

      @Override
      public LeaseSettingsSection build() {
         return new LeaseSettingsSection(this);
      }

      public B fromLeaseSettingsSection(LeaseSettingsSection in) {
         return fromSectionType(in)
               .links(in.getLinks())
               .deploymentLeaseInSeconds(in.getDeploymentLeaseInSeconds())
               .storageLeaseInSeconds(in.getStorageLeaseInSeconds())
               .deploymentLeaseExpiration(in.getDeploymentLeaseExpiration())
               .storageLeaseExpiration(in.getStorageLeaseExpiration())
               .href(in.getHref())
               .type(in.getType());
      }
   }

   @XmlElement(name = "Link")
   private Set<Link> links;
   @XmlElement(name = "DeploymentLeaseInSeconds")
   private Integer deploymentLeaseInSeconds;
   @XmlElement(name = "StorageLeaseInSeconds")
   private Integer storageLeaseInSeconds;
   @XmlElement(name = "DeploymentLeaseExpiration")
   @XmlSchemaType(name = "dateTime")
   private Date deploymentLeaseExpiration;
   @XmlElement(name = "StorageLeaseExpiration")
   @XmlSchemaType(name = "dateTime")
   private Date storageLeaseExpiration;
   @XmlAttribute
   @XmlSchemaType(name = "anyURI")
   private URI href;
   @XmlAttribute
   private String type;

   private LeaseSettingsSection(Builder<?> builder) {
      super(builder);
      this.links = ImmutableSet.copyOf(builder.links);
      this.deploymentLeaseInSeconds = builder.deploymentLeaseInSeconds;
      this.storageLeaseInSeconds = builder.storageLeaseInSeconds;
      this.deploymentLeaseExpiration = builder.deploymentLeaseExpiration;
      this.storageLeaseExpiration = builder.storageLeaseExpiration;
      this.href = builder.href;
      this.type = builder.type;
   }

   private LeaseSettingsSection() {
      // For JAXB
   }

   /**
    * Gets the value of the links property.
    */
   public Set<Link> getLinks() {
      return Collections.unmodifiableSet(links);
   }

   /**
    * Gets the value of the deploymentLeaseInSeconds property.
    */
   public Integer getDeploymentLeaseInSeconds() {
      return deploymentLeaseInSeconds;
   }

   /**
    * Gets the value of the storageLeaseInSeconds property.
    */
   public Integer getStorageLeaseInSeconds() {
      return storageLeaseInSeconds;
   }

   /**
    * Gets the value of the deploymentLeaseExpiration property.
    */
   public Date getDeploymentLeaseExpiration() {
      return deploymentLeaseExpiration;
   }

   /**
    * Gets the value of the storageLeaseExpiration property.
    */
   public Date getStorageLeaseExpiration() {
      return storageLeaseExpiration;
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
      LeaseSettingsSection that = LeaseSettingsSection.class.cast(o);
      return super.equals(that) && 
            equal(this.links, that.links) &&
            equal(this.deploymentLeaseInSeconds, that.deploymentLeaseInSeconds) &&
            equal(this.storageLeaseInSeconds, that.storageLeaseInSeconds) &&
            equal(this.deploymentLeaseExpiration, that.deploymentLeaseExpiration) &&
            equal(this.storageLeaseExpiration, that.storageLeaseExpiration) &&
            equal(this.href, that.href) &&
            equal(this.type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(),
            links,
            deploymentLeaseInSeconds,
            storageLeaseInSeconds,
            deploymentLeaseExpiration,
            storageLeaseExpiration,
            href,
            type);
   }

   @Override
   public Objects.ToStringHelper string() {
      return super.string()
            .add("links", links)
            .add("deploymentLeaseInSeconds", deploymentLeaseInSeconds)
            .add("storageLeaseInSeconds", storageLeaseInSeconds)
            .add("deploymentLeaseExpiration", deploymentLeaseExpiration)
            .add("storageLeaseExpiration", storageLeaseExpiration)
            .add("href", href)
            .add("type", type);
   }

}
