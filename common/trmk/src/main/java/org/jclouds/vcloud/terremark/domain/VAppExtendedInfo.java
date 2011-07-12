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
package org.jclouds.vcloud.terremark.domain;

import java.net.URI;
import java.util.List;

/**
 * @author Seshu Pasam
 */
public class VAppExtendedInfo implements Comparable<VAppExtendedInfo> {
   private final String id;
   private final URI href;
   private final String name;
   private final List<String> tags;
   private final String longName;
   private final List<NetworkAdapter> networkAdapters;

   public VAppExtendedInfo(String id, URI href, String name, List<String> tags, String longName,
           List<NetworkAdapter> networkAdapters) {
      this.id = id;
      this.href = href;
      this.name = name;
      this.tags = tags;
      this.longName = longName;
      this.networkAdapters = networkAdapters;
   }

   public int compareTo(VAppExtendedInfo that) {
      return (this == that) ? 0 : getHref().compareTo(that.getHref());
   }

   public String getId() {
      return id;
   }

   public URI getHref() {
      return href;
   }

   public String getName() {
      return name;
   }

   public List<String> getTags() {
      return tags;
   }

   public String getLongName() {
      return longName;
   }

   public List<NetworkAdapter> getNetworkAdapters() {
      return networkAdapters;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((longName == null) ? 0 : longName.hashCode());
      result = prime * result + ((tags == null) ? 0 : tags.hashCode());
      result = prime * result + ((networkAdapters == null) ? 0 : networkAdapters.hashCode());
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
      VAppExtendedInfo other = (VAppExtendedInfo) obj;
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
      if (tags == null) {
         if (other.tags != null)
            return false;
      } else if (!tags.equals(other.tags))
         return false;
      if (networkAdapters == null) {
         if (other.networkAdapters != null)
            return false;
      } else if (!networkAdapters.equals(other.networkAdapters))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[href=" + href + ", id=" + id + ", name=" + name + ", long name=" + longName
          + ", tags=" + tags.toString() + ", network adapters=" + networkAdapters.toString() + "]";
   }
}
