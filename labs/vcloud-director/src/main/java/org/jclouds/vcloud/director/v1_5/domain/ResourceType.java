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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.*;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * The base type for all objects in the vCloud model.
 *
 * Has an optional list of links and href and type attributes.
 *
 * <pre>
 * &lt;xs:complexType name="ResourceType"&gt;
 * </pre>
 * 
 * @author Adrian Cole
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceType<T extends ResourceType<T>> implements URISupplier {

   public static <T extends ResourceType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromResourceType(this);
   }

   public static class Builder<T extends ResourceType<T>> {

      protected URI href;
      protected String type;
      protected Set<Link> links = Sets.newLinkedHashSet();

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
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      public Builder<T> link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      public ResourceType<T> build() {
         ResourceType<T> reference = new ResourceType<T>(href);
         reference.setType(type);
         reference.setLinks(links);
         return reference;
      }

      protected Builder<T> fromResourceType(ResourceType<T> in) {
         return href(in.getHref()).type(in.getType()).links(in.getLinks());
      }
   }

   @XmlAttribute
   private URI href;
   @XmlAttribute
   private String type;
   @XmlElement(namespace = XMLNS, name = "Link")
   private Set<Link> links = Sets.newLinkedHashSet();

   protected ResourceType(URI href) {
      this.href = href;
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
    * @see URISupplier#getURI()
    */
   @Override
   public URI getURI() {
      return getHref();
   }

   /**
    * Returns the unique UUID string for this resource.
    *
    * @see ReferenceType#getUuid()
    */
   public String getUuid() {
      return Iterables.getLast(Splitter.on("/").split(href.getPath()));
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

   public void setType(String type) {
      this.type = type;
   }

   /**
    * Set of optional links to an entity or operation associated with this object.
    */
   public Set<Link>getLinks() {
      return links;
   }

   public void setLinks(Set<Link> links) {
      this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
   }

   public void addLink(Link link) {
      this.links.add(checkNotNull(link, "link"));
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