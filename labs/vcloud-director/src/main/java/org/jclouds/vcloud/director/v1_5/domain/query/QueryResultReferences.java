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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlElementRef;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
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

      protected Set<T> references = Sets.newLinkedHashSet();

      /**
       * @see QueryResultReferences#getReferences()
       */
      public Builder<T> references(Set<T> references) {
         this.references = checkNotNull(references, "references");
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
         return new QueryResultReferences<T>(href, type, links, name, page, pageSize, total, references);
      }

      /**
       * @see ContainerType#getName()
       */
      @Override
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see ContainerType#getPage()
       */
      @Override
      public Builder<T> page(Integer page) {
         this.page = page;
         return this;
      }

      /**
       * @see ContainerType#getPageSize()
       */
      @Override
      public Builder<T> pageSize(Integer pageSize) {
         this.pageSize = pageSize;
         return this;
      }

      /**
       * @see ContainerType#getTotal()
       */
      @Override
      public Builder<T> total(Long total) {
         this.total = total;
         return this;
      }

      /**
       * @see ContainerType#getHref()
       */
      @Override
      public Builder<T> href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ContainerType#getType()
       */
      @Override
      public Builder<T> type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ContainerType#getLinks()
       */
      @Override
      public Builder<T> links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return this;
      }

      /**
       * @see ContainerType#getLinks()
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

   protected QueryResultReferences(URI href, String type, Set<Link> links, String name, Integer page, Integer pageSize, Long total, Set<T> references) {
      super(href, type, links, name, page, pageSize, total);
      this.references = ImmutableSet.copyOf(references);
   }

   protected QueryResultReferences() {
      // for JAXB
   }

   // NOTE add other types as they are used. probably not the best way to do this.
   @XmlElementRef
   private Set<T> references = Sets.newLinkedHashSet();

   /**
    * Set of references representing query results.
    */
   public Set<T> getReferences() {
      return references;
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
      return Objects.hashCode(super.hashCode(), references);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("references", references);
   }

}
