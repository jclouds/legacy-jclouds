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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a set of metadata
 * 
 * <pre>
 * &lt;xs:complexType name="Metadata"&gt;
 * </pre>
 *
 * @author danikov
 */
@XmlRootElement(namespace = VCLOUD_1_5_NS, name = "Metadata")
public class Metadata extends ResourceType<Metadata> {
   
   public static final String MEDIA_TYPE = VCloudDirectorMediaType.METADATA;

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromMetadata(this);
   }

   public static class Builder extends ResourceType.Builder<Metadata> {

      private Set<MetadataEntry> metadataEntries = Sets.newLinkedHashSet();

      /**
       * @see Metadata#getMetadata()
       */
      public Builder metadata(Set<MetadataEntry> metadataEntries) {
         this.metadataEntries = Sets.newLinkedHashSet(checkNotNull(metadataEntries, "metadataEntries"));
         return this;
      }

      /**
       * @see Metadata#getMetadata()
       */
      public Builder entry(MetadataEntry metadataEntry) {
         metadataEntries.add(checkNotNull(metadataEntry, "metadataEntry"));
         return this;
      }

      @Override
      public Metadata build() {
         Metadata metadata = new Metadata(href, metadataEntries);
         metadata.setType(type);
         metadata.setLinks(links);
         return metadata;
      }
      
      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }

      public Builder fromMetadata(Metadata in) {
         return fromResourceType(in).metadata(in.getMetadataEntries());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResourceType(ResourceType<Metadata> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }
   }

   private Metadata() {
      // For JAXB and builder use
   }

   private Metadata(URI href, Set<MetadataEntry> metadataEntries) {
      super(href);
      this.metadataEntries = ImmutableSet.copyOf(metadataEntries);
   }

   @XmlElement(namespace = VCLOUD_1_5_NS, name = "MetadataEntry")
   private Set<MetadataEntry> metadataEntries = Sets.newLinkedHashSet();

   public Set<MetadataEntry> getMetadataEntries() {
      return ImmutableSet.copyOf(metadataEntries);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Metadata that = Metadata.class.cast(o);
      return super.equals(that) && equal(this.metadataEntries, that.metadataEntries);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(metadataEntries);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("metadataEntries", metadataEntries);
   }

}
