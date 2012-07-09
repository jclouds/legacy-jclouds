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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_DescribeLoadBalancerPolicies.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class Policy {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromPolicy(this);
   }

   public static class Builder {

      protected String name;
      protected String typeName;
      protected ImmutableMultimap.Builder<String, Object> attributes = ImmutableMultimap.<String, Object> builder();

      /**
       * @see Policy#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see Policy#getTypeName()
       */
      public Builder typeName(String typeName) {
         this.typeName = typeName;
         return this;
      }

      /**
       * @see Policy#getAttributes()
       */
      public Builder attributes(Multimap<String, Object> attributes) {
         this.attributes.putAll(checkNotNull(attributes, "attributes"));
         return this;
      }

      /**
       * @see Policy#getAttributes()
       */
      public Builder attribute(String key, Object value) {
         this.attributes.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Policy build() {
         return new Policy(name, typeName, attributes.build());
      }

      public Builder fromPolicy(Policy in) {
         return this.name(in.getName()).typeName(in.getTypeName()).attributes(in.getAttributes());
      }
   }
   
   protected final String name;
   protected final String typeName;
   protected final Multimap<String, Object> attributes;

   protected Policy(String name, String typeName, Multimap<String, Object> attributes) {
      this.name = checkNotNull(name, "name");
      this.typeName = checkNotNull(typeName, "typeName");
      this.attributes = ImmutableMultimap.copyOf(checkNotNull(attributes, "attributes"));
   }

   /**
    * The name of the policy associated with the LoadBalancer
    */
   public String getName() {
      return name;
   }

   /**
    * The name of the policy type associated with the LoadBalancer.
    */
   public String getTypeName() {
      return typeName;
   }

   /**
    * A list of policy attribute description structures. Note that values are either Long, Boolean,
    * or String, depending on {@link AttributeMetadata#getType()}
    */
   public Multimap<String, Object> getAttributes() {
      return attributes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, typeName);
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
      Policy other = (Policy) obj;
      return Objects.equal(this.name, other.name) && Objects.equal(this.typeName, other.typeName);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("typeName", typeName)
               .add("attributes", attributes).toString();
   }

}
