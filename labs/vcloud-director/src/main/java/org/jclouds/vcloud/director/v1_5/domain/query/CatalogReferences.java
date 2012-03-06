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

package org.jclouds.vcloud.director.v1_5.domain.query;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.domain.CatalogReference;
import org.jclouds.vcloud.director.v1_5.domain.Link;

import com.google.common.collect.Sets;

/**
 * Represents the results from a vCloud query as references.
 * <p/>
 * <pre>
 * &lt;complexType name="QueryResultReferences" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "CatalogReferences")
public class CatalogReferences extends QueryResultReferences<CatalogReference> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromQueryResultReferences(this);
   }

   public static class Builder extends QueryResultReferences.Builder<CatalogReference> {

      @Override
      public CatalogReferences build() {
         return new CatalogReferences(href, type, links, name, page, pageSize, total, references);
      }

      /**
       * @see QueryResultReferences#getReferences()
       */
      @Override
      public Builder references(Set<CatalogReference> references) {
         this.references = references;
         return this;
      }

      /**
       * @see QueryResultReferences#getReferences()
       */
      @Override
      public Builder reference(CatalogReference reference) {
         this.references.add(reference);
         return this;
      }

      /**
       * @see CatalogReferences#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see CatalogReferences#getPage()
       */
      @Override
      public Builder page(Integer page) {
         this.page = page;
         return this;
      }

      /**
       * @see CatalogReferences#getPageSize()
       */
      @Override
      public Builder pageSize(Integer pageSize) {
         this.pageSize = pageSize;
         return this;
      }

      /**
       * @see CatalogReferences#getTotal()
       */
      @Override
      public Builder total(Long total) {
         this.total = total;
         return this;
      }

      /**
       * @see CatalogReferences#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see CatalogReference#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see CatalogReferences#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return this;
      }

      /**
       * @see CatalogReferences#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }

      @Override
      public Builder fromQueryResultReferences(QueryResultReferences<CatalogReference> in) {
         return Builder.class.cast(super.fromQueryResultReferences(in));
      }

      public Builder fromCatalogReferences(CatalogReferences in) {
         return fromQueryResultReferences(in);
      }
   }

   public CatalogReferences(URI href, String type, Set<Link> links, String name, Integer page, Integer pageSize, Long total, Set<CatalogReference> references) {
      super(href, type, links, name, page, pageSize, total, references);
   }

   protected CatalogReferences() {
      // for JAXB
   }
}
