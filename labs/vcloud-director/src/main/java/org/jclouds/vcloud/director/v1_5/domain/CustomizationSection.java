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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType.Builder;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a vApp template customization settings section.
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
 */
@XmlRootElement(name = "CustomizationSection")
public class CustomizationSection extends SectionType<CustomizationSection> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromCustomizationSection(this);
   }

   public static class Builder extends SectionType.Builder<CustomizationSection> {
      private boolean customizeOnInstantiate;
      private Set<Link> links = Sets.newLinkedHashSet();
      private URI href;
      private String type;

      /**
       * @see CustomizationSection#isCustomizeOnInstantiate()
       */
      public Builder customizeOnInstantiate(boolean customizeOnInstantiate) {
         this.customizeOnInstantiate = customizeOnInstantiate;
         return this;
      }

      /**
       * @see CustomizationSection#getLinks()
       */
      public Builder links(Set<Link> links) {
         this.links = checkNotNull(links, "links");
         return this;
      }

      /**
       * @see CustomizationSection#getHref()
       */
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see CustomizationSection#getType()
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }


      @Override
      public CustomizationSection build() {
         return new CustomizationSection(info, required, customizeOnInstantiate, links, href, type);
      }

      public Builder fromCustomizationSection(CustomizationSection in) {
         return fromSectionType(in)
               .customizeOnInstantiate(in.isCustomizeOnInstantiate())
               .links(in.getLinks())
               .href(in.getHref())
               .type(in.getType());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSectionType(SectionType<CustomizationSection> in) {
         return Builder.class.cast(super.fromSectionType(in));
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

   private CustomizationSection(@Nullable String info, @Nullable Boolean required, boolean customizeOnInstantiate, Set<Link> links,
                               URI href, String type) {
      super(info, required);
      this.customizeOnInstantiate = customizeOnInstantiate;
      this.links = ImmutableSet.copyOf(links);
      this.href = href;
      this.type = type;
   }

   private CustomizationSection() {
      // For JAXB
   }

   @XmlElement(name = "CustomizeOnInstantiate")
   protected boolean customizeOnInstantiate;
   @XmlElement(name = "Link")
   protected Set<Link> links = Sets.newLinkedHashSet();
   @XmlAttribute
   @XmlSchemaType(name = "anyURI")
   protected URI href;
   @XmlAttribute
   protected String type;

   /**
    * Gets the value of the customizeOnInstantiate property.
    */
   public boolean isCustomizeOnInstantiate() {
      return customizeOnInstantiate;
   }

   /**
    * Gets the value of the links property.
    */
   public Set<Link> getLinks() {
      return Collections.unmodifiableSet(this.links);
   }

   /**
    * Gets the value of the href property.
    *
    * @return possible object is
    *         {@link String }
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
      CustomizationSection that = CustomizationSection.class.cast(o);
      return super.equals(that) && 
            equal(customizeOnInstantiate, that.customizeOnInstantiate) &&
            equal(links, that.links) &&
            equal(href, that.href) &&
            equal(type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(
            super.hashCode(),
            customizeOnInstantiate,
            links,
            href,
            type);
   }

   @Override
   public Objects.ToStringHelper string() {
      return super.string()
            .add("customizeOnInstantiate", customizeOnInstantiate)
            .add("links", links)
            .add("href", href)
            .add("type", type);
   }

}
