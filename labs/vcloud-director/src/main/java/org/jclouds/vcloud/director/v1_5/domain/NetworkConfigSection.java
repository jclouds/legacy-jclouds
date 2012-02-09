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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.ovf.Section;
import org.w3c.dom.Element;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents the network config section of a vApp.
 *             
 * 
 * <p>Java class for NetworkConfigSection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NetworkConfigSection", propOrder = {
    "link",
    "networkConfig",
    "any"
})
public class NetworkConfigSection<T extends NetworkConfigSection<T>>
    extends Section<T>

{
   public static <T extends NetworkConfigSection<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromNetworkConfigSection(this);
   }

   public static class Builder<T extends NetworkConfigSection<T>> extends Section.Builder<T> {
      
      private List<Link> link;
      private List<VAppNetworkConfiguration<?>> networkConfig;
      private List<Object> any;
      private String href;
      private String type;

      /**
       * @see NetworkConfigSection#getLink()
       */
      public Builder<T> link(List<Link> link) {
         this.link = link;
         return this;
      }

      /**
       * @see NetworkConfigSection#getNetworkConfig()
       */
      public Builder<T> networkConfig(List<VAppNetworkConfiguration<?>> networkConfig) {
         this.networkConfig = networkConfig;
         return this;
      }

      /**
       * @see NetworkConfigSection#getAny()
       */
      public Builder<T> any(List<Object> any) {
         this.any = any;
         return this;
      }

      /**
       * @see NetworkConfigSection#getHref()
       */
      public Builder<T> href(String href) {
         this.href = href;
         return this;
      }

      /**
       * @see NetworkConfigSection#getType()
       */
      public Builder<T> type(String type) {
         this.type = type;
         return this;
      }


      public NetworkConfigSection<T> build() {
         NetworkConfigSection<T> networkConfigSection = new NetworkConfigSection<T>(link, networkConfig, any);
         networkConfigSection.setHref(href);
         networkConfigSection.setType(type);
         return networkConfigSection;
      }

     /**
      * {@inheritDoc}
      */
     @SuppressWarnings("unchecked")
      public Builder<T> fromSection(Section<T> in) {
          return Builder.class.cast(super.fromSection(in));
      }
      public Builder<T> fromNetworkConfigSection(NetworkConfigSection<T> in) {
         return fromSection(in)
            .link(in.getLink())
            .networkConfig(in.getNetworkConfig())
            .any(in.getAny())
            .href(in.getHref())
            .type(in.getType());
      }
   }

   private NetworkConfigSection() {
      // For JAXB and builder use
   }

   private NetworkConfigSection(List<Link> link, List<VAppNetworkConfiguration<?>> networkConfig, List<Object> any) {
      this.link = link;
      this.networkConfig = networkConfig;
      this.any = any;
   }


    @XmlElement(name = "Link")
    protected List<Link> link;
    @XmlElement(name = "NetworkConfig")
    protected List<VAppNetworkConfiguration<?>> networkConfig;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute
    protected String type;

    /**
     * Gets the value of the link property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the link property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LinkType }
     * 
     * 
     */
    public List<Link> getLink() {
        if (link == null) {
            link = new ArrayList<Link>();
        }
        return this.link;
    }

    /**
     * Gets the value of the networkConfig property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the networkConfig property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetworkConfig().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VAppNetworkConfigurationType }
     * 
     * 
     */
    public List<VAppNetworkConfiguration<?>> getNetworkConfig() {
        if (networkConfig == null) {
            networkConfig = new ArrayList<VAppNetworkConfiguration<?>>();
        }
        return this.networkConfig;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the href property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
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
      NetworkConfigSection<?> that = NetworkConfigSection.class.cast(o);
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
