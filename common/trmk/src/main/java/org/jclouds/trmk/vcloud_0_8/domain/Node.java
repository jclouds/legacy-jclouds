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
package org.jclouds.trmk.vcloud_0_8.domain;

import java.net.URI;

/**
 * @author Adrian Cole
 */
public class Node implements Comparable<Node> {
   private final String name;
   private final URI id;
   private final String ipAddress;
   private final int port;
   private final boolean enabled;
   private final String description;

   public Node(String name, URI id, String ipAddress, int port, boolean enabled, String description) {
      this.name = name;
      this.id = id;
      this.ipAddress = ipAddress;
      this.port = port;
      this.enabled = enabled;
      this.description = description;
   }

   public int getPort() {
      return port;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public String getDescription() {
      return description;
   }

   public String getName() {
      return name;
   }

   public URI getId() {
      return id;
   }

   public String getIpAddress() {
      return ipAddress;
   }

   public int compareTo(Node that) {
      return (this == that) ? 0 : getId().compareTo(that.getId());
   }

   @Override
   public String toString() {
      return "Node [id=" + id + ", name=" + name + ", description=" + description + ", ipAddress=" + ipAddress
            + ", port=" + port + ", enabled=" + enabled + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + (enabled ? 1231 : 1237);
      result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + port;
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
      Node other = (Node) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (enabled != other.enabled)
         return false;
      if (ipAddress == null) {
         if (other.ipAddress != null)
            return false;
      } else if (!ipAddress.equals(other.ipAddress))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (port != other.port)
         return false;
      return true;
   }
}
