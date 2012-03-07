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

import static com.google.common.base.Objects.equal;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Container for references to VappTemplate and Media objects.
 * <p/>
 * <pre>
 * &lt;complexType name="CatalogType" /&gt;
 * </pre>
 *
 * @author danikov
 */
@XmlRootElement(name = "Catalog")
public class CatalogType<T extends CatalogType<T>> extends EntityType<T> {

   public static <T extends CatalogType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   @Override
   public Builder<T> toBuilder() {
      return new Builder<T>().fromCatalogType(this);
   }

   public static class Builder<T extends CatalogType<T>> extends EntityType.Builder<T> {

      protected Owner owner;
      protected CatalogItems catalogItems;
      protected Boolean isPublished;

      /**
       * @see CatalogType#getOwner()
       */
      public Builder<T> owner(Owner owner) {
         this.owner = owner;
         return this;
      }

      /**
       * @see CatalogType#getCatalogItems()
       */
      public Builder<T> catalogItems(CatalogItems catalogItems) {
         this.catalogItems = catalogItems;
         return this;
      }

      /**
       * @see CatalogType#isPublished()
       */
      public Builder<T> isPublished(Boolean isPublished) {
         this.isPublished = isPublished;
         return this;
      }

      /**
       * @see CatalogType#isPublished()
       */
      public Builder<T> published() {
         this.isPublished = Boolean.TRUE;
         return this;
      }

      @Override
      public CatalogType<T> build() {
         return new CatalogType<T>(href, type, links, description, tasksInProgress, id, name, owner, catalogItems, isPublished);
      }

      /**
       * @see EntityType#getName()
       */
      @Override
      public Builder<T> name(String name) {
         super.name(name);
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      @Override
      public Builder<T> description(String description) {
         super.description(description);
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder<T> id(String id) {
         super.id(id);
         return this;
      }

      /**
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder<T> tasksInProgress(TasksInProgress tasksInProgress) {
         super.tasksInProgress(tasksInProgress);
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder<T> href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder<T> type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder<T> links(Set<Link> links) {
         super.links(links);
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder<T> link(Link link) {
         super.link(link);
         return this;
      }

      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> fromEntityType(EntityType<T> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder<T> fromCatalogType(CatalogType<T> in) {
         return fromEntityType(in).owner(in.getOwner()).catalogItems(in.getCatalogItems()).isPublished(in.isPublished());
      }
   }

   public CatalogType(URI href, String type, Set<Link> links, String description, TasksInProgress tasksInProgress, String id,
                  String name, Owner owner, CatalogItems catalogItems, Boolean published) {
      super(href, type, links, description, tasksInProgress, id, name);
      this.owner = owner;
      this.catalogItems = catalogItems;
      this.isPublished = published;
   }

   protected CatalogType() {
      // For JAXB
   }

   @XmlElement(name = "Owner")
   private Owner owner;
   @XmlElement(name = "CatalogItems")
   private CatalogItems catalogItems;
   @XmlElement(name = "IsPublished")
   private Boolean isPublished;

   /**
    * Gets the value of the owner property.
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * Gets the value of the catalogItems property.
    */
   public CatalogItems getCatalogItems() {
      return catalogItems;
   }

   /**
    * Gets the value of the isPublished property.
    */
   public Boolean isPublished() {
      return isPublished;
   }
   
   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CatalogType<?> that = CatalogType.class.cast(o);
      return super.equals(that) &&
            equal(this.owner, that.owner) && 
            equal(this.catalogItems, that.catalogItems) &&
            equal(this.isPublished, that.isPublished);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), owner, catalogItems, catalogItems);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("owner", owner)
            .add("catalogItems", catalogItems)
            .add("isPublished", isPublished);
   }

}
