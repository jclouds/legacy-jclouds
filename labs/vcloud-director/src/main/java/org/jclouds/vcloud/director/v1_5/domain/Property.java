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
package org.jclouds.vcloud.director.v1_5.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

/**
 * Contains key/value pair as property.
 * <p/>
 * <pre>
 * &lt;complexType name="PropertyType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "Property")
public class Property {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.PROPERTY;

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder {

      private String value;
      private String key;

      /**
       * @see Property#getKey()
       */
      public Builder key(String key) {
         this.key = key;
         return this;
      }

      /**
       * @see Property#getValue()
       */
      public Builder value(String value) {
         this.value = value;
         return this;
      }

      /**
       * @see Property#getKey()
       * @see Property#getValue()
       */
      public Builder property(String key, String value) {
         this.key = key;
         this.value = value;
         return this;
      }

      public Property build() {
         return new Property(key, value);
      }

      public Builder fromProperty(Property in) {
         return property(in.getKey(), in.getValue());
      }
   }

   private Property() {
      // For JAXB
   }

   private Property(String key, String value) {
      this.value = value;
      this.key = key;
   }

   @XmlValue
   private String value;
   @XmlAttribute(required = true)
   private String key;

   /**
    * Gets the value of the value property.
    */
   public String getValue() {
      return value;
   }

   /**
    * Gets the value of the key property.
    */
   public String getKey() {
      return key;
   }
}
