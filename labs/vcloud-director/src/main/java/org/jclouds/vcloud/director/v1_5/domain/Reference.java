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

import static com.google.common.base.Objects.equal;

import java.net.URI;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A reference to a resource.
 *
 * Contains an href attribute and optional name and type attributes.
 *
 * <pre>
 * &lt;xs:complexType name="ReferenceType"&gt;
 * </pre>
 * 
 * @author Adrian Cole
 */
public class Reference<T extends Reference<T>> {

   public static <T extends Reference<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromReference(this);
   }

   public static class Builder<T extends Reference<T>> {

      protected URI href;
      protected String id;
      protected String name;
      protected String type;

      /**
       * @see Reference#getHref()
       */
      public Builder<T> href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see Reference#getId()
       */
      public Builder<T> id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see Reference#getType()
       */
      public Builder<T> type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see Reference#getName()
       */
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      public Reference<T> build() {
         Reference<T> reference = new Reference<T>(href);
         reference.setId(id);
         reference.setName(name);
         reference.setType(type);
         return reference;
      }

      protected Builder<T> fromReference(Reference<T> in) {
         return href(in.getHref()).id(in.getId()).name(in.getName()).type(in.getType());
      }

      protected Builder<T> fromAttributes(Map<String, String> attributes) {
         return href(URI.create(attributes.get("href"))).id(attributes.get("id")).name(attributes.get("name")).type(attributes.get("type"));
      }

   }

   @XmlAttribute
   protected URI href;
   @XmlAttribute
   protected String id;
   @XmlAttribute
   protected String name;
   @XmlAttribute
   protected String type;

   protected Reference(URI href) {
      this.href = href;
   }

   protected Reference() {
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
    * The resource identifier, expressed in URN format.
    *
    * The value of this attribute uniquely identifies the resource, persists for the life of the
    * resource, and is never reused.
    */
   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   /**
    * Contains the name of the the entity.
    *
    * The object type, specified as a MIME content type, of the object that the link references.
    * This attribute is present only for links to objects. It is not present for links to actions.
    * 
    * @return type definition, type, expressed as an HTTP Content-Type
    */
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
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
    * @see #getHref()
    */
   public URI getURI() {
      return getHref();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Reference<?> that = Reference.class.cast(o);
      return equal(this.href, that.href) && equal(this.id, that.id) && equal(this.name, that.name) && equal(this.type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(href, id, name, type);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("href", href).add("id", id).add("name", name).add("type", type);
   }
}