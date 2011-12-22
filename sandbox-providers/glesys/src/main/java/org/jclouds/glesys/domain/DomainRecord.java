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
package org.jclouds.glesys.domain;

import com.google.common.base.Objects;

/**
 * DNS record data.
 *
 * @author Adam Lowe
 */
public class DomainRecord implements Comparable<DomainRecord> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String zone;
      private String host;
      private String type;
      private String data;
      private int ttl;

      public Builder id(String id) {
         this.id = id;
         return this;
      }


      public Builder zone(String zone) {
         this.zone = zone;
         return this;
      }

      public Builder host(String host) {
         this.host = host;
         return this;
      }

      public Builder type(String type) {
         this.type = type;
         return this;
      }

      public Builder data(String data) {
         this.data = data;
         return this;
      }

      public Builder ttl(int ttl) {
         this.ttl = ttl;
         return this;
      }

      public DomainRecord build() {
         return new DomainRecord(id, zone, host, type, data, ttl);
      }

      public Builder fromDomainRecord(DomainRecord in) {
         return new Builder().id(in.getId()).zone(in.getZone()).host(in.getHost()).type(in.getType()).data(in.getData()).ttl(in.getTtl());
      }
   }

   private final String id;
   private final String zone;
   private final String host;
   private final String type;
   private final String data;
   private final int ttl;

   public DomainRecord(String id, String zone, String host, String type, String data, int ttl) {
      this.id = id;
      this.zone = zone;
      this.host = host;
      this.type = type;
      this.data = data;
      this.ttl = ttl;
   }

   public String getId() {
      return id;
   }

   public String getZone() {
      return zone;
   }

   public String getHost() {
      return host;
   }

   public String getType() {
      return type;
   }

   public String getData() {
      return data;
   }

   public int getTtl() {
      return ttl;
   }

   @Override
   public int compareTo(DomainRecord other) {
      return id.compareTo(other.getId());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof DomainRecord) {
         DomainRecord other = (DomainRecord) object;
         return Objects.equal(id, other.id);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return String.format("[id=%s, zone=%s, host=%s, type=%s, data=%s, ttl=%d]", id, zone, host, type, data, ttl);
   }

}