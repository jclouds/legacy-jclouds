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
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Server other = (Server) obj;
      if (datacenter == null) {
         if (other.datacenter != null)
            return false;
      } else if (!datacenter.equals(other.datacenter))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (id != other.id)
         return false;
      if (image == null) {
         if (other.image != null)
            return false;
      } else if (!image.equals(other.image))
         return false;
      if (ip == null) {
         if (other.ip != null)
            return false;
      } else if (!ip.equals(other.ip))
         return false;
      if (isSandbox != other.isSandbox)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (os == null) {
         if (other.os != null)
            return false;
      } else if (!os.equals(other.os))
         return false;
      if (ram == null) {
         if (other.ram != null)
            return false;
      } else if (!ram.equals(other.ram))
         return false;
      if (state == null) {
         if (other.state != null)
            return false;
      } else if (!state.equals(other.state))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((datacenter == null) ? 0 : datacenter.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((image == null) ? 0 : image.hashCode());
      result = prime * result + ((ip == null) ? 0 : ip.hashCode());
      result = prime * result + (isSandbox ? 1231 : 1237);
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((os == null) ? 0 : os.hashCode());
      result = prime * result + ((ram == null) ? 0 : ram.hashCode());
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }

   @Override
   public int compareTo(Server o) {
      return Longs.compare(id, o.getId());
   }

   @Override
   public String toString() {
      return "Server [datacenter=" + datacenter + ", description=" + description + ", id=" + id + ", image=" + image
            + ", ip=" + ip + ", isSandbox=" + isSandbox + ", name=" + name + ", os=" + os + ", ram=" + ram + ", state="
            + state + ", type=" + type + "]";
   }
}
