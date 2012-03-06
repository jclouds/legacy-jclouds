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
package org.jclouds.savvis.vpdc.domain;

import java.net.URI;

/**
 * Location of a Rest resource
 * 
 * @author Adrian Cole
 * 
 */
public class ResourceImpl implements Resource {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String id;
      protected String name;
      protected String type;
      protected URI href;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder type(String type) {
         this.type = type;
         return this;
      }

      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      public ResourceImpl build() {
         return new ResourceImpl(id, name, type, href);
      }

      public static Builder fromResource(ResourceImpl in) {
         return new Builder().id(in.getId()).name(in.getName()).type(in.getType()).href(in.getHref());
      }
   }

   protected final String id;
   protected final String name;
   protected final String type;
   protected final URI href;

   public ResourceImpl(String id, String name, String type, URI href) {
      this.id = id;
      this.name = name;
      this.type = type;
      this.href = href;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getType() {
      return type;
   }

   public URI getHref() {
      return href;
   }

   public int compareTo(Resource that) {
      return (this == that) ? 0 : getHref().compareTo(that.getHref());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      ResourceImpl other = (ResourceImpl) obj;
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
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

   public Builder toBuilder() {
      return Builder.fromResource(this);
   }

   @Override
   public String toString() {
      return "[id=" + id + ", href=" + href + ", name=" + name + ", type=" + type + "]";
   }
}