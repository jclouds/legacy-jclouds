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

import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents the results from a vCloud query as records.
 *
 * <pre>
 * &lt;complexType name="QueryResultRecords" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "QueryResultRecords")
public class QueryResultRecords<R extends QueryResultRecordType> extends ContainerType {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.QUERY_RESULT_RECORDS;

   public static <R extends QueryResultRecordType> Builder<R, ?> builder() {
      return new ConcreteBuilder<R>();
   }

   @Override
   public Builder<R, ?> toBuilder() {
      return QueryResultRecords.<R>builder().fromQueryResultRecords(this);
   }

   private static class ConcreteBuilder<R extends QueryResultRecordType> extends Builder<R, ConcreteBuilder<R>> {
   }
   
   public static class Builder<R extends QueryResultRecordType, B extends Builder<R, B>> extends ContainerType.Builder<B> {

      private Set<R> records = Sets.newLinkedHashSet();

      /**
       * @see QueryResultRecords#getRecords()
       */
      public B records(Set<? extends R> records) {
         this.records = Sets.newLinkedHashSet(checkNotNull(records, "records"));
         return self();
      }

      /**
       * @see QueryResultRecords#getRecords()
       */
      public B record(R record) {
         this.records.add(record);
         return self();
      }

      @Override
      public QueryResultRecords<R> build() {
         return new QueryResultRecords<R>(this);
      }

      public B fromQueryResultRecords(QueryResultRecords<R> in) {
         return fromContainerType(in).records(in.getRecords());
      }
   }

   protected QueryResultRecords(Builder<R,?> builder) {
      super(builder);
      this.records = ImmutableSet.copyOf(builder.records);
   }

   protected QueryResultRecords() {
      // For JAXB
   }

   @XmlElementRef
   private Set<R> records = Sets.newLinkedHashSet();

   /**
    * Set of records representing query results.
    */
   public Set<R> getRecords() {
      return Collections.unmodifiableSet(records);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultRecords<?> that = QueryResultRecords.class.cast(o);
      return super.equals(that) && equal(this.records, that.records);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), records);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("records", records);
   }
}
