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
package org.jclouds.tmrk.enterprisecloud.domain;

import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseNamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.net.URI;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <xs:complexType name="Link">
 * @author Adrian Cole
 * 
 */
public class Link extends BaseNamedResource<Link> {
    @XmlEnum
    public static enum Relationship {
      /**
       * The entity in the link owns the entity in the response
       */
      @XmlEnumValue("up")
      UP,
      /**
       * The entity in the response owns the entity in the link
       */
      @XmlEnumValue("down")
      DOWN,
      /**
       * The entity in the link is an alternate view of the entity in the
       * response
       */
      @XmlEnumValue("alternate")
      ALTERNATE,
      /**
       * The link is a path to the first page in the pages of responses
       */
      @XmlEnumValue("first")
      FIRST,
      /**
       * The link is a path to the previous page in the pages of responses
       */
      @XmlEnumValue("previous")
      PREVIOUS,
      /**
       * The link is a path to the next page in the pages of responses
       */
      @XmlEnumValue("next")
      NEXT,
      /**
       * The link is a path to the last page in the pages of responses
       */
      @XmlEnumValue("last")
      LAST,
      /**
       * Relationship was not parsed by jclouds.
       */
      UNRECOGNIZED;

      public String value() {
         return name().toLowerCase();
      }

      @Override
      public String toString() {
         return value();
      }

      public static Relationship fromValue(String rel) {
         try {
            return valueOf(checkNotNull(rel, "rel").toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromLink(this);
   }

   public static class Builder extends BaseNamedResource.Builder<Link> {

      protected Relationship rel;

      /**
       * @see Link#getRelationship
       */
      public Builder rel(Relationship rel) {
         this.rel = rel;
         return this;
      }

      @Override
      public Link build() {
         return new Link(href, type, name, rel);
      }

      public Builder fromLink(Link in) {
         return fromNamedResource(in).rel(in.getRelationship());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(BaseResource<Link> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromNamedResource(BaseNamedResource<Link> in) {
         return Builder.class.cast(super.fromNamedResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

       /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         super.fromAttributes(attributes);
         if (attributes.containsKey("rel"))
            rel(Relationship.fromValue(attributes.get("rel")));
         return this;
      }
   }

   @XmlAttribute
   protected Relationship rel;

   public Link(URI href, String type, String name, Relationship rel) {
      super(href, type, name);
      this.rel = checkNotNull(rel, "rel");
   }

   protected Link() {
       //For JAXB
   }

   /**
    * 
    * @return
    */
   public Relationship getRelationship() {
      return rel;
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Link link = (Link) o;

        if (rel != link.rel) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + rel.hashCode();
        return result;
    }

    @Override
    public String string() {
        return super.string()+", rel="+rel;
    }
}