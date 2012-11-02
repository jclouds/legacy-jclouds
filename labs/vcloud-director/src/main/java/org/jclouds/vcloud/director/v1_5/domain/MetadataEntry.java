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
 * @author danikov
 */
//TODO: this is a ridiculously complicated way of representing Map<String, String>
@XmlRootElement(name = "MetadataEntry")
public class MetadataEntry extends Resource {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.METADATA_ENTRY;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromMetadataEntry(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {
      private String key;
      private String value;

      /**
       * @see MetadataEntry#getKey()
       */
      public B key(String key) {
         this.key = key;
         return self();
      }

      /**
       * @see MetadataEntry#getValue()
       */
      public B value(String value) {
         this.value = value;
         return self();
      }

      /**
       * @see MetadataEntry#getKey()
       * @see MetadataEntry#getValue()
       */
      public B entry(String key, String value) {
         this.key = key;
         this.value = value;
         return self();
      }

      @Override
      public MetadataEntry build() {
         return new MetadataEntry(this);
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public B href(URI href) {
         super.href(href);
         return self();
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public B type(String type) {
         super.type(type);
         return self();
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public B links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return self();
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public B link(Link link) {
         super.link(link);
         return self();
      }

      public B fromMetadataEntry(MetadataEntry in) {
         return fromResource(in).entry(key, value);
      }

   }

   MetadataEntry() {
      // for JAXB
   }

   public MetadataEntry(Builder<?> builder) {
      super(builder);
      this.key = checkNotNull(builder.key, "key");
      this.value = checkNotNull(builder.value, "value");
   }

   @XmlElement(name = "Key")
   private String key;
   @XmlElement(name = "Value")
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
      return super.equals(that) && equal(key, that.key) && equal(this.value, that.value);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), key, value);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("key", key).add("value", value);
   }
}
