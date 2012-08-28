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
package org.jclouds.vcloud.director.v1_5.domain.query;

import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects.ToStringHelper;

/**
 * Container for the list of typed queries available to the
 * requesting user.
 *
 * <pre>
 * &lt;complexType name="QueryList" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlType(name = "QueryList")
public class QueryList extends ContainerType {

   // FIXME Should this class have fields?!
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryList(this);
   }

   public static class Builder<B extends Builder<B>> extends ContainerType.Builder<B> {

      @Override
      public QueryList build() {
         return new QueryList(this);
      }

      public Builder<?> fromQueryList(QueryList in) {
         return fromContainerType(in);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   QueryList() {
      // for JAXB
   }

   protected QueryList(Builder<?> builder) {
      super(builder);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryList that = QueryList.class.cast(o);
      return super.equals(that);
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public ToStringHelper string() {
      return super.string();
   }

}
