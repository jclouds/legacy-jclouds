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
package org.jclouds.vcloud.director.v1_5.domain.section;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.dmtf.DMTFConstants.OVF_NS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_VMW_NS;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Link;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * An OperatingSystemSection specifies the operating system installed on a virtual machine.
 *
 * @author Adrian Cole
 * @author Adam Lowe
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "OperatingSystemSection", namespace = OVF_NS)
@XmlType(name = "OperatingSystemSection_Type")
@XmlSeeAlso({ org.jclouds.dmtf.ovf.OperatingSystemSection.class })
public class OperatingSystemSection extends org.jclouds.dmtf.ovf.OperatingSystemSection {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromOperatingSystemSection(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends org.jclouds.dmtf.ovf.OperatingSystemSection.Builder<B> {

      private String osType;
      private URI href;
      private String type;
      private Set<Link> links;

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
       * @see OperatingSystemSection#getLinks()
       */
      public B link(Link link) {
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
         return super.fromOperatingSystemSection(in)
               .osType(in.getOsType())
               .href(in.getHref())
               .type(in.getType())
               .links(in.getLinks());
      }
   }

   @XmlAttribute(namespace = VCLOUD_VMW_NS)
   protected String osType;
   @XmlAttribute(namespace = VCLOUD_1_5_NS)
   private URI href;
   @XmlAttribute(namespace = VCLOUD_1_5_NS)
   private String type;
   @XmlElement(name = "Link", namespace = VCLOUD_1_5_NS)
   private Set<Link> links = Sets.newLinkedHashSet();

   public OperatingSystemSection(Builder<?> builder) {
      super(builder);
      this.osType = builder.osType;
      this.href = builder.href;
      this.type = builder.type;
      this.links = builder.links != null && builder.links.isEmpty() ? null : builder.links;
   }

   protected OperatingSystemSection() {
      // For Builders and JAXB
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
      return links != null ? ImmutableSet.copyOf(links) : Sets.<Link>newLinkedHashSet();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), osType, href, type, links);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
 
      OperatingSystemSection that = (OperatingSystemSection) obj;
      return super.equals(that)
            && equal(this.osType, that.osType)
            && equal(this.href, that.href)
            && equal(this.links, that.links)
            && equal(this.type, that.type);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("osType", osType)
            .add("type", type)
            .add("href", href)
            .add("links", links);
   }
}
