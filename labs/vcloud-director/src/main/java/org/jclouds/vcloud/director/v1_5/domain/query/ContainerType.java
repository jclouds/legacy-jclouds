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

import javax.xml.bind.annotation.XmlAttribute;

import org.jclouds.vcloud.director.v1_5.domain.ResourceType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Container for query result sets.
 *
 * <pre>
 * &lt;complexType name="Container" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
public class ContainerType extends ResourceType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromContainerType(this);
   }

   public static class Builder<B extends Builder<B>> extends ResourceType.Builder<B> {

      private String name;
      private Integer page;
      private Integer pageSize;
      private Long total;

      /**
       * @see ContainerType#getName()
       */
      public B name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see ContainerType#getPage()
       */
      public B page(Integer page) {
         this.page = page;
         return self();
      }

      /**
       * @see ContainerType#getPageSize()
       */
      public B pageSize(Integer pageSize) {
         this.pageSize = pageSize;
         return self();
      }

      /**
       * @see ContainerType#getTotal()
       */
      public B total(Long total) {
         this.total = total;
         return self();
      }

      @Override
      public ContainerType build() {
         return new ContainerType(this);
      }

      public B fromContainerType(ContainerType in) {
         return fromResourceType(in).name(in.getName()).page(in.getPage()).pageSize(in.getPageSize()).total(in.getTotal());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   @XmlAttribute
   private String name;
   @XmlAttribute
   private Integer page;
   @XmlAttribute
   private Integer pageSize;
   @XmlAttribute
   private Long total;

   protected ContainerType(Builder<?> builder) {
      super(builder);
      this.name = builder.name;
      this.page = builder.page;
      this.pageSize = builder.pageSize;
      this.total = builder.total;
   }

   protected ContainerType() {
      // for JAXB
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
      ContainerType that = ContainerType.class.cast(o);
      return super.equals(that) &&
            equal(this.name, that.name) && equal(this.page, that.page) &&
            equal(this.pageSize, that.pageSize) && equal(this.total, that.total);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name, page, pageSize, total);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("name", name).add("page", page).add("pageSize", pageSize).add("total", total);
   }

}
