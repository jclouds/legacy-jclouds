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
package org.jclouds.dmtf.ovf;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 * @author Adam Lowe
 * @author grkvlt@apache.org
 */
@XmlType(name = "Property")
@XmlSeeAlso({ ProductSectionProperty.class })
public class Property {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromProperty(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public abstract static class Builder<B extends Builder<B>> {

      protected String key;
      protected Set<PropertyConfigurationValueType> values = Sets.newLinkedHashSet();
      protected MsgType label;
      protected MsgType description;
      protected String type;
      protected String qualifiers;
      protected Boolean userConfigurable;
      protected String defaultValue = "";

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * @see Property#getKey()
       */
      public B key(String key) {
         this.key = key;
         return self();
      }

      /**
       * @see Property#getValues()
       */
      public B values(Set<PropertyConfigurationValueType> values) {
         this.values = checkNotNull(values, "values");
         return self();
      }

      /**
       * @see Property#getValues()
       */
      public B value(PropertyConfigurationValueType value) {
         this.values.add(checkNotNull(value, "value"));
         return self();
      }

      /**
       * @see Property#getLabel()
       */
      public B label(MsgType label) {
         this.label = label;
         return self();
      }

      /**
       * @see Property#getDescription()
       */
      public B description(MsgType description) {
         this.description = description;
         return self();
      }

      /**
       * @see Property#getType()
       */
      public B type(String type) {
         this.type = type;
         return self();
      }

      /**
       * @see Property#getQualifiers()
       */
      public B qualifiers(String qualifiers) {
         this.qualifiers = qualifiers;
         return self();
      }

      /**
       * @see Property#getQualifiers()
       */
      public B qualifiers(Iterable<String> qualifiers) {
         this.qualifiers = Joiner.on(',').join(qualifiers);
         return self();
      }

      /**
       * @see Property#getQualifiers()
       */
      public B qualifiers(String...qualifiers) {
         this.qualifiers = Joiner.on(',').join(qualifiers);
         return self();
      }

      /**
       * @see Property#isUserConfigurable()
       */
      public B isUserConfigurable(Boolean userConfigurable) {
         this.userConfigurable = userConfigurable;
         return self();
      }

      /**
       * @see Property#isUserConfigurable()
       */
      public B userConfigurable() {
         this.userConfigurable = Boolean.TRUE;
         return self();
      }

      /**
       * @see Property#isUserConfigurable()
       */
      public B notUserConfigurable() {
         this.userConfigurable = Boolean.FALSE;
         return self();
      }

      /**
       * @see Property#getDefaultValue()
       */
      public B defaultValue(String defaultValue) {
         this.defaultValue = defaultValue;
         return self();
      }

      public Property build() {
         return new Property(this);
      }

      public B fromProperty(Property in) {
         return key(in.getKey()).values(in.getValues()).description(in.getDescription()).label(in.getLabel())
               .type(in.getType()).qualifiers(in.getQualifiers()).isUserConfigurable(in.isUserConfigurable()).defaultValue(in.getDefaultValue());
      }
   }

   @XmlAttribute
   private String key;
   @XmlElement(name = "Value")
   private Set<PropertyConfigurationValueType> values;
   @XmlElement(name = "Label")
   private MsgType label;
   @XmlElement(name = "Description")
   private MsgType description;
   @XmlAttribute(required = true)
   private String type;
   @XmlAttribute(required = true)
   private String qualifiers;
   @XmlAttribute
   private Boolean userConfigurable;
   @XmlAttribute(name = "value")
   private String defaultValue;

   protected Property(Builder<?> builder) {
      this.key = builder.key;
      this.values = builder.values;
      this.label = builder.label;
      this.description = builder.description;
      this.type = builder.type;
      this.qualifiers = builder.qualifiers;
      this.userConfigurable = builder.userConfigurable;
      this.defaultValue = builder.defaultValue;
   }

   protected Property() {
      // for JAXB
   }

   /**
    * Property identifier.
    */
   public String getKey() {
      return key;
   }

   /**
    * Description of property.
    */
   public MsgType getDescription() {
      return description;
   }

   /**
    * Short description of property.
    */
   public MsgType getLabel() {
      return label;
   }

   /**
    * Alternative default property values for different configuration
    */
   public Set<PropertyConfigurationValueType> getValues() {
      return values;
   }

   /**
    * Property type.
    */
   public String getType() {
      return type;
   }

   /**
    * A comma-separated set of type qualifiers.
    */
   public String getQualifiers() {
      return qualifiers;
   }

   /**
    * Determines whether the property value is configurable during installation.
    */
   public Boolean isUserConfigurable() {
      return userConfigurable;
   }

   /**
    * A Default value for property.
    */
   public String getDefaultValue() {
      if (defaultValue == null) {
         return "";
      } else {
	      return defaultValue;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(key, values, label, description, type, qualifiers, userConfigurable, defaultValue);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Property that = Property.class.cast(obj);
      return equal(this.key, that.key) &&
            equal(this.values, that.values) &&
            equal(this.label, that.label) &&
            equal(this.description, that.description) &&
            equal(this.type, that.type) &&
            equal(this.qualifiers, that.qualifiers) &&
            equal(this.userConfigurable, that.userConfigurable) &&
            equal(this.defaultValue, that.defaultValue);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("key", key).add("values", values).add("label", label).add("description", description)
            .add("type", type).add("qualifiers", qualifiers).add("userConfigurable", userConfigurable).add("defaultValue", defaultValue)
            .toString();
   }
}
