/*
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
package org.jclouds.openstack.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;

import javax.inject.Named;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * For convenience, resources contain links to themselves. This allows a api to easily obtain a
 * resource URIs rather than to construct them.
 * 
 * @author AdrianCole
 * @see <a href= "http://docs.openstack.org/api/openstack-compute/1.1/content/LinksReferences.html"
 *      />
 */
public class Link {
   /**
    * Relations associated with resources.
    */
   public static enum Relation {
      /**
       * a versioned link to the resource. These links should be used in cases where the link will
       * be followed immediately.
       */
      SELF,
      /**
       * a permanent link to a resource that is appropriate for long term storage.
       */
      BOOKMARK,
      /**
       * 
       */
      DESCRIBEDBY,
      /**
       * Indicates that the link's context is a part of a series, and that the next in the series is
       * the link target.
       */
      NEXT,

      /**
       * Indicates that the link's context is a part of a series, and that the previous in the
       * series is the link target.
       */
      PREVIOUS,

      ALTERNATE,
      /**
       * the value returned by the OpenStack service was not recognized.
       */
      UNRECOGNIZED;

      public String value() {
         return name().toLowerCase();
      }

      public static Relation fromValue(String v) {
         try {
            return valueOf(v.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Link create(Relation relation, URI href) {
      return new Link(relation, null, href);
   }

   public static Link create(Relation relation, String type, URI href) {
      return new Link(relation, Optional.fromNullable(type), href);
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromLink(this);
   }

   public static class Builder {

      protected Link.Relation relation;
      protected Optional<String> type = Optional.absent();
      protected URI href;

      /**
       * @see Link#getRelation()
       */
      public Builder relation(Link.Relation relation) {
         this.relation = relation;
         return this;
      }

      /**
       * @see Link#getType()
       */
      public Builder type(String type) {
         this.type = Optional.fromNullable(type);
         return this;
      }

      /**
       * @see Link#getHref()
       */
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      public Link build() {
         return new Link(relation, type, href);
      }

      public Builder fromLink(Link in) {
         return this.relation(in.getRelation()).type(in.getType().orNull()).href(in.getHref());
      }
   }

   @Named("rel")
   private final Link.Relation relation;
   private final Optional<String> type;
   private final URI href;

   @ConstructorProperties({ "rel", "type", "href" })
   protected Link(Link.Relation relation, Optional<String> type, URI href) {
      this.href = checkNotNull(href, "href");
      this.relation = checkNotNull(relation, "relation of %s", href);
      this.type = (type == null) ? Optional.<String> absent() : type;
   }

   /**
    * There are three kinds of link relations associated with resources. A self link contains a
    * versioned link to the resource. These links should be used in cases where the link will be
    * followed immediately. A bookmark link provides a permanent link to a resource that is
    * appropriate for long term storage. An alternate link can contain an alternate representation
    * of the resource. For example, an OpenStack Compute image may have an alternate representation
    * in the OpenStack Image service. Note that the type attribute here is used to provide a hint as
    * to the type of representation to expect when following the link.
    * 
    * @return the relation of the resource in the current OpenStack deployment
    */
   public Link.Relation getRelation() {
      return this.relation;
   }

   /**
    * @return the type of the resource or null if not specified
    */
   public Optional<String> getType() {
      return this.type;
   }

   /**
    * @return the href of the resource
    */
   public URI getHref() {
      return this.href;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(relation, type, href);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Link that = Link.class.cast(obj);
      return Objects.equal(this.relation, that.relation) && Objects.equal(this.type, that.type)
               && Objects.equal(this.href, that.href);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("relation", relation).add("type", type.orNull())
               .add("href", href);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
