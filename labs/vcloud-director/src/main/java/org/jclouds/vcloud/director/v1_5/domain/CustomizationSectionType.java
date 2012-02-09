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
 *                 Represents a vApp template customization settings section.
 *             
 * 
 * <p>Java class for CustomizationSection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomizationSection">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.dmtf.org/ovf/envelope/1}Section_Type">
 *       &lt;sequence>
 *         &lt;element name="CustomizeOnInstantiate" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomizationSection", propOrder = {
    "customizeOnInstantiate",
    "link",
    "any"
})
public class CustomizationSectionType<T extends CustomizationSectionType<T>>
    extends Section<T>

{
   public static <T extends CustomizationSectionType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromCustomizationSectionType(this);
   }

   public static class Builder<T extends CustomizationSectionType<T>> extends Section.Builder<T> {
      
      private boolean customizeOnInstantiate;
      private List<Link> link;
      private List<Object> any;
      private String href;
      private String type;

      /**
       * @see CustomizationSectionType#getCustomizeOnInstantiate()
       */
      public Builder<T> customizeOnInstantiate(boolean customizeOnInstantiate) {
         this.customizeOnInstantiate = customizeOnInstantiate;
         return this;
      }

      /**
       * @see CustomizationSectionType#getLink()
       */
      public Builder<T> link(List<Link> link) {
         this.link = link;
         return this;
      }

      /**
       * @see CustomizationSectionType#getAny()
       */
      public Builder<T> any(List<Object> any) {
         this.any = any;
         return this;
      }

      /**
       * @see CustomizationSectionType#getHref()
       */
      public Builder<T> href(String href) {
         this.href = href;
         return this;
      }

      /**
       * @see CustomizationSectionType#getType()
       */
      public Builder<T> type(String type) {
         this.type = type;
         return this;
      }


      public CustomizationSectionType<T> build() {
         CustomizationSectionType<T> customizationSection = new CustomizationSectionType<T>(info, link, any);
         customizationSection.setCustomizeOnInstantiate(customizeOnInstantiate);
         customizationSection.setHref(href);
         customizationSection.setType(type);
         return customizationSection;
      }
      
      /**
       * @see Section#getInfo
       */
      public Builder<T> info(String info) {
         this.info = info;
         return this;
      }

     /**
      * {@inheritDoc}
      */
     @SuppressWarnings("unchecked")
      @Override
      public Builder<T> fromSection(Section<T> in) {
          return Builder.class.cast(super.fromSection(in));
      }
      
      public Builder<T> fromCustomizationSectionType(CustomizationSectionType<T> in) {
         return fromSection(in)
            .customizeOnInstantiate(in.isCustomizeOnInstantiate())
            .link(in.getLink())
            .any(in.getAny())
            .href(in.getHref())
            .type(in.getType());
      }
   }

   private CustomizationSectionType() {
      // For JAXB and builder use
   }

   private CustomizationSectionType(String info, List<Link> link, List<Object> any) {
      super(info);
      this.link = link;
      this.any = any;
   }


    @XmlElement(name = "CustomizeOnInstantiate")
    protected boolean customizeOnInstantiate;
    @XmlElement(name = "Link")
    protected List<Link> link;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute
    protected String type;

    /**
     * Gets the value of the customizeOnInstantiate property.
     * 
     */
    public boolean isCustomizeOnInstantiate() {
        return customizeOnInstantiate;
    }

    /**
     * Sets the value of the customizeOnInstantiate property.
     * 
     */
    public void setCustomizeOnInstantiate(boolean value) {
        this.customizeOnInstantiate = value;
    }

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
      CustomizationSectionType<?> that = CustomizationSectionType.class.cast(o);
      return equal(customizeOnInstantiate, that.customizeOnInstantiate) && 
           equal(link, that.link) && 
           equal(any, that.any) && 
           equal(href, that.href) && 
           equal(type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(customizeOnInstantiate, 
           link, 
           any, 
           href, 
           type);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("customizeOnInstantiate", customizeOnInstantiate)
            .add("link", link)
            .add("any", any)
            .add("href", href)
            .add("type", type).toString();
   }

}
