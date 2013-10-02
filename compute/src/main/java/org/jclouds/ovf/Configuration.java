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
public class Configuration {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String id;
      protected String label;
      protected String description;

      /**
       * @see Configuration#getId
       */
      public Builder id(String id) {
         this.id = id;
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

      public Configuration build() {
         return new Configuration(id, label, description);
      }

      public Builder fromConfiguration(Configuration in) {
         return id(in.getId()).description(in.getDescription()).label(in.getLabel());
      }
   }

   private final String id;
   private final String label;
   private final String description;

   public Configuration(String id, String label, String description) {
      this.id = id;
      this.label = label;
      this.description = description;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
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
      Configuration other = (Configuration) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[id=%s, label=%s, description=%s]", id, label, description);
   }

   public String getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public String getLabel() {
      return label;
   }
}
