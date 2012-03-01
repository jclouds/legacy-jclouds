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
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;


/**
 * Represents the lease settings section for a vApp.
 * <p/>
 * <p/>
 * <p>Java class for LeaseSettingsSection complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
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
@XmlType(propOrder = {
      "links",
      "deploymentLeaseInSeconds",
      "storageLeaseInSeconds",
      "deploymentLeaseExpiration",
      "storageLeaseExpiration"
})
public class LeaseSettingsSection extends SectionType<LeaseSettingsSection> {
   public static <T extends LeaseSettingsSection> Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromLeaseSettingsSection(this);
   }

   public static class Builder extends SectionType.Builder<LeaseSettingsSection> {
      private Set<Link> links = Sets.newLinkedHashSet();
      private Integer deploymentLeaseInSeconds;
      private Integer storageLeaseInSeconds;
      private XMLGregorianCalendar deploymentLeaseExpiration;
      private XMLGregorianCalendar storageLeaseExpiration;
      private URI href;
      private String type;

      /**
       * @see LeaseSettingsSection#getLinks()
       */
      public Builder links(Set<Link> links) {
         this.links = checkNotNull(links, "links");
         return this;
      }

      /**
       * @see LeaseSettingsSection#getDeploymentLeaseInSeconds()
       */
      public Builder deploymentLeaseInSeconds(Integer deploymentLeaseInSeconds) {
         this.deploymentLeaseInSeconds = deploymentLeaseInSeconds;
         return this;
      }

      /**
       * @see LeaseSettingsSection#getStorageLeaseInSeconds()
       */
      public Builder storageLeaseInSeconds(Integer storageLeaseInSeconds) {
         this.storageLeaseInSeconds = storageLeaseInSeconds;
         return this;
      }

      /**
       * @see LeaseSettingsSection#getDeploymentLeaseExpiration()
       */
      public Builder deploymentLeaseExpiration(XMLGregorianCalendar deploymentLeaseExpiration) {
         this.deploymentLeaseExpiration = deploymentLeaseExpiration;
         return this;
      }

      /**
       * @see LeaseSettingsSection#getStorageLeaseExpiration()
       */
      public Builder storageLeaseExpiration(XMLGregorianCalendar storageLeaseExpiration) {
         this.storageLeaseExpiration = storageLeaseExpiration;
         return this;
      }

      /**
       * @see LeaseSettingsSection#getHref()
       */
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see LeaseSettingsSection#getType()
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }


      public LeaseSettingsSection build() {
         return new LeaseSettingsSection(info, required, links, deploymentLeaseInSeconds,
               storageLeaseInSeconds, deploymentLeaseExpiration,
               storageLeaseExpiration, href, type);
      }

      public Builder fromLeaseSettingsSection(LeaseSettingsSection in) {
         return fromSection(in)
               .links(in.getLinks())
               .deploymentLeaseInSeconds(in.getDeploymentLeaseInSeconds())
               .storageLeaseInSeconds(in.getStorageLeaseInSeconds())
               .deploymentLeaseExpiration(in.getDeploymentLeaseExpiration())
               .storageLeaseExpiration(in.getStorageLeaseExpiration())
               .href(in.getHref())
               .type(in.getType());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(SectionType<LeaseSettingsSection> in) {
         return Builder.class.cast(super.fromSection(in));
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
   protected Set<Link> links;
   @XmlElement(name = "DeploymentLeaseInSeconds")
   protected Integer deploymentLeaseInSeconds;
   @XmlElement(name = "StorageLeaseInSeconds")
   protected Integer storageLeaseInSeconds;
   @XmlElement(name = "DeploymentLeaseExpiration")
   @XmlSchemaType(name = "dateTime")
   protected XMLGregorianCalendar deploymentLeaseExpiration;
   @XmlElement(name = "StorageLeaseExpiration")
   @XmlSchemaType(name = "dateTime")
   protected XMLGregorianCalendar storageLeaseExpiration;
   @XmlAttribute
   @XmlSchemaType(name = "anyURI")
   protected URI href;
   @XmlAttribute
   protected String type;

   private LeaseSettingsSection(@Nullable String info, @Nullable Boolean required, Set<Link> links, Integer deploymentLeaseInSeconds,
                                Integer storageLeaseInSeconds, XMLGregorianCalendar deploymentLeaseExpiration,
                                XMLGregorianCalendar storageLeaseExpiration, URI href, String type) {
      super(info, required);
      this.links = links;
      this.deploymentLeaseInSeconds = deploymentLeaseInSeconds;
      this.storageLeaseInSeconds = storageLeaseInSeconds;
      this.deploymentLeaseExpiration = deploymentLeaseExpiration;
      this.storageLeaseExpiration = storageLeaseExpiration;
      this.href = href;
      this.type = type;
   }

   private LeaseSettingsSection() {
      // For JAXB
   }

   /**
    * Gets the value of the link property.
    * <p/>
    * <p/>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the link property.
    * <p/>
    * <p/>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getLink().add(newItem);
    * </pre>
    * <p/>
    * <p/>
    * <p/>
    * Objects of the following type(s) are allowed in the list
    * {@link Link }
    */
   public Set<Link> getLinks() {
      return Collections.unmodifiableSet(this.links);
   }

   /**
    * Gets the value of the deploymentLeaseInSeconds property.
    *
    * @return possible object is
    *         {@link Integer }
    */
   public Integer getDeploymentLeaseInSeconds() {
      return deploymentLeaseInSeconds;
   }

   /**
    * Gets the value of the storageLeaseInSeconds property.
    *
    * @return possible object is
    *         {@link Integer }
    */
   public Integer getStorageLeaseInSeconds() {
      return storageLeaseInSeconds;
   }

   /**
    * Gets the value of the deploymentLeaseExpiration property.
    *
    * @return possible object is
    *         {@link XMLGregorianCalendar }
    */
   public XMLGregorianCalendar getDeploymentLeaseExpiration() {
      return deploymentLeaseExpiration;
   }

   /**
    * Gets the value of the storageLeaseExpiration property.
    *
    * @return possible object is
    *         {@link XMLGregorianCalendar }
    */
   public XMLGregorianCalendar getStorageLeaseExpiration() {
      return storageLeaseExpiration;
   }

   /**
    * @return the value of the href property.
    */
   public URI getHref() {
      return href;
   }

   /**
    * Gets the value of the type property.
    *
    * @return possible object is
    *         {@link String }
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
            equal(links, that.links) &&
            equal(deploymentLeaseInSeconds, that.deploymentLeaseInSeconds) &&
            equal(storageLeaseInSeconds, that.storageLeaseInSeconds) &&
            equal(deploymentLeaseExpiration, that.deploymentLeaseExpiration) &&
            equal(storageLeaseExpiration, that.storageLeaseExpiration) &&
            equal(href, that.href) &&
            equal(type, that.type);
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
