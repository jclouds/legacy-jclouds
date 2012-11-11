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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A metadata container for multiple Resource types.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/projects#resource"/>
 */
public class Metadata {

   /**
    * A metadata entry
    *
    * @see Metadata#getItems()
    */
   public static class MetadataItem {
      private String key;
      private String value;

      public MetadataItem(String key, String value) {
         this.key = key;
         this.value = value;
      }

      public String getKey() {
         return key;
      }

      public String getValue() {
         return value;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(key);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         MetadataItem that = MetadataItem.class.cast(obj);
         return equal(this.key, that.key);
      }

      protected Objects.ToStringHelper string() {
         return toStringHelper(this)
                 .add("key", key).add("value", value);
      }

      @Override
      public String toString() {
         return string().toString();
      }
   }

   public enum Kind {
      COMPUTE("compute#metadata");

      String metadataName;

      Kind(String metadataName) {
         this.metadataName = metadataName;
      }

      String metadataName() {
         return this.metadataName;
      }

      public String value() {
         return metadataName();
      }

      public static Kind fromValue(String v) {
         try {
            return valueOf(v);
         } catch (IllegalArgumentException e) {
            return COMPUTE;
         }
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromMetadata(this);
   }

   public static class Builder {
      private Kind kind = Kind.COMPUTE;
      private ImmutableSet.Builder<MetadataItem> items = ImmutableSet.builder();

      /**
       * @see Metadata#getKind()
       */
      public Builder kind(Kind kind) {
         this.kind = checkNotNull(kind);
         return this;
      }

      /**
       * @see Metadata#getItems()
       */
      public Builder items(Set<MetadataItem> items) {
         this.items.addAll(checkNotNull(items));
         return this;
      }

      /**
       * @see Metadata#getItems()
       */
      public Builder addItem(String key, String value) {
         this.items.add(new MetadataItem(checkNotNull(key), checkNotNull(value, "value of %s", key)));
         return this;
      }

      /**
       * Costly operation required when deleting a metadata entry. Rebuilds the MetadataItems and deletes the item by
       * creating a temporary mutable set.
       */
      public Builder removeItem(String key) {
         Set<MetadataItem> newItems = Sets.newHashSet(items.build());
         newItems.remove(new MetadataItem(key, null));
         ImmutableSet.Builder<MetadataItem> items = ImmutableSet.builder();
         items.addAll(newItems);
         this.items = items;
         return this;
      }

      public Metadata build() {
         return new Metadata(kind, items.build());
      }

      public Builder fromMetadata(Metadata metadata) {
         checkNotNull(metadata);
         return new Builder().kind(metadata.getKind()).items(metadata.getItems());
      }
   }

   private final Kind kind;
   private final Set<MetadataItem> items;

   @ConstructorProperties({
           "kind", "items"
   })
   private Metadata(Kind kind, Set<MetadataItem> items) {
      this.kind = checkNotNull(kind);
      this.items = items == null ? ImmutableSet.<MetadataItem>of() : items;
   }

   /**
    * @return the Kind of the Metadata, e.g. COMPUTE
    */
   public Kind getKind() {
      return kind;
   }

   /**
    * Set of MetadataEntries. The total size of all keys and values must be less than 512 KB.
    * <p/>
    * Key for the metadata entry: Keys must conform to the following regexp: [a-zA-Z0-9-_]+,
    * and be less than 128 bytes in length. This is reflected as part of a URL in the metadata server. Additionally,
    * to avoid ambiguity, keys must not conflict with any other metadata keys for the project.
    * <p/>
    * Value for the metadata entry: These are free-form strings, and only have meaning as interpreted by the image
    * running in the instance. The only restriction placed on values is that their size must be less than or equal to
    * 32768 bytes.
    */
   public Set<MetadataItem> getItems() {
      return items;
   }

   /**
    * @return a map view of metadata
    * @see Metadata#getItems()
    */
   public Map<String, String> asItemsMap() {
      ImmutableMap.Builder<String, String> itemsAsMap = ImmutableMap.builder();
      for (MetadataItem item : items) {
         itemsAsMap.put(item.getKey(), item.getValue());
      }
      return itemsAsMap.build();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, items);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Metadata that = Metadata.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.items, that.items);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("kind", kind).add("items", items);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
