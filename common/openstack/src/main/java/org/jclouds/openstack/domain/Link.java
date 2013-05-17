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
package org.jclouds.openstack.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * For convenience, resources contain links to themselves. This allows a client to easily obtain a
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
       * an alternate representation of the resource. For example, an OpenStack Compute image may
       * have an alternate representation in the OpenStack Image service.
       */
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
   
   public static Link create(Relation relation,String type, URI href) {
      return new Link(relation, type, href);
   }
   
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromLink(this);
   }

   public static class Builder {
      protected Relation relation;
      protected String type;
      protected URI href;

      /**
       * @see Link#getRelation()
       */
      public Builder relation(Relation relation) {
         this.relation = checkNotNull(relation, "relation");
         return this;
      }

      /**
       * @see Link#getType()
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }
      
      /**
       * @see Link#getHref()
       */
      public Builder href(URI href) {
         this.href = checkNotNull(href, "href");
         return this;
      }

      public Link build(){
         return new Link(relation, type, href);
      }
      
      public Builder fromLink(Link from) {
         return relation(from.getRelation()).type(from.getType()).href(from.getHref());
      }
   }
  
   @Named("rel")
   protected Relation relation;
   protected String type;
   protected URI href;

   @ConstructorProperties({"rel", "type", "href"})
   protected Link(Relation relation, @Nullable String type, URI href) {
      this.relation = checkNotNull(relation, "relation");
      this.type = type;
      this.href = checkNotNull(href, "href");
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
   public Relation getRelation() {
      return relation;
   }
   
   /**
    * @return the type of the resource or null if not specified
    */
   @Nullable
   public String getType() {
      return type;
   }
   
   /**
    * @return the href of the resource
    */
   public URI getHref() {
      return href;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Link) {
         final Link other = Link.class.cast(object);
         return equal(relation, other.relation) && equal(type, other.type) && equal(href, other.href);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(relation, type, href);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("relation", relation).add("type", type).add("href", href).toString();
   }

}
