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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;
import org.w3c.dom.Element;

import com.google.common.base.Objects;


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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "LeaseSettingsSection", namespace = VCLOUD_1_5_NS)
@XmlType(propOrder = {
      "link",
      "deploymentLeaseInSeconds",
      "storageLeaseInSeconds",
      "deploymentLeaseExpiration",
      "storageLeaseExpiration",
      "any"
})
public class LeaseSettingsSection extends SectionType<LeaseSettingsSection> {
   public static <T extends LeaseSettingsSection> Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromLeaseSettingsSection(this);
   }

   public static class Builder extends SectionType.Builder<LeaseSettingsSection> {
      private List<Link> links;
      private Integer deploymentLeaseInSeconds;
      private Integer storageLeaseInSeconds;
      private XMLGregorianCalendar deploymentLeaseExpiration;
      private XMLGregorianCalendar storageLeaseExpiration;
      private List<Object> any;
      private URI href;
      private String type;

      /**
       * @see LeaseSettingsSection#getLinks()
       */
      public Builder links(List<Link> links) {
         this.links = links;
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
       * @see LeaseSettingsSection#getAny()
       */
      public Builder any(List<Object> any) {
         this.any = any;
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
         LeaseSettingsSection leaseSettingsSection = new LeaseSettingsSection(info, links, any);
         leaseSettingsSection.setDeploymentLeaseInSeconds(deploymentLeaseInSeconds);
         leaseSettingsSection.setStorageLeaseInSeconds(storageLeaseInSeconds);
         leaseSettingsSection.setDeploymentLeaseExpiration(deploymentLeaseExpiration);
         leaseSettingsSection.setStorageLeaseExpiration(storageLeaseExpiration);
         leaseSettingsSection.setHref(href);
         leaseSettingsSection.setType(type);
         return leaseSettingsSection;
      }

      public Builder fromLeaseSettingsSection(LeaseSettingsSection in) {
         return fromSection(in)
               .links(in.getLinks())
               .deploymentLeaseInSeconds(in.getDeploymentLeaseInSeconds())
               .storageLeaseInSeconds(in.getStorageLeaseInSeconds())
               .deploymentLeaseExpiration(in.getDeploymentLeaseExpiration())
               .storageLeaseExpiration(in.getStorageLeaseExpiration())
               .any(in.getAny())
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
   }

   @XmlElement(name = "Link")
   protected List<Link> link;
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
   @XmlAnyElement(lax = true)
   protected List<Object> any;
   @XmlAttribute
   @XmlSchemaType(name = "anyURI")
   protected URI href;
   @XmlAttribute
   protected String type;

   private LeaseSettingsSection(@Nullable String info, List<Link> link, List<Object> any) {
      super(info);
      this.link = link;
      this.any = any;
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
   public List<Link> getLinks() {
      if (link == null) {
         link = new ArrayList<Link>();
      }
      return this.link;
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
    * Sets the value of the deploymentLeaseInSeconds property.
    *
    * @param value allowed object is
    *              {@link Integer }
    */
   public void setDeploymentLeaseInSeconds(Integer value) {
      this.deploymentLeaseInSeconds = value;
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
    * Sets the value of the storageLeaseInSeconds property.
    *
    * @param value allowed object is
    *              {@link Integer }
    */
   public void setStorageLeaseInSeconds(Integer value) {
      this.storageLeaseInSeconds = value;
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
    * Sets the value of the deploymentLeaseExpiration property.
    *
    * @param value allowed object is
    *              {@link XMLGregorianCalendar }
    */
   public void setDeploymentLeaseExpiration(XMLGregorianCalendar value) {
      this.deploymentLeaseExpiration = value;
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
    * Sets the value of the storageLeaseExpiration property.
    *
    * @param value allowed object is
    *              {@link XMLGregorianCalendar }
    */
   public void setStorageLeaseExpiration(XMLGregorianCalendar value) {
      this.storageLeaseExpiration = value;
   }

   /**
    * Gets the value of the any property.
    * <p/>
    * <p/>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the any property.
    * <p/>
    * <p/>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getAny().add(newItem);
    * </pre>
    * <p/>
    * <p/>
    * <p/>
    * Objects of the following type(s) are allowed in the list
    * {@link Object }
    * {@link Element }
    */
   public List<Object> getAny() {
      if (any == null) {
         any = new ArrayList<Object>();
      }
      return this.any;
   }

   /**
    * @return the value of the href property.
    */
   public URI getHref() {
      return href;
   }

   /**
    * Sets the value of the href property.
    *
    * @param value the value to set
    */
   public void setHref(URI value) {
      this.href = value;
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

   /**
    * Sets the value of the type property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setType(String value) {
      this.type = value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      LeaseSettingsSection that = LeaseSettingsSection.class.cast(o);
      return equal(link, that.link) &&
            equal(deploymentLeaseInSeconds, that.deploymentLeaseInSeconds) &&
            equal(storageLeaseInSeconds, that.storageLeaseInSeconds) &&
            equal(deploymentLeaseExpiration, that.deploymentLeaseExpiration) &&
            equal(storageLeaseExpiration, that.storageLeaseExpiration) &&
            equal(any, that.any) &&
            equal(href, that.href) &&
            equal(type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(link,
            deploymentLeaseInSeconds,
            storageLeaseInSeconds,
            deploymentLeaseExpiration,
            storageLeaseExpiration,
            any,
            href,
            type);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("link", link)
            .add("deploymentLeaseInSeconds", deploymentLeaseInSeconds)
            .add("storageLeaseInSeconds", storageLeaseInSeconds)
            .add("deploymentLeaseExpiration", deploymentLeaseExpiration)
            .add("storageLeaseExpiration", storageLeaseExpiration)
            .add("any", any)
            .add("href", href)
            .add("type", type).toString();
   }

}
