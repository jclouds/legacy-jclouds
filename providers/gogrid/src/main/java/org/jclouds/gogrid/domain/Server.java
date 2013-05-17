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
package org.jclouds.gogrid.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.primitives.Longs;

/**
 * Class Server
 * 
 * @author Oleksiy Yarmula
*/
public class Server implements Comparable<Server> {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromServer(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected long id;
      protected boolean isSandbox;
      protected String name;
      protected String description;
      protected ServerState state;
      protected Option datacenter;
      protected Option type;
      protected Option ram;
      protected Option os;
      protected Ip ip;
      protected ServerImage image;
   
      /** 
       * @see Server#getId()
       */
      public T id(long id) {
         this.id = id;
         return self();
      }

      /** 
       * @see Server#isSandbox()
       */
      public T isSandbox(boolean isSandbox) {
         this.isSandbox = isSandbox;
         return self();
      }

      /** 
       * @see Server#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see Server#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /** 
       * @see Server#getState()
       */
      public T state(ServerState state) {
         this.state = state;
         return self();
      }

      /** 
       * @see Server#getDatacenter()
       */
      public T datacenter(Option datacenter) {
         this.datacenter = datacenter;
         return self();
      }

      /** 
       * @see Server#getType()
       */
      public T type(Option type) {
         this.type = type;
         return self();
      }

      /** 
       * @see Server#getRam()
       */
      public T ram(Option ram) {
         this.ram = ram;
         return self();
      }

      /** 
       * @see Server#getOs()
       */
      public T os(Option os) {
         this.os = os;
         return self();
      }

      /** 
       * @see Server#getIp()
       */
      public T ip(Ip ip) {
         this.ip = ip;
         return self();
      }

      /** 
       * @see Server#getImage()
       */
      public T image(ServerImage image) {
         this.image = image;
         return self();
      }

      public Server build() {
         return new Server(id, isSandbox, name, description, state, datacenter, type, ram, os, ip, image);
      }
      
      public T fromServer(Server in) {
         return this
                  .id(in.getId())
                  .isSandbox(in.isSandbox())
                  .name(in.getName())
                  .description(in.getDescription())
                  .state(in.getState())
                  .datacenter(in.getDatacenter())
                  .type(in.getType())
                  .ram(in.getRam())
                  .os(in.getOs())
                  .ip(in.getIp())
                  .image(in.getImage());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final long id;
   private final boolean isSandbox;
   private final String name;
   private final String description;
   private final ServerState state;
   private final Option datacenter;
   private final Option type;
   private final Option ram;
   private final Option os;
   private final Ip ip;
   private final ServerImage image;

   @ConstructorProperties({
      "id", "isSandbox", "name", "description", "state", "datacenter", "type", "ram", "os", "ip", "image"
   })
   protected Server(long id, boolean isSandbox, String name, @Nullable String description, ServerState state,
                    @Nullable Option datacenter, Option type, Option ram, Option os, Ip ip, ServerImage image) {
      this.id = id;
      this.isSandbox = isSandbox;
      this.name = checkNotNull(name, "name");
      this.description = description;
      this.state = checkNotNull(state, "state");
      this.datacenter = datacenter;
      this.type = checkNotNull(type, "type");
      this.ram = checkNotNull(ram, "ram");
      this.os = checkNotNull(os, "os");
      this.ip = checkNotNull(ip, "ip");
      this.image = checkNotNull(image, "image");
   }

   public long getId() {
      return this.id;
   }

   public boolean isSandbox() {
      return this.isSandbox;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   public ServerState getState() {
      return this.state;
   }
   
   @Nullable
   public Option getDatacenter() {
      return this.datacenter;
   }

   public Option getType() {
      return this.type;
   }

   public Option getRam() {
      return this.ram;
   }

   public Option getOs() {
      return this.os;
   }

   public Ip getIp() {
      return this.ip;
   }

   public ServerImage getImage() {
      return this.image;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, isSandbox, name, description, state, datacenter, type, ram, os, ip, image);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Server that = Server.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.isSandbox, that.isSandbox)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.description, that.description)
               && Objects.equal(this.state, that.state)
               && Objects.equal(this.datacenter, that.datacenter)
               && Objects.equal(this.type, that.type)
               && Objects.equal(this.ram, that.ram)
               && Objects.equal(this.os, that.os)
               && Objects.equal(this.ip, that.ip)
               && Objects.equal(this.image, that.image);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("isSandbox", isSandbox).add("name", name).add("description", description).add("state", state).add("datacenter", datacenter).add("type", type).add("ram", ram).add("os", os).add("ip", ip).add("image", image);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Server that) {
      return Longs.compare(id, that.getId());
   }
}
