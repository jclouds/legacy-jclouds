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

import static com.google.common.base.Preconditions.*;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * 
 * Contains a reference to a VappTemplate or Media object and related metadata.
 *
 * <pre>
 * &lt;complexType name="CatalogItemType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(namespace = VCLOUD_1_5_NS, name = "CatalogItem")
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
      private List<Property> properties = Lists.newArrayList();

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
      public Builder properties(List<Property> properties) {
         this.properties = Lists.newArrayList(checkNotNull(properties, "properties"));
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
         CatalogItem catalog = new CatalogItem(href, name, entity);
         catalog.setProperties(properties);
         catalog.setDescription(description);
         catalog.setId(id);
         catalog.setType(type);
         catalog.setLinks(links);
         catalog.setTasksInProgress(tasksInProgress);
         return catalog;
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
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
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

   private CatalogItem() {
      // For JAXB and builder use
   }

   private CatalogItem(URI href, String name, Reference entity) {
      super(href, name);
      this.entity = entity;
      this.setProperties(properties);
   }

   @XmlElement(name = "Entity", required = true)
   private Reference entity;
   @XmlElement(name = "Property")
   private List<Property> properties = Lists.newArrayList();

   /**
    * A reference to a VappTemplate or Media object.
    */
   public Reference getEntity() {
      return entity;
   }

   /**
    * User-specified key/value pair.
    *
    * This element has been superseded by the {@link Metadata} element, which is the preferred way to specify key/value pairs for objects.
    */
   public List<Property> getProperties() {
      return this.properties;
   }

   public void setProperties(List<Property> properties) {
      this.properties = Lists.newArrayList(checkNotNull(properties, "properties"));
   }

   public void addProperty(Property property) {
      this.properties.add(checkNotNull(property, "property"));
   }
}
