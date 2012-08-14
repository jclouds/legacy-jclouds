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
package org.jclouds.elb.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_PolicyTypeDescription.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class PolicyType {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromPolicyType(this);
   }

   public static class Builder {

      protected String name;
      protected String description;
      protected ImmutableSet.Builder<AttributeMetadata<?>> attributeMetadata = ImmutableSet.<AttributeMetadata<?>> builder();

      /**
       * @see PolicyType#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see PolicyType#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see PolicyType#getAttributeMetadata()
       */
      public Builder attributeMetadata(Iterable<AttributeMetadata<?>> attributeMetadata) {
         this.attributeMetadata.addAll(checkNotNull(attributeMetadata, "attributeMetadata"));
         return this;
      }

      /**
       * @see PolicyType#getAttributeMetadata()
       */
      public Builder attributeMetadata(AttributeMetadata<?> attributeMetadata) {
         this.attributeMetadata.add(checkNotNull(attributeMetadata, "attributeMetadata"));
         return this;
      }

      public PolicyType build() {
         return new PolicyType(name, description, attributeMetadata.build());
      }

      public Builder fromPolicyType(PolicyType in) {
         return this.name(in.getName()).description(in.getDescription()).attributeMetadata(in.getAttributeMetadata());
      }
   }

   protected final String name;
   protected final String description;
   protected final Set<AttributeMetadata<?>> attributeMetadata;

   protected PolicyType(String name, String description, Iterable<AttributeMetadata<?>> attributeMetadata) {
      this.name = checkNotNull(name, "name");
      this.description = checkNotNull(description, "description");
      this.attributeMetadata = ImmutableSet.copyOf(checkNotNull(attributeMetadata, "attributeMetadata"));
   }

   /**
    * The name of the policy type.
    */
   public String getName() {
      return name;
   }

   /**
    * A human-readable description of the policy type.
    */
   public String getDescription() {
      return description;
   }

   /**
    * The description of the policy attributes associated with the LoadBalancer policies defined by
    * the Elastic Load Balancing service.
    */
   public Set<AttributeMetadata<?>> getAttributeMetadata() {
      return attributeMetadata;
   }
   
   /**
    * convenience method
    * @see #getAttributeMetadata()
    */
   public Map<String, AttributeMetadata<?>> getAttributeMetadataByName() {
      return Maps.uniqueIndex(attributeMetadata, new Function<AttributeMetadata<?>, String>(){

         @Override
         public String apply(AttributeMetadata<?> input) {
            return input.getName();
         }
         
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PolicyType other = (PolicyType) obj;
      return Objects.equal(this.name, other.name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("description", description)
               .add("attributeMetadata", attributeMetadata).toString();
   }

}
