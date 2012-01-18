/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Name 2.0 (the
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

package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * Resource found in a paginated collection
 * 
 * @author AdrianCole
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/1.1/content/Paginated_Collections-d1e664.html"
 *      />
 */
public class Resource implements Comparable<Resource> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromResource(this);
   }

   public static class Builder {
      protected String id;
      protected String name;
      protected Set<Link> links = ImmutableSet.of();

      /**
       * @see Resource#getId()
       */
      public Builder id(String id) {
         this.id = checkNotNull(id, "id");
         return this;
      }

      /**
       * @see Resource#getName()
       */
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * @see Resource#getLinks()
       */
      public Builder links(Link... links) {
         return links(ImmutableSet.copyOf(checkNotNull(links, "links")));
      }

      /**
       * @see Resource#getLinks()
       */
      public Builder links(Set<Link> links) {
         this.links = ImmutableSet.copyOf(checkNotNull(links, "links"));
         return this;
      }

      public Resource build() {
         return new Resource(id, name, links);
      }

      public Builder fromResource(Resource from) {
         return id(from.getId()).name(from.getName()).links(from.getLinks());
      }
   }

   protected final String id;
   protected final String name;
   protected final Set<Link> links;

   public Resource(String id, String name, Set<Link> links) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.links = ImmutableSet.copyOf(checkNotNull(links, "links"));
   }

   /**
    * When providing an ID, it is assumed that the resource exists in the current OpenStack
    * deployment
    * 
    * @return the id of the resource in the current OpenStack deployment
    */
   public String getId() {
      return id;
   }

   /**
    * @return the name of the resource
    */
   public String getName() {
      return name;
   }

   /**
    * @return the links of the id address allocated to the new server
    */
   public Set<Link> getLinks() {
      return links;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Resource) {
         final Resource other = Resource.class.cast(object);
         return equal(id, other.id) && equal(name, other.name) && equal(links, other.links);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, links);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("name", name).add("links", links).toString();
   }

   @Override
   public int compareTo(Resource that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.id.compareTo(that.id);
   }

}
