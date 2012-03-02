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

import java.net.URI;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * Represents a metadata entry
 * <p/>
 * <pre>
 * &lt;xs:complexType name="MetadataType"&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "MetadataValue")
public class MetadataValue extends ResourceType<MetadataValue> {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.METADATA_ENTRY;

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromMetadataValue(this);
   }

   public static class Builder extends ResourceType.Builder<MetadataValue> {
      private String value;

      /**
       * @see MetadataValue#getValue
       */
      public Builder value(String value) {
         this.value = value;
         return this;
      }

      @Override
      public MetadataValue build() {
         return new MetadataValue(href, type, links, value);
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

      public Builder fromMetadataValue(MetadataValue in) {
         return fromResourceType(in).value(value);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResourceType(ResourceType<MetadataValue> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }
   }

   private MetadataValue() {
      // For JAXB
   }

   private MetadataValue(URI href, String type, Set<Link> links, String value) {
      super(href, type, links);
      this.value = checkNotNull(value, "value");
   }

   @XmlElement(name = "Value", required = true)
   private String value;

   /**
    * The value.
    */
   public String getValue() {
      return value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      MetadataValue that = MetadataValue.class.cast(o);
      return super.equals(that) && equal(this.value, that.value);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(value);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("value", value);
   }
}
