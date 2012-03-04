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
import javax.xml.bind.annotation.XmlAttribute;

import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.ResourceType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * Container for query result sets.
 * <p/>
 * <pre>
 * &lt;complexType name="Container" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
public class ContainerType<T extends ContainerType<T>> extends ResourceType<T> {

   public static <T extends ContainerType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   @Override
   public Builder<T> toBuilder() {
      return new Builder<T>().fromContainerType(this);
   }

   public static class Builder<T extends ContainerType<T>> extends ResourceType.Builder<T> {

      protected String name;
      protected Integer page;
      protected Integer pageSize;
      protected Long total;

      /**
       * @see ContainerType#getName()
       */
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see ContainerType#getPage()
       */
      public Builder<T> page(Integer page) {
         this.page = page;
         return this;
      }

      /**
       * @see ContainerType#getPageSize()
       */
      public Builder<T> pageSize(Integer pageSize) {
         this.pageSize = pageSize;
         return this;
      }

      /**
       * @see ContainerType#getTotal()
       */
      public Builder<T> total(Long total) {
         this.total = total;
         return this;
      }

      @Override
      public ContainerType<T> build() {
         return new ContainerType<T>(href, type, links, name, page, pageSize, total);
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
      public Builder<T> fromResourceType(ResourceType<T> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }

      public Builder<T> fromContainerType(ContainerType<T> in) {
         return fromResourceType(in).name(in.getName()).page(in.getPage()).pageSize(in.getPageSize()).total(in.getTotal());
      }
   }

   @XmlAttribute
   protected String name;
   @XmlAttribute
   protected Integer page;
   @XmlAttribute
   protected Integer pageSize;
   @XmlAttribute
   protected Long total;

   public ContainerType(URI href, String type, Set<Link> links, String name, Integer page, Integer pageSize, Long total) {
      super(href, type, links);
      this.name = name;
      this.page = page;
      this.pageSize = pageSize;
      this.total = total;
   }

   protected ContainerType() {
      // For JAXB and builder use
   }

   /**
    * Query name that generated this result set.
    */
   public String getName() {
      return name;
   }

   /**
    * Page of the result set that this container holds. The first page is page number 1.
    */
   public Integer getPage() {
      return page;
   }

   /**
    * Page size, as a number of records or references.
    */
   public Integer getPageSize() {
      return pageSize;
   }

   /**
    * Total number of records or references in the container.
    */
   public Long getTotal() {
      return total;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ContainerType<T> that = ContainerType.class.cast(o);
      return super.equals(that) &&
            equal(this.name, that.name) && equal(this.page, that.page) &&
            equal(this.pageSize, that.pageSize) && equal(this.total, that.total);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(name, page, pageSize, total);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("name", name).add("page", page).add("pageSize", pageSize).add("total", total);
   }

}
