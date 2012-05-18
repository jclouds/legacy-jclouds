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
package org.jclouds.cloudstack.domain;

import com.google.common.base.Objects;

import java.util.Date;

/**
 * Represents an alert issued by Cloudstack
 *
 * @author Richard Downer
 */
public class Alert implements Comparable<Alert> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String description;
      private Date sent;
      private String type;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder sent(Date sent) {
         this.sent = sent;
         return this;
      }

      public Builder type(String type) {
         this.type = type;
         return this;
      }

      public Alert build() {
         return new Alert(id, description, sent, type);
      }
   }

   private long id;
   private String description;
   private Date sent;
   private String type;

   /* exists for the deserializer, only */
   Alert() {
   }

   private Alert(long id, String description, Date sent, String type) {
      this.id = id;
      this.description = description;
      this.sent = sent;
      this.type = type;
   }

   public long getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public Date getSent() {
      return sent;
   }

   public String getType() {
      return type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Alert that = (Alert) o;

      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(description, that.description)) return false;
      if (!Objects.equal(sent, that.sent)) return false;
      if (!Objects.equal(type, that.type)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(id, description, sent, type);
   }


   @Override
   public String toString() {
      return "Alert{" +
            "id=" + id +
            ", description='" + description + '\'' +
            ", sent=" + sent +
            ", type='" + type + '\'' +
            '}';
   }

   @Override
   public int compareTo(Alert other) {
      return Long.valueOf(this.getId()).compareTo(other.getId());
   }
}
