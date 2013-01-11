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
package org.jclouds.rackspace.cloudloadbalancers.domain;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;


/**
 * @author Everett Toews
 */
public class Metadata {
   private int id;
   private String key;
   private String value;
   
   private Metadata(Integer id, String key, String value) {
      this.id = id;
      this.key = key;
      this.value = value;
   }
   
   public int getId() {
      return id;
   }
   
   public String getKey() {
      return key;
   }
   
   public String getValue() {
      return value;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Metadata that = Metadata.class.cast(obj);
      
      return Objects.equal(this.id, that.id);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("id", id).add("key", key).add("value", value);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   public static class Builder {
      private Integer id;
      private String key;
      private String value;

      public Builder id(Integer id) {
         this.id = id;
         return this;
      }

      public Builder key(String key) {
         this.key = key;
         return this;
      }

      public Builder value(String value) {
         this.value = value;
         return this;
      }

      public Metadata build() {
         return new Metadata(id, key, value);
      }

      public Builder from(Metadata in) {
         return id(in.getId()).key(in.getKey()).value(in.getValue());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().from(this);
   }
}
