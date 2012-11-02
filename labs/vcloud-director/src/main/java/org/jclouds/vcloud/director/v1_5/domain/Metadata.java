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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a set of metadata
 * <p/>
 * 
 * <pre>
 * &lt;xs:complexType name="Metadata"&gt;
 * </pre>
 * 
 * @author danikov
 */
@XmlRootElement(name = "Metadata")
public class Metadata extends Resource implements Map<String, String> {
   
   public static Metadata toMetadata(Map<String, String> input) {
      if (input instanceof Metadata)
         return Metadata.class.cast(input);
      Builder<?> builder = builder();
      for (Map.Entry<String, String> entry : input.entrySet()) {
         builder.entry(MetadataEntry.builder().entry(entry.getKey(), entry.getValue()).build());
      }
      return builder.build();
   }

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.METADATA;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromMetadata(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {

      private Set<MetadataEntry> metadataEntries = Sets.newLinkedHashSet();
      
      /**
       * @see Metadata#getMetadataEntries()
       */
      public B entries(Set<MetadataEntry> metadataEntries) {
         this.metadataEntries = Sets.newLinkedHashSet(checkNotNull(metadataEntries, "metadataEntries"));
         return self();
      }

      /**
       * @see Metadata#getMetadataEntries()
       */
      public B entry(MetadataEntry metadataEntry) {
         metadataEntries.add(checkNotNull(metadataEntry, "metadataEntry"));
         return self();
      }

      @Override
      public Metadata build() {
         return new Metadata(this);
      }

      public B fromMetadata(Metadata in) {
         return fromResource(in).entries(in.getMetadataEntries());
      }
   }

   protected Metadata() {
      // For JAXB
   }

   protected Metadata(Builder<?> builder) {
      super(builder);
      this.metadataEntries = ImmutableSet.copyOf(builder.metadataEntries);
   }

   @XmlElement(name = "MetadataEntry")
   private Set<MetadataEntry> metadataEntries = Sets.newLinkedHashSet();

   public Set<MetadataEntry> getMetadataEntries() {
      return ImmutableSet.copyOf(metadataEntries);
   }

   protected Map<String, String> toMap() {
      ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String> builder();
      for (MetadataEntry entry : metadataEntries) {
         builder.put(entry.getKey(), entry.getValue());
      }
      return builder.build();
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
      return Objects.hashCode(super.hashCode(), metadataEntries);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("metadataEntries", metadataEntries);
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsKey(Object arg0) {
      return toMap().containsKey(arg0);
   }

   @Override
   public boolean containsValue(Object arg0) {
      return toMap().containsValue(arg0);
   }

   @Override
   public Set<Map.Entry<String, String>> entrySet() {
      return toMap().entrySet();
   }

   @Override
   public String get(Object arg0) {
      return toMap().get(arg0);
   }

   @Override
   public boolean isEmpty() {
      return getMetadataEntries().size() == 0;
   }

   @Override
   public Set<String> keySet() {
      return toMap().keySet();
   }

   @Override
   public String put(String arg0, String arg1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends String, ? extends String> arg0) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String remove(Object arg0) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int size() {
      return getMetadataEntries().size();
   }

   @Override
   public Collection<String> values() {
      return toMap().values();
   }

}
