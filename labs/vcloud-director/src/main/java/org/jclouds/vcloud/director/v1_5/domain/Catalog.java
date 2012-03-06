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
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.collect.Sets;

/**
 * Container for references to VappTemplate and Media objects.
 * <p/>
 * <pre>
 * &lt;complexType name="CatalogType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "Catalog")
public class Catalog extends CatalogType<Catalog> {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.CATALOG;

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromCatalog(this);
   }

   public static class Builder extends CatalogType.Builder<Catalog> {

      @Override
      public Catalog build() {
         return new Catalog(href, type, links, description, tasksInProgress, id, name, owner, catalogItems, isPublished);
      }
      
      /**
       * @see Catalog#getOwner()
       */
      public Builder owner(Owner owner) {
         super.owner(owner);
         return this;
      }

      /**
       * @see Catalog#getCatalogItems()
       */
      public Builder catalogItems(CatalogItems catalogItems) {
         super.catalogItems(catalogItems);
         return this;
      }

      /**
       * @see Catalog#isPublished()
       */
      public Builder isPublished(Boolean isPublished) {
         super.isPublished(isPublished);
         return this;
      }

      /**
       * @see Catalog#isPublished()
       */
      public Builder published() {
         super.isPublished(isPublished);
         return this;
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
      public Builder fromCatalogType(CatalogType<Catalog> in) {
         return Builder.class.cast(super.fromCatalogType(in));
      }

      public Builder fromCatalog(Catalog in) {
         return fromCatalogType(in).owner(in.getOwner()).catalogItems(in.getCatalogItems()).isPublished(in.isPublished());
      }
   }

   public Catalog(URI href, String type, Set<Link> links, String description, TasksInProgress tasksInProgress, String id,
                  String name, Owner owner, CatalogItems catalogItems, Boolean published) {
      super(href, type, links, description, tasksInProgress, id, name, owner, catalogItems, published);
   }

   @SuppressWarnings("unused")
   private Catalog() {
      // for JAXB
   }
}
