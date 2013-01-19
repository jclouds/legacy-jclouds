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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.logging.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A reference to a resource.
 * 
 * Contains an href attribute and optional name and type attributes.
 * <p>
 * 
 * <pre>
 * &lt;xs:complexType name="ReferenceType"&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
@XmlSeeAlso({ VAppReference.class, CatalogReference.class, RoleReference.class })
@XmlRootElement(name = "Reference")
@XmlType(name = "ReferenceType")
public class Reference {

   @javax.annotation.Resource
   protected static Logger logger = Logger.NULL;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromReference(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public static class Builder<B extends Builder<B>> {

      private URI href;
      private String name;
      private String type;

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * @see Reference#getHref()
       */
      public B href(URI href) {
         this.href = href;
         return self();
      }

      /**
       * @see Reference#getType()
       */
      public B type(String type) {
         this.type = type;
         return self();
      }

      /**
       * @see Reference#getName()
       */
      public B name(String name) {
         this.name = name;
         return self();
      }

      public Reference build() {
         return new Reference(this);
      }

      public B fromReference(Reference in) {
         return href(in.getHref()).name(in.getName()).type(in.getType());
      }

      public B fromEntity(Entity in) {
         return href(in.getHref()).name(in.getName()).type(in.getType());
      }

      protected B fromAttributes(Map<String, String> attributes) {
         return href(URI.create(attributes.get("href"))).name(attributes.get("name")).type(attributes.get("type"));
      }
   }

   @XmlAttribute(required = true)
   private URI href;
   @XmlAttribute
   private String name;
   @XmlAttribute
   private String type;

   protected Reference(Builder<?> builder) {
      this.href = builder.href;
      this.name = builder.name;
      this.type = builder.type;
   }

   protected Reference(URI href, String name, String type) {
      this.href = href;
      this.name = name;
      this.type = type;
   }

   protected Reference() {
      // For JAXB
   }

   /**
    * Contains the URI to the entity.
    * <p/>
    * An object reference, expressed in URL format. Because this URL includes the object identifier
    * portion of the id attribute value, it uniquely identifies the object, persists for the life of
    * the object, and is never reused. The value of the href attribute is a reference to a view of
    * the object, and can be used to access a representation of the object that is valid in a
    * particular context. Although URLs have a well-known syntax and a well-understood
    * interpretation, a api should treat each href as an opaque string. The rules that govern how
    * the server constructs href strings might change in future releases.
    * 
    * @return an opaque reference and should never be parsed
    */
   public URI getHref() {
      return href;
   }

   /**
    * Contains the name of the the entity.
    * <p/>
    * The object type, specified as a MIME content type, of the object that the link references.
    * This attribute is present only for links to objects. It is not present for links to actions.
    * 
    * @return type definition, type, expressed as an HTTP Content-Type
    */
   public String getName() {
      return name;
   }

   /**
    * Contains the type of the the entity.
    * <p/>
    * The object type, specified as a MIME content type, of the object that the link references.
    * This attribute is present only for links to objects. It is not present for links to actions.
    * 
    * @return type definition, type, expressed as an HTTP Content-Type
    */
   public String getType() {
      return type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Reference that = Reference.class.cast(o);
      return equal(this.href, that.href) && equal(this.name, that.name) && equal(this.type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(href, name, type);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("href", href).add("name", name).add("type", type);
   }

}
