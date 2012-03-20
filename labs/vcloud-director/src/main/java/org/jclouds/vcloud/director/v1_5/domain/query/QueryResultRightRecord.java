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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents the results from a Right vCloud query as a record.
 * 
 * @author Aled Sage
 */
public class QueryResultRightRecord extends QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryResultRightRecord(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public static class Builder<B extends Builder<B>> extends QueryResultRecordType.Builder<B> {

      private String name;
      private String category;

      /**
       * @see QueryResultRightRecord#getName()
       */
      public B name(String val) {
         this.name = val;
         return self();
      }

      /**
       * @see QueryResultRightRecord#getCategory()
       */
      public B category(String val) {
         this.category = val;
         return self();
      }

      @Override
      public QueryResultRightRecord build() {
         return new QueryResultRightRecord(this);
      }

      public B fromQueryResultRightRecord(QueryResultRightRecord in) {
         return fromQueryResultRecordType(in)
                  .name(in.getName())
                  .category(in.getCategory());
      }

   }

   @XmlAttribute
   private String name;
   @XmlAttribute
   private String category;

   protected QueryResultRightRecord(Builder<?> builder) {
      super(builder);
      this.name = builder.name;
      this.category = builder.category;
   }

   protected QueryResultRightRecord() {
      // for JAXB
   }

   /**
    * name
    */
   public String getName() {
      return name;
   }

   /**
    * Category
    */
   public String getCategory() {
      return category;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultRightRecord that = QueryResultRightRecord.class.cast(o);

      return super.equals(that) && equal(name, that.name) && equal(category, that.category);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name, category);
   }
   @Override
   public ToStringHelper string() {
      return super.string().add("name", name).add("category", category);
   }
}
