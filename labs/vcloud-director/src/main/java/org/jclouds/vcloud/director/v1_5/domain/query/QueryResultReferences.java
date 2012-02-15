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

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.CatalogReference;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Lists;
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
public class QueryResultReferences<T extends ReferenceType<T>> extends ContainerType<QueryResultReferences<T>> {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.QUERY_RESULT_REFERENCES;

   public static <T extends ReferenceType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   @Override
   public Builder<T> toBuilder() {
      return new Builder<T>().fromQueryResultReferences(this);
   }

   public static class Builder<T extends ReferenceType<T>> extends ContainerType.Builder<QueryResultReferences<T>> {

      protected List<T> references = Lists.newArrayList();

      /**
       * @see QueryResultReferences#getReferences()
       */
      public Builder<T> references(List<T> references) {
         this.references = references;
         return this;
      }

      /**
       * @see QueryResultReferences#getReferences()
       */
      public Builder<T> reference(T reference) {
         this.references.add(reference);
         return this;
      }

      @Override
      public QueryResultReferences<T> build() {
         QueryResultReferences<T> queryResultReferences = new QueryResultReferences<T>(href);
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
       * @see Container#getName()
       */
      @Override
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see Container#getPage()
       */
      @Override
      public Builder<T> page(Integer page) {
         this.page = page;
         return this;
      }

      /**
       * @see Container#getPageSize()
       */
      @Override
      public Builder<T> pageSize(Integer pageSize) {
         this.pageSize = pageSize;
         return this;
      }

      /**
       * @see Container#getTotal()
       */
      @Override
      public Builder<T> total(Long total) {
         this.total = total;
         return this;
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder<T> href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder<T> type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder<T> links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder<T> link(Link link) {
         super.link(link);
         return this;
      }

      @Override
      public Builder<T> fromContainerType(ContainerType<QueryResultReferences<T>> in) {
         return Builder.class.cast(super.fromContainerType(in));
      }

      public Builder<T> fromQueryResultReferences(QueryResultReferences<T> in) {
         return fromContainerType(in).references(in.getReferences());
      }
   }

   protected QueryResultReferences() {
      // For JAXB and builder use
   }

   protected QueryResultReferences(URI href) {
      super(href);
   }

   // NOTE add other types as they are used. probably not the best way to do this.
   @XmlElementRefs({
       @XmlElementRef(type = CatalogReference.class)
   })
   protected List<T> references;

   /**
    * Set of references representing query results.
    */
   public List<T> getReferences() {
      return references;
   }

   public void setReferences(List<T> references) {
      this.references = Lists.newArrayList(checkNotNull(references, "references"));
   }

   public void addReference(T reference) {
      this.references.add(checkNotNull(reference, "reference"));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultReferences<T> that = QueryResultReferences.class.cast(o);
      return super.equals(that) && equal(this.references, that.references);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(references);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("references", references);
   }

}
