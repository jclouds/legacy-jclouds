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
package org.jclouds.vcloud.director.v1_5.domain.ovf;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_VMW_NS;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.domain.Link;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * An OperatingSystemSection specifies the operating system installed on a virtual machine.
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@XmlRootElement(name = "OperatingSystemSection")
public class OperatingSystemSection extends SectionType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromOperatingSystemSection(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends SectionType.Builder<B> {
      private Integer id;
      private String description;
      private String version;
      private String osType;
      private URI href;
      private String type;
      private Set<Link> links;

      /**
       * @see OperatingSystemSection#getId()
       */
      public B id(Integer id) {
         this.id = id;
         return self();
      }

      /**
       * @see OperatingSystemSection#getVersion()
       */
      public B version(String version) {
         this.version = version;
         return self();
      }

      /**
       * @see OperatingSystemSection#getDescription
       */
      public B description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see OperatingSystemSection#getOsType()
       */
      public B osType(String osType) {
         this.osType = osType;
         return self();
      }
      
      /**
       * @see OperatingSystemSection#getHref()
       */
      public B href(URI href) {
         this.href = href;
         return self();
      }

      /**
       * @see OperatingSystemSection#getType()
       */
      public B type(String type) {
         this.type = type;
         return self();
      }

      /**
       * @see OperatingSystemSection#getLinks()
       */
      public B links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return self();
      }

      /**
       * @see ResourceType#getLinks()
       */
      public B link(Link link) {
         if (links == null)
            links = Sets.newLinkedHashSet();
         this.links.add(checkNotNull(link, "link"));
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public OperatingSystemSection build() {
         return new OperatingSystemSection(this);
      }

      public B fromOperatingSystemSection(OperatingSystemSection in) {
         return fromSectionType(in).id(in.getId()).version(in.getVersion()).description(in.getDescription())
               .osType(in.getOsType()).href(in.getHref()).type(in.getType()).links(in.getLinks());
      }
   }

   @XmlAttribute(required = true)
   protected Integer id;
   @XmlAttribute
   protected String version;
   @XmlElement
   protected String description;
   @XmlAttribute(namespace = VCLOUD_VMW_NS)
   protected String osType;
   @XmlAttribute(namespace = VCLOUD_1_5_NS)
   private URI href;
   @XmlAttribute(namespace = VCLOUD_1_5_NS)
   private String type;
   @XmlElement(name = "Link", namespace = VCLOUD_1_5_NS)
   private Set<Link> links;

   public OperatingSystemSection(Builder<?> builder) {
      super(builder);
      this.id = builder.id;
      this.description = builder.description;
      this.version = builder.version;
      this.osType = builder.osType;
      this.href = builder.href;
      this.type = builder.type;
      this.links = builder.links;
   }

   protected OperatingSystemSection() {
      // For Builders and JAXB
   }

   /**
    * Gets the OVF id
    *
    * @see org.jclouds.vcloud.director.v1_5.domain.cim.OSType#getCode()
    */
   public Integer getId() {
      return id;
   }

   /**
    * Gets the version
    */
   public String getVersion() {
      return version;
   }

   /**
    * Gets the description or null
    */
   public String getDescription() {
      return description;
   }

   /**
    * Gets the osType
    */
   public String getOsType() {
      return osType;
   }

   /**
    * Contains the URI to the entity.
    *
    * @see ResourceType#getHref()
    */
   public URI getHref() {
      return href;
   }

   /**
    * Contains the type of the the entity.
    *
    * @see ResourceType#getType()
    */
   public String getType() {
      return type;
   }

   /**
    * Set of optional links to an entity or operation associated with this object.
    *
    * @see ResourceType#getLinks()
    */
   public Set<Link> getLinks() {
      return links == null ? ImmutableSet.<Link>of() : Collections.unmodifiableSet(links);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), id, version, description, osType, href, type, links);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      
      OperatingSystemSection that = (OperatingSystemSection) obj;
      return super.equals(that) &&
            equal(this.id, that.id) && equal(this.version, that.version) && equal(this.description, that.description) &&
            equal(this.osType, that.osType) && equal(this.href, that.href) && equal(this.links, that.links) && equal(this.type, that.type);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string().add("id", id).add("version", version).add("description", description).add("osType", osType)
            .add("href", href).add("links", links).add("type", type);
   }
}