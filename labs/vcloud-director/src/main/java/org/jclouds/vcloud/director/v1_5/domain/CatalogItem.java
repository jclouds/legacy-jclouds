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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Contains a reference to a VappTemplate or Media object and related metadata.
 * <p/>
 * <pre>
 * &lt;complexType name="CatalogItemType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "CatalogItem")
public class CatalogItem extends EntityType {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.CATALOG_ITEM;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromCatalogItem(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends EntityType.Builder<B> {

      private Reference entity;
      private Set<Property> properties = Sets.newLinkedHashSet();

      /**
       * @see CatalogItem#getEntity()
       */
      public B entity(Reference entity) {
         this.entity = entity;
         return self();
      }

      /**
       * @see CatalogItem#getProperties()
       */
      public B properties(Set<Property> properties) {
         this.properties = Sets.newLinkedHashSet(checkNotNull(properties, "properties"));
         return self();
      }

      /**
       * @see CatalogItem#getProperties()
       */
      public B property(Property property) {
         this.properties.add(checkNotNull(property, "property"));
         return self();
      }

      @Override
      public CatalogItem build() {
         return new CatalogItem(this);
      }

      public B fromCatalogItem(CatalogItem in) {
         return fromEntityType(in).entity(in.getEntity()).properties(in.getProperties());
      }
   }

   protected CatalogItem(Builder<?> builder) {
      super(builder);
      this.entity = builder.entity;
      this.properties = ImmutableSet.copyOf(builder.properties);
   }

   protected CatalogItem() {
      // for JAXB
   }

   @XmlElement(name = "Entity", required = true)
   private Reference entity;
   @XmlElement(name = "Property")
   private Set<Property> properties = Sets.newLinkedHashSet();

   /**
    * A reference to a VappTemplate or Media object.
    */
   public Reference getEntity() {
      return entity;
   }

   /**
    * User-specified key/value pair.
    * <p/>
    * This element has been superseded by the {@link Metadata} element, which is the preferred way to specify key/value pairs for objects.
    */
   public Set<Property> getProperties() {
      return Collections.unmodifiableSet(this.properties);
   }
}
