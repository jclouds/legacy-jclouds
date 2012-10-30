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
package org.jclouds.trmk.vcloud_0_8.domain.internal;

import java.net.URI;

import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;

/**
 * Location of a Rest resource
 * 
 * @author Adrian Cole
 * 
 */
public class ReferenceTypeImpl implements ReferenceType {
   private final String name;
   private final String type;
   private final URI href;

   public ReferenceTypeImpl(String name, String type, URI href) {
      this.name = name;
      this.type = type;
      this.href = href;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getType() {
      return type;
   }

   @Override
   public URI getHref() {
      return href;
   }

   @Override
   public int compareTo(ReferenceType that) {
      return (this == that) ? 0 : getHref().compareTo(that.getHref());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((href == null) ? 0 : href.hashCode());
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
      ReferenceTypeImpl other = (ReferenceTypeImpl) obj;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
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

   @Override
   public String toString() {
      return "[href=" + href + ", name=" + name + ", type=" + type + "]";
   }
}
