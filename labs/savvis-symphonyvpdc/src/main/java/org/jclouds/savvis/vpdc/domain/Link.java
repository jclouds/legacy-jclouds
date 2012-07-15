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
public class Link extends ResourceImpl {
   protected final String rel;

   public Link( String id,String name, String type, URI href, String rel) {
      super(id, name, type, href);
      this.rel = rel;
   }

   public String getRel() {
      return rel;
   }

   public int compareTo(Link that) {
      return (this == that) ? 0 : getHref().compareTo(that.getHref());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((rel == null) ? 0 : rel.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      Link other = (Link) obj;
      if (rel == null) {
         if (other.rel != null)
            return false;
      } else if (!rel.equals(other.rel))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", href=" + href + ", name=" + name + ", type=" + type + ", rel=" + rel + "]";
   }
}
