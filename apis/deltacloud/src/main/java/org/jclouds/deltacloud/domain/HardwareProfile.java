/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.deltacloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * A hardware profile represents a configuration of resources upon which a machine may be deployed.
 * It defines aspects such as local disk storage, available RAM, and architecture. Each provider is
 * free to define as many (or as few) hardware profiles as desired.
 * 
 * @author Adrian Cole
 */
public class HardwareProfile {
   private final URI href;
   private final String id;
   private final String name;
   private final Set<? extends HardwareProperty> properties;

   public HardwareProfile(URI href, String id, String name, Set<? extends HardwareProperty> properties) {
      this.href = checkNotNull(href, "href");
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.properties = ImmutableSet.copyOf(checkNotNull(properties, "properties"));
   }

   /**
    * 
    * @return URL to manipulate a specific profile
    */
   public URI getHref() {
      return href;
   }

   /**
    * 
    * @return A unique identifier for the profile
    */
   public String getId() {
      return id;
   }

   /**
    * 
    * @return A short label
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return properties included in this hardware profile
    */
   public Set<? extends HardwareProperty> getProperties() {
      return properties;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
      HardwareProfile other = (HardwareProfile) obj;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
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
      if (properties == null) {
         if (other.properties != null)
            return false;
      } else if (!properties.equals(other.properties))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[href=" + href + ", id=" + id + ", name=" + name + ", properties=" + properties + "]";
   }

}
