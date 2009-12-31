/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.domain;

import java.net.URI;

/**
 * @author Adrian Cole
 */
public class InternetService implements Comparable<InternetService> {
   private final int id;
   private final String name;
   private final URI location;
   private final PublicIpAddress publicIpAddress;
   private final int port;
   private final Protocol protocol;
   private final boolean enabled;
   private final int timeout;
   private final String description;

   public InternetService(int id, String name, URI location, PublicIpAddress publicIpAddress,
            int port, Protocol protocol, boolean enabled, int timeout, String description) {
      this.id = id;
      this.name = name;
      this.location = location;
      this.publicIpAddress = publicIpAddress;
      this.port = port;
      this.protocol = protocol;
      this.enabled = enabled;
      this.timeout = timeout;
      this.description = description;
   }

   public PublicIpAddress getPublicIpAddress() {
      return publicIpAddress;
   }

   public int getPort() {
      return port;
   }

   public Protocol getProtocol() {
      return protocol;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public int getTimeout() {
      return timeout;
   }

   public String getDescription() {
      return description;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public URI getLocation() {
      return location;
   }
   
   public int compareTo(InternetService that) {
      if (this == that)
         return 0;
      if (this.id < that.id)
         return -1;
      if (this.id > that.id)
         return 1;
      return 0;
   }

   @Override
   public String toString() {
      return "InternetService [description=" + description + ", enabled=" + enabled + ", id=" + id
               + ", location=" + location + ", name=" + name + ", port=" + port + ", protocol="
               + protocol + ", publicIpAddress=" + publicIpAddress + ", timeout=" + timeout + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + (enabled ? 1231 : 1237);
      result = prime * result + id;
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + port;
      result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
      result = prime * result + ((publicIpAddress == null) ? 0 : publicIpAddress.hashCode());
      result = prime * result + timeout;
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
      InternetService other = (InternetService) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (enabled != other.enabled)
         return false;
      if (id != other.id)
         return false;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (port != other.port)
         return false;
      if (protocol == null) {
         if (other.protocol != null)
            return false;
      } else if (!protocol.equals(other.protocol))
         return false;
      if (publicIpAddress == null) {
         if (other.publicIpAddress != null)
            return false;
      } else if (!publicIpAddress.equals(other.publicIpAddress))
         return false;
      if (timeout != other.timeout)
         return false;
      return true;
   }

}