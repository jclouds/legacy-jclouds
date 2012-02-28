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
 * Represents the network config section of a vApp.
 * <p/>
 * <p/>
 * <p>Java class for NetworkConfigSection complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="NetworkConfigSection">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.dmtf.org/ovf/envelope/1}Section_Type">
 *       &lt;sequence>
 *         &lt;element name="Link" type="{http://www.vmware.com/vcloud/v1.5}LinkType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="NetworkConfig" type="{http://www.vmware.com/vcloud/v1.5}VAppNetworkConfigurationType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlRootElement(name="NetworkConfigSection", namespace = VCLOUD_1_5_NS)
@XmlType( propOrder = {
      "link",
      "networkConfig",
      "any"
})
public class NetworkConfigSection extends SectionType<NetworkConfigSection> {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNetworkConfigSection(this);
   }

   public static class Builder extends SectionType.Builder<NetworkConfigSection> {

      private List<Link> link;
      private List<VAppNetworkConfiguration<?>> networkConfig;
      private List<Object> any;
      private URI href;
      private String type;

      /**
       * @see NetworkConfigSection#getLink()
       */
      public Builder link(List<Link> link) {
         this.link = link;
         return this;
      }

      /**
       * @see NetworkConfigSection#getNetworkConfig()
       */
      public Builder networkConfig(List<VAppNetworkConfiguration<?>> networkConfig) {
         this.networkConfig = networkConfig;
         return this;
      }

      /**
       * @see NetworkConfigSection#getAny()
       */
      public Builder any(List<Object> any) {
         this.any = any;
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


      public NetworkConfigSection build() {
         NetworkConfigSection networkConfigSection = new NetworkConfigSection(info, link, networkConfig, any);
         networkConfigSection.setHref(href);
         networkConfigSection.setType(type);
         return networkConfigSection;
      }

      public Builder fromNetworkConfigSection(NetworkConfigSection in) {
         return fromSection(in)
               .link(in.getLink())
               .networkConfig(in.getNetworkConfig())
               .any(in.getAny())
               .href(in.getHref())
               .type(in.getType());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(SectionType<NetworkConfigSection> in) {
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
   @XmlElement(name = "NetworkConfig")
   protected List<VAppNetworkConfiguration<?>> networkConfig;
   @XmlAnyElement(lax = true)
   protected List<Object> any;
   @XmlAttribute
   @XmlSchemaType(name = "anyURI")
   protected URI href;
   @XmlAttribute
   protected String type;
   
   private NetworkConfigSection(@Nullable String info, List<Link> link, List<VAppNetworkConfiguration<?>> networkConfig, List<Object> any) {
      super(info);
      this.link = link;
      this.networkConfig = networkConfig;
      this.any = any;
   }

   private NetworkConfigSection() {
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
   public List<Link> getLink() {
      if (link == null) {
         link = new ArrayList<Link>();
      }
      return this.link;
   }

   /**
    * Gets the value of the networkConfig property.
    * <p/>
    * <p/>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the networkConfig property.
    * <p/>
    * <p/>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getNetworkConfig().add(newItem);
    * </pre>
    * <p/>
    * <p/>
    * <p/>
    * Objects of the following type(s) are allowed in the list
    * {@link VAppNetworkConfiguration }
    */
   public List<VAppNetworkConfiguration<?>> getNetworkConfig() {
      if (networkConfig == null) {
         networkConfig = new ArrayList<VAppNetworkConfiguration<?>>();
      }
      return this.networkConfig;
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
    * Gets the value of the href property.
    */
   public URI getHref() {
      return href;
   }

   /**
    * Sets the value of the href property.
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
      NetworkConfigSection that = NetworkConfigSection.class.cast(o);
      return equal(link, that.link) &&
            equal(networkConfig, that.networkConfig) &&
            equal(any, that.any) &&
            equal(href, that.href) &&
            equal(type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(link,
            networkConfig,
            any,
            href,
            type);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("link", link)
            .add("networkConfig", networkConfig)
            .add("any", any)
            .add("href", href)
            .add("type", type).toString();
   }

}
