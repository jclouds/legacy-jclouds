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

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Link;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The virtual hardware required by a virtual machine is specified in VirtualHardwareSection.
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "VirtualHardwareSection", namespace = OVF_NS)
@XmlType(name = "VirtualHardwareSection_Type")
@XmlSeeAlso({ org.jclouds.dmtf.ovf.VirtualHardwareSection.class })
public class VirtualHardwareSection extends org.jclouds.dmtf.ovf.VirtualHardwareSection {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromVirtualHardwareSection(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends org.jclouds.dmtf.ovf.VirtualHardwareSection.Builder<B> {

      private URI href;
      private String type;
      private Set<Link> links = Sets.newLinkedHashSet();

      @Override
      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * @see VirtualHardwareSection#getType()
       */
      public B type(String type) {
         this.type = type;
         return self();
      }

      /**
       * @see VirtualHardwareSection#getHref()
       */
      public B href(URI href) {
         this.href = href;
         return self();
      }

      /**
       * @see VirtualHardwareSection#getLinks()
       */
      public B links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return self();
      }

      /**
       * @see VirtualHardwareSection#getLinks()
       */
      public B link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return self();
      }

      @Override
      public VirtualHardwareSection build() {
         return new VirtualHardwareSection(this);
      }

      public B fromVirtualHardwareSection(VirtualHardwareSection in) {
         return super.fromVirtualHardwareSection(in)
               .type(in.getType())
               .href(in.getHref())
               .links(in.getLinks());
      }
   }

   @XmlAttribute(namespace = VCLOUD_1_5_NS)
   protected String type;
   @XmlAttribute(namespace = VCLOUD_1_5_NS)
   @XmlSchemaType(name = "anyURI")
   protected URI href;
   @XmlElement(name = "Link", namespace = VCLOUD_1_5_NS)
   protected Set<Link> links = Sets.newLinkedHashSet();


   protected VirtualHardwareSection(Builder<?> builder) {
      super(builder);
      this.type = builder.type;
      this.href = builder.href;
      this.links = builder.links != null && builder.links.isEmpty() ? null : builder.links;
   }

   protected VirtualHardwareSection() {
      // for JAXB
   }

   /**
    * Contains the URI to the entity.
    *
    * @see ResourceType#getHref()
    */
   public String getType() {
      return type;
   }

   /**
    * Contains the type of the the entity.
    *
    * @see ResourceType#getType()
    */
   public URI getHref() {
      return href;
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
   public ToStringHelper string() {
      return super.string()
            .add("type", type)
            .add("href", href)
            .add("links", links);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), type, href, links);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;

      VirtualHardwareSection that = VirtualHardwareSection.class.cast(obj);
      return super.equals(that)
            && equal(this.type, that.type)
            && equal(this.href, that.href)
            && equal(this.links, that.links);
   }
}
