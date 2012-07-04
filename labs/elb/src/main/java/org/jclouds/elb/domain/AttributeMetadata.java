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
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * This data type is used to describe values that are acceptable for the policy attribute.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_PolicyAttributeTypeDescription.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class AttributeMetadata<V> {
   /**
    * The cardinality of the attribute.
    */
   public static enum Cardinality {
      /**
       * ONE(1) : Single value required
       */
      ONE,
      /**
       * ZERO_OR_ONE(0..1) : Up to one value can be supplied
       */
      ZERO_OR_ONE,
      /**
       * ZERO_OR_MORE(0..*) : Optional. Multiple values are allowed
       */
      ZERO_OR_MORE,
      /**
       * ONE_OR_MORE(1..*0) : Required. Multiple values are allowed *
       */
      ONE_OR_MORE;

   }

   public static <V> Builder<V> builder() {
      return new Builder<V>();
   }

   public Builder<V> toBuilder() {
      return new Builder<V>().fromAttributeMetadata(this);
   }

   public static class Builder<V> {

      protected String name;
      protected Class<V> type;
      protected String rawType;
      protected Cardinality cardinality;
      protected Optional<V> defaultValue = Optional.absent();
      protected Optional<String> description = Optional.absent();

      /**
       * @see AttributeMetadata#getName()
       */
      public Builder<V> name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see AttributeMetadata#getType()
       */
      public Builder<V> type(Class<V> type) {
         this.type = type;
         return this;
      }

      /**
       * @see AttributeMetadata#getRawType()
       */
      public Builder<V> rawType(String rawType) {
         this.rawType = rawType;
         return this;
      }

      /**
       * @see AttributeMetadata#getCardinality()
       */
      public Builder<V> cardinality(Cardinality cardinality) {
         this.cardinality = cardinality;
         return this;
      }

      /**
       * @see AttributeMetadata#getDefaultValue()
       */
      public Builder<V> defaultValue(V defaultValue) {
         this.defaultValue = Optional.fromNullable(defaultValue);
         return this;
      }

      /**
       * @see AttributeMetadata#getDescription()
       */
      public Builder<V> description(String description) {
         this.description = Optional.fromNullable(description);
         return this;
      }

      public AttributeMetadata<V> build() {
         return new AttributeMetadata<V>(name, type, rawType, cardinality, defaultValue, description);
      }

      public Builder<V> fromAttributeMetadata(AttributeMetadata<V> in) {
         return this.name(in.getName()).type(in.getType()).rawType(in.getRawType()).cardinality(in.getCardinality())
                  .defaultValue(in.getDefaultValue().orNull()).description(in.getDescription().orNull());
      }
   }

   protected final String name;
   protected final Class<V> type;
   protected final String rawType;
   protected final Cardinality cardinality;
   protected final Optional<V> defaultValue;
   protected final Optional<String> description;

   protected AttributeMetadata(String name, Class<V> type, String rawType, Cardinality cardinality,
            Optional<V> defaultValue, Optional<String> description) {
      this.name = checkNotNull(name, "name");
      this.type = checkNotNull(type, "type");
      this.rawType = checkNotNull(rawType, "rawType");
      this.cardinality = checkNotNull(cardinality, "cardinality");
      this.defaultValue = checkNotNull(defaultValue, "defaultValue");
      this.description = checkNotNull(description, "description");
   }

   /**
    * The name of the attribute associated with the policy type.
    */
   public String getName() {
      return name;
   }

   /**
    * The type of attribute. For example, Boolean, Long, String, etc.
    */
   public Class<V> getType() {
      return type;
   }

   /**
    * Literal type of the value, noting that if it doesn't correspond to a primitive or String,
    * {@link #getType() will return String.class}
    */
   public String getRawType() {
      return rawType;
   }

   /**
    * The cardinality of the attribute.
    */
   public Cardinality getCardinality() {
      return cardinality;
   }

   /**
    * The default value of the attribute, if applicable.
    */
   public Optional<V> getDefaultValue() {
      return defaultValue;
   }

   /**
    * A human-readable description of the attribute.
    */
   public Optional<String> getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, type, rawType, cardinality, defaultValue, description);
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
      AttributeMetadata<?> other = AttributeMetadata.class.cast(obj);
      return Objects.equal(this.name, other.name) && Objects.equal(this.type, other.type)
               && Objects.equal(this.rawType, other.rawType) && Objects.equal(this.cardinality, other.cardinality)
               && Objects.equal(this.defaultValue, other.defaultValue)
               && Objects.equal(this.description, other.description);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("type", type).add("rawType", rawType)
               .add("cardinality", cardinality).add("defaultValue", defaultValue.orNull())
               .add("description", description.orNull());
   }

}
