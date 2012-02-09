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

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.*;

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
 * 
 * <pre>
 * &lt;xs:complexType name="MetadataType"&gt;
 * </pre>
 *
 * @author danikov
 */
@XmlRootElement(namespace = VCLOUD_1_5_NS, name = "MetadataEntry")
public class MetadataEntry extends ResourceType<MetadataEntry> {
   
   public static final String MEDIA_TYPE = VCloudDirectorMediaType.METADATA_ENTRY;

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromMetadata(this);
   }

   public static class Builder extends ResourceType.Builder<MetadataEntry> {
      private String key;
      private String value;

      /**
       * @see MetadataEntry#getKey
       */
      public Builder key(String key) {
         this.key = key;
         return this;
      }

      /**
       * @see MetadataEntry#getValue
       */
      public Builder value(String value) {
         this.value = value;
         return this;
      }
      
      @Override
      public MetadataEntry build() {
         MetadataEntry metadataEntry = new MetadataEntry(href, key, value);
         metadataEntry.setType(type);
         metadataEntry.setLinks(links);
         return metadataEntry;
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

      public Builder fromMetadata(MetadataEntry in) {
         return key(in.getKey()).value(in.getValue());
      }

   }

   private MetadataEntry() {
      // For JAXB and builder use
   }

   private MetadataEntry(URI href, String key, String value) {
      super(href);
      this.key = checkNotNull(key, "key");
      this.value = checkNotNull(value, "value");
   }

   @XmlElement(namespace = VCLOUD_1_5_NS, name = "Key")
   private String key;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "Value")
   private String value;

   /**
    * @return key of the entry
    */
   public String getKey() {
      return key;
   }

   /**
    * @return value of the entry
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
      MetadataEntry that = MetadataEntry.class.cast(o);
      return super.equals(that) && equal(key, that.key);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(key);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("key", key).add("value", value);
   }
}
