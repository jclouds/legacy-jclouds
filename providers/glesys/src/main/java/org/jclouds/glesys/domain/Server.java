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

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Listing of a server.
 *
 * @author Adrian Cole
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#server_list" />
 */
public class Server {

   /**
    */
   public static enum State {

      RUNNING, LOCKED, STOPPED, UNRECOGNIZED;

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static State fromValue(String state) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(state, "state")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServer(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String hostname;
      protected String datacenter;
      protected String platform;

      /**
       * @see Server#getId()
       */
      public T id(String id) {
         this.id = checkNotNull(id, "id");
         return self();
      }

      /**
       * @see Server#getHostname()
       */
      public T hostname(String hostname) {
         this.hostname = checkNotNull(hostname, "hostname");
         return self();
      }

      /**
       * @see Server#getDatacenter()
       */
      public T datacenter(String datacenter) {
         this.datacenter = checkNotNull(datacenter, "datacenter");
         return self();
      }

      /**
       * @see Server#getPlatform()
       */
      public T platform(String platform) {
         this.platform = checkNotNull(platform, "platform");
         return self();
      }

      public Server build() {
         return new Server(id, hostname, datacenter, platform);
      }

      public T fromServer(Server in) {
         return this.id(in.getId()).hostname(in.getHostname()).datacenter(in.getDatacenter()).platform(in.getPlatform());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String hostname;
   private final String datacenter;
   private final String platform;

   @ConstructorProperties({
         "serverid", "hostname", "datacenter", "platform"
   })
   protected Server(String id, String hostname, String datacenter, String platform) {
      this.id = checkNotNull(id, "id");
      this.hostname = checkNotNull(hostname, "hostname");
      this.datacenter = checkNotNull(datacenter, "datacenter");
      this.platform = checkNotNull(platform, "platform");
   }

   /**
    * @return the generated id of the server
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the hostname of the server
    */
   public String getHostname() {
      return this.hostname;
   }

   /**
    * @return platform running the server (ex. {@code OpenVZ})
    */
   public String getDatacenter() {
      return this.datacenter;
   }

   /**
    * @return the datacenter the server exists in (ex. {@code Falkenberg})
    */
   public String getPlatform() {
      return this.platform;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Server that = Server.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("id", id).add("hostname", hostname).add("datacenter", datacenter)
            .add("platform", platform);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
