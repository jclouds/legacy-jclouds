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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.domain.CatalogReference;
import org.jclouds.vcloud.director.v1_5.domain.Link;

import com.google.common.collect.Sets;

/**
 * Represents the results from a vCloud query as references.
 * 
 * <pre>
 * &lt;complexType name="QueryResultReferences" /&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
@XmlRootElement(namespace = VCLOUD_1_5_NS, name = "CatalogReferences")
public class CatalogReferences extends QueryResultReferences<CatalogReference> {

   @SuppressWarnings("unchecked")
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
         CatalogReferences queryResultReferences = new CatalogReferences(href);
         queryResultReferences.setReferences(references);
         queryResultReferences.setName(name);
         queryResultReferences.setPage(page);
         queryResultReferences.setPageSize(pageSize);
         queryResultReferences.setTotal(total);
         queryResultReferences.setType(type);
         queryResultReferences.setLinks(links);
         return queryResultReferences;
      }

      /**
       * @see QueryResultReferences#getReferences()
       */
      @Override
      public Builder references(List<CatalogReference> references) {
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
       * @see Container#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see Container#getPage()
       */
      @Override
      public Builder page(Integer page) {
         this.page = page;
         return this;
      }

      /**
       * @see Container#getPageSize()
       */
      @Override
      public Builder pageSize(Integer pageSize) {
         this.pageSize = pageSize;
         return this;
      }

      /**
       * @see Container#getTotal()
       */
      @Override
      public Builder total(Long total) {
         this.total = total;
         return this;
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

      @Override
      public Builder fromQueryResultReferences(QueryResultReferences<CatalogReference> in) {
         return Builder.class.cast(super.fromQueryResultReferences(in));
      }

      public Builder fromCatalogReferences(CatalogReferences in) {
         return fromQueryResultReferences(in);
      }
   }

   protected CatalogReferences() {
      // For JAXB and builder use
   }

   protected CatalogReferences(URI href) {
      super(href);
   }
}
