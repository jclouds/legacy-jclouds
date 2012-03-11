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

import java.net.URI;
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
public class CatalogItem extends EntityType<CatalogItem> {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.CATALOG_ITEM;

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromCatalogItem(this);
   }

   public static class Builder extends EntityType.Builder<CatalogItem> {

      private Reference entity;
      private Set<Property> properties = Sets.newLinkedHashSet();

      /**
       * @see CatalogItem#getEntity()
       */
      public Builder entity(Reference entity) {
         this.entity = entity;
         return this;
      }

      /**
       * @see CatalogItem#getProperties()
       */
      public Builder properties(Set<Property> properties) {
         this.properties = Sets.newLinkedHashSet(checkNotNull(properties, "properties"));
         return this;
      }

      /**
       * @see CatalogItem#getProperties()
       */
      public Builder property(Property property) {
         this.properties.add(checkNotNull(property, "property"));
         return this;
      }

      @Override
      public CatalogItem build() {
         return new CatalogItem(href, type, links, description, tasksInProgress, id, name, entity, properties);
      }

      /**
       * @see EntityType#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      @Override
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder tasksInProgress(TasksInProgress tasksInProgress) {
         this.tasksInProgress = tasksInProgress;
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      @Override
      public Builder fromEntityType(EntityType<CatalogItem> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder fromCatalogItem(CatalogItem in) {
         return fromEntityType(in).entity(in.getEntity()).properties(in.getProperties());
      }
   }

   private CatalogItem(URI href, String type, Set<Link> links, String description, TasksInProgress tasksInProgress, String id, String name, Reference entity, Set<Property> properties) {
      super(href, type, links, description, tasksInProgress, id, name);
      this.entity = entity;
      this.properties = ImmutableSet.copyOf(properties);
   }

   private CatalogItem() {
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
