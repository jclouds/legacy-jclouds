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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The base type for all objects in the vCloud model.
 *
 * Has an optional list of links and href and type attributes.
 *
 * <pre>
 * &lt;xs:complexType name="ResourceType" /&gt;
 * </pre>
 *
 * @author Adrian Cole
 *
 * @since 0.9
 */
@XmlType(name = "ResourceType")
public class ResourceType<T extends ResourceType<T>> {

   @javax.annotation.Resource
   protected static Logger logger = Logger.NULL;
   
   public NewBuilder<?> toNewBuilder() {
      throw new UnsupportedOperationException("New builder not yet implemented for this class");
   }
   
   public static abstract class NewBuilder<T extends NewBuilder<T>> {
      protected abstract T self();
      
      protected URI href;
      protected String type;
      protected Set<Link> links;

      /**
       * @see ResourceType#getHref()
       */
      public T href(URI href) {
         this.href = href;
         return self();
      }

      /**
       * @see ResourceType#getType()
       */
      public T type(String type) {
         this.type = type;
         return self();
      }

      /**
       * @see ResourceType#getLinks()
       */
      public T links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return self();
      }

      /**
       * @see ResourceType#getLinks()
       */
      public T link(Link link) {
         if (links == null)
            links = Sets.newLinkedHashSet();
         this.links.add(checkNotNull(link, "link"));
         return self();
      }

      public abstract ResourceType<?> build();

      protected T fromResourceType(ResourceType<?> in) {
         return href(in.getHref()).type(in.getType()).links(in.getLinks());
      }
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromResourceType(this);
   }
   
   public static class Builder<T extends ResourceType<T>> {

      protected URI href;
      protected String type;
      protected Set<Link> links;

      /**
       * @see ResourceType#getHref()
       */
      public Builder<T> href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      public Builder<T> type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      public Builder<T> links(Set<Link> links) {
         if (checkNotNull(links, "links").size() > 0)
            this.links = Sets.newLinkedHashSet(links);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      public Builder<T> link(Link link) {
         if (links == null)
            links = Sets.newLinkedHashSet();
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      public ResourceType<T> build() {
         return new ResourceType<T>(href, type, links);
      }

      protected Builder<T> fromResourceType(ResourceType<T> in) {
         return href(in.getHref()).type(in.getType()).links(in.getLinks());
      }
   }

   @XmlAttribute
   private URI href;
   @XmlAttribute
   private String type;
   @XmlElement(name = "Link")
   private Set<Link> links;

   public ResourceType(URI href, String type, @Nullable Set<Link> links) {
      this.href = href;
      this.type = type;
      // nullable so that jaxb wont persist empty collections
      this.links = links != null && links.isEmpty() ? null : links;
   }

   protected ResourceType() {
      // For JAXB
   }

   /**
    * Contains the URI to the entity.
    *
    * An object reference, expressed in URL format. Because this URL includes the object identifier
    * portion of the id attribute value, it uniquely identifies the object, persists for the life of
    * the object, and is never reused. The value of the href attribute is a reference to a view of
    * the object, and can be used to access a representation of the object that is valid in a
    * particular context. Although URLs have a well-known syntax and a well-understood
    * interpretation, a client should treat each href as an opaque string. The rules that govern how
    * the server constructs href strings might change in future releases.
    *
    * @return an opaque reference and should never be parsed
    */
   public URI getHref() {
      return href;
   }

   /**
    * Contains the type of the the entity.
    *
    * The object type, specified as a MIME content type, of the object that the link references.
    * This attribute is present only for links to objects. It is not present for links to actions.
    *
    * @return type definition, type, expressed as an HTTP Content-Type
    */
   public String getType() {
      return type;
   }

   /**
    * Set of optional links to an entity or operation associated with this object.
    */
   public Set<Link> getLinks() {
      return links == null ? ImmutableSet.<Link>of() : Collections.unmodifiableSet(links);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ResourceType<?> that = ResourceType.class.cast(o);
      return equal(this.href, that.href) && equal(this.links, that.links) && equal(this.type, that.type);
   }
   
   public boolean clone(Object o) {
      if (this == o)
         return false;
      if (o == null || getClass() != o.getClass())
         return false;
      ResourceType<?> that = ResourceType.class.cast(o);
      return equal(this.type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(href, links, type);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("href", href).add("links", links).add("type", type);
   }
}
