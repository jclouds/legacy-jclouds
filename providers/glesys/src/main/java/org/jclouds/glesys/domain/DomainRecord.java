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
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * DNS record data.
 *
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#domain_list_records" />
 */
public class DomainRecord {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromDomainRecord(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String domainname;
      protected String host;
      protected String type;
      protected String data;
      protected int ttl;

      /**
       * @see DomainRecord#getId()
       */
      public T id(String id) {
         this.id = checkNotNull(id, "id");
         return self();
      }

      /**
       * @see DomainRecord#getname()
       */
      public T domainname(String domainname) {
         this.domainname = checkNotNull(domainname, "domainname");
         return self();
      }

      /**
       * @see DomainRecord#getHost()
       */
      public T host(String host) {
         this.host = checkNotNull(host, "host");
         return self();
      }

      /**
       * @see DomainRecord#getType()
       */
      public T type(String type) {
         this.type = checkNotNull(type, "type");
         return self();
      }

      /**
       * @see DomainRecord#getData()
       */
      public T data(String data) {
         this.data = checkNotNull(data, "data");
         return self();
      }

      /**
       * @see DomainRecord#getTtl()
       */
      public T ttl(int ttl) {
         this.ttl = ttl;
         return self();
      }

      public DomainRecord build() {
         return new DomainRecord(id, domainname, host, type, data, ttl);
      }

      public T fromDomainRecord(DomainRecord in) {
         return this.id(in.getId())
               .domainname(in.getname())
               .host(in.getHost())
               .type(in.getType())
               .data(in.getData())
               .ttl(in.getTtl());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String domainname;
   private final String host;
   private final String type;
   private final String data;
   private final int ttl;

   @ConstructorProperties({
         "recordid", "domainname", "host", "type", "data", "ttl"
   })
   protected DomainRecord(@Nullable String id, String domainname, String host, String type, @Nullable String data, int ttl) {
      this.id = id;
      this.domainname = checkNotNull(domainname, "domainname");
      this.host = checkNotNull(host, "host");
      this.type = checkNotNull(type, "type");
      this.data = data;
      this.ttl = ttl;
   }

   /**
    * @return the id of the record used to modify it via the API
    * @see org.jclouds.glesys.features.DomainApi
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the zone content of the record
    */
   public String getname() {
      return this.domainname;
   }

   /**
    * @return the host content of the record
    */
   public String getHost() {
      return this.host;
   }

   /**
    * @return the type of the record, ex. "A"
    */
   public String getType() {
      return this.type;
   }

   /**
    * @return the data content of the record
    */
   @Nullable
   public String getData() {
      return this.data;
   }

   /**
    * @return the TTL/Time-to-live for the record
    */
   public int getTtl() {
      return this.ttl;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      DomainRecord that = DomainRecord.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("id", id).add("domainname", domainname).add("host", host).add("type", type).add("data", data)
            .add("ttl", ttl);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
