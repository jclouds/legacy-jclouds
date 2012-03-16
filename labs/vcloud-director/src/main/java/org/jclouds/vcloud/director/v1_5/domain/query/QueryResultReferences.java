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

import java.util.Set;

import javax.xml.bind.annotation.XmlElementRef;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Reference;

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
public class QueryResultReferences extends ContainerType {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.QUERY_RESULT_REFERENCES;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryResultReferences(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends ContainerType.Builder<B> {

      private Set<Reference> references = Sets.newLinkedHashSet();

      /**
       * @see QueryResultReferences#getReferences()
       */
      public B references(Set<? extends Reference> references) {
         this.references = Sets.newLinkedHashSet(checkNotNull(references, "references"));
         return self();
      }

      /**
       * @see QueryResultReferences#getReferences()
       */
      public B reference(Reference reference) {
         this.references.add(reference);
         return self();
      }

      @Override
      public QueryResultReferences build() {
         return new QueryResultReferences(this);
      }

      public B fromQueryResultReferences(QueryResultReferences in) {
         return fromContainerType(in).references(in.getReferences());
      }
   }

   protected QueryResultReferences(Builder<?> builder) {
      super(builder);
      this.references = ImmutableSet.copyOf(builder.references);
   }

   protected QueryResultReferences() {
      // for JAXB
   }

   // NOTE add other types as they are used. probably not the best way to do this.
   @XmlElementRef
   private Set<Reference> references = Sets.newLinkedHashSet();

   /**
    * Set of references representing query results.
    */
   public Set<Reference> getReferences() {
      return references;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultReferences that = QueryResultReferences.class.cast(o);
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
