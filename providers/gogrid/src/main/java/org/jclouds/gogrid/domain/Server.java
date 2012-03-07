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
package org.jclouds.gogrid.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

/**
 * @author Oleksiy Yarmula
 */
public class Server implements Comparable<Server> {
   private long id;
   private boolean isSandbox;
   private String name;
   private String description;
   private ServerState state;
   private Option datacenter;

   private Option type;
   private Option ram;
   private Option os;
   private Ip ip;

   private ServerImage image;

   /**
    * A no-args constructor is required for deserialization
    */
   Server() {
   }

   public Server(long id, Option datacenter, boolean sandbox, String name, String description, ServerState state,
            Option type, Option ram, Option os, Ip ip, ServerImage image) {
      this.id = id;
      this.isSandbox = sandbox;
      this.name = name;
      this.description = description;
      this.state = state;
      this.type = type;
      this.ram = ram;
      this.os = os;
      this.ip = ip;
      this.image = image;
      this.datacenter = datacenter;
   }

   public long getId() {
      return id;
   }

   public boolean isSandbox() {
      return isSandbox;
   }

   public Option getDatacenter() {
      return datacenter;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public ServerState getState() {
      return state;
   }

   public Option getType() {
      return type;
   }

   public Option getRam() {
      return ram;
   }

   public Option getOs() {
      return os;
   }

   public Ip getIp() {
      return ip;
   }

   public ServerImage getImage() {
      return image;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Server) {
         final Server other = Server.class.cast(object);
         return equal(id, other.id) && equal(name, other.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name);
   }

   @Override
   public int compareTo(Server that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return Longs.compare(id, that.getId());
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("name", name).add("description", description).add("os", os).add(
               "image", image).add("datacenter", datacenter).add("state", state).add("ip", ip).add("isSandbox",
               isSandbox).add("ram", ram).add("type", type).toString();
   }

}
