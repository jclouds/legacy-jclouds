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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;
import org.w3c.dom.Element;

import com.google.common.base.Objects;


/**
 * Represents a list of network cards existing in a VM.
 * <p/>
 * <p/>
 * <p>Java class for NetworkConnectionSection complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="NetworkConnectionSection">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.dmtf.org/ovf/envelope/1}Section_Type">
 *       &lt;sequence>
 *         &lt;element name="PrimaryNetworkConnectionIndex" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="NetworkConnection" type="{http://www.vmware.com/vcloud/v1.5}NetworkConnectionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Link" type="{http://www.vmware.com/vcloud/v1.5}LinkType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlRootElement(name="NetworkConnectionSection", namespace = VCLOUD_1_5_NS)
@XmlType(propOrder = {
      "primaryNetworkConnectionIndex",
      "networkConnection",
      "link",
      "any"
})
public class NetworkConnectionSection extends SectionType<NetworkConnectionSection> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNetworkConnectionSection(this);
   }

   public static class Builder extends SectionType.Builder<NetworkConnectionSection> {

      private Integer primaryNetworkConnectionIndex;
      private List<NetworkConnection> networkConnection;
      private List<Link> link;
      private List<Object> any;
      private URI href;
      private String type;

      /**
       * @see NetworkConnectionSection#getPrimaryNetworkConnectionIndex()
       */
      public Builder primaryNetworkConnectionIndex(Integer primaryNetworkConnectionIndex) {
         this.primaryNetworkConnectionIndex = primaryNetworkConnectionIndex;
         return this;
      }

      /**
       * @see NetworkConnectionSection#getNetworkConnection()
       */
      public Builder networkConnection(List<NetworkConnection> networkConnection) {
         this.networkConnection = networkConnection;
         return this;
      }

      /**
       * @see NetworkConnectionSection#getLink()
       */
      public Builder link(List<Link> link) {
         this.link = link;
         return this;
      }

      /**
       * @see NetworkConnectionSection#getAny()
       */
      public Builder any(List<Object> any) {
         this.any = any;
         return this;
      }

      /**
       * @see NetworkConnectionSection#getHref()
       */
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see NetworkConnectionSection#getType()
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }


      public NetworkConnectionSection build() {
         NetworkConnectionSection networkConnectionSection = new NetworkConnectionSection(info, networkConnection, link, any);
         networkConnectionSection.setPrimaryNetworkConnectionIndex(primaryNetworkConnectionIndex);
         networkConnectionSection.setHref(href);
         networkConnectionSection.setType(type);
         return networkConnectionSection;
      }

      public Builder fromNetworkConnectionSection(NetworkConnectionSection in) {
         return fromSection(in)
               .primaryNetworkConnectionIndex(in.getPrimaryNetworkConnectionIndex())
               .networkConnection(in.getNetworkConnection())
               .link(in.getLink())
               .any(in.getAny())
               .href(in.getHref())
               .type(in.getType());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(SectionType<NetworkConnectionSection> in) {
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

   private NetworkConnectionSection(@Nullable String info, List<NetworkConnection> networkConnection, List<Link> link, List<Object> any) {
      super(info);
      this.networkConnection = networkConnection;
      this.link = link;
      this.any = any;
   }

   private NetworkConnectionSection() {
      // For JAXB
   }

   @XmlElement(name = "PrimaryNetworkConnectionIndex")
   protected Integer primaryNetworkConnectionIndex;
   @XmlElement(name = "NetworkConnection")
   protected List<NetworkConnection> networkConnection;
   @XmlElement(name = "Link")
   protected List<Link> link;
   @XmlAnyElement(lax = true)
   protected List<Object> any;
   @XmlAttribute
   @XmlSchemaType(name = "anyURI")
   protected URI href;
   @XmlAttribute
   protected String type;

   /**
    * Gets the value of the primaryNetworkConnectionIndex property.
    *
    * @return possible object is
    *         {@link Integer }
    */
   public Integer getPrimaryNetworkConnectionIndex() {
      return primaryNetworkConnectionIndex;
   }

   /**
    * Sets the value of the primaryNetworkConnectionIndex property.
    *
    * @param value allowed object is
    *              {@link Integer }
    */
   public void setPrimaryNetworkConnectionIndex(Integer value) {
      this.primaryNetworkConnectionIndex = value;
   }

   /**
    * Gets the value of the networkConnection property.
    * <p/>
    * <p/>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the networkConnection property.
    * <p/>
    * <p/>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getNetworkConnection().add(newItem);
    * </pre>
    * <p/>
    * <p/>
    * <p/>
    * Objects of the following type(s) are allowed in the list
    * {@link NetworkConnection }
    */
   public List<NetworkConnection> getNetworkConnection() {
      if (networkConnection == null) {
         networkConnection = new ArrayList<NetworkConnection>();
      }
      return this.networkConnection;
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
   public List<Link> getLink() {
      if (link == null) {
         link = new ArrayList<Link>();
      }
      return this.link;
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
      NetworkConnectionSection that = NetworkConnectionSection.class.cast(o);
      return equal(primaryNetworkConnectionIndex, that.primaryNetworkConnectionIndex) &&
            equal(networkConnection, that.networkConnection) &&
            equal(link, that.link) &&
            equal(any, that.any) &&
            equal(href, that.href) &&
            equal(type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(primaryNetworkConnectionIndex,
            networkConnection,
            link,
            any,
            href,
            type);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("primaryNetworkConnectionIndex", primaryNetworkConnectionIndex)
            .add("networkConnection", networkConnection)
            .add("link", link)
            .add("any", any)
            .add("href", href)
            .add("type", type).toString();
   }

}
