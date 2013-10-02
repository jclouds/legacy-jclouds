/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ovf;

/**
 * 
 * @author Adrian Cole
 */
public class Property {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String key;
      protected String value;
      protected String label;
      protected String description;

      /**
       * @see Property#getKey
       */
      public Builder key(String key) {
         this.key = key;
         return this;
      }

      /**
       * @see Property#getValue
       */
      public Builder value(String value) {
         this.value = value;
         return this;
      }

      /**
       * @see Section#getLabel
       */
      public Builder label(String label) {
         this.label = label;
         return this;
      }

      /**
       * @see Section#getDescription
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Property build() {
         return new Property(key, value, label, description);
      }

      public Builder fromProperty(Property in) {
         return key(in.getKey()).value(in.getValue()).description(in.getDescription()).label(in.getLabel());
      }
   }

   private final String key;
   private final String value;
   private final String label;
   private final String description;

   public Property(String key, String value, String label, String description) {
      this.key = key;
      this.value = value;
      this.label = label;
      this.description = description;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Property other = (Property) obj;
      if (key == null) {
         if (other.key != null)
            return false;
      } else if (!key.equals(other.key))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[key=%s, value=%s, label=%s, description=%s]", key, value, label, description);
   }

   public String getKey() {
      return key;
   }

   public String getDescription() {
      return description;
   }

   public String getLabel() {
      return label;
   }

   public String getValue() {
      return value;
   }
}
