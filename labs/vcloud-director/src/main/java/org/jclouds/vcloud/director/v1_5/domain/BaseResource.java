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
 * Location of a Rest resource
 * 
 * @author Adrian Cole
 */
public class BaseResource<T extends BaseResource<T>> {

   public static <T extends BaseResource<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromBaseResource(this);
   }

   public static class Builder<T extends BaseResource<T>> {

      protected String type;
      protected URI href;

      /**
       * @see BaseResource#getType
       */
      public Builder<T> type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see BaseResource#getHref
       */
      public Builder<T> href(URI href) {
         this.href = href;
         return this;
      }

      public BaseResource<T> build() {
         return new BaseResource<T>(href, type);
      }

      protected Builder<T> fromBaseResource(BaseResource<T> in) {
         return type(in.getType()).href(in.getHref());
      }

      protected Builder<T> fromAttributes(Map<String, String> attributes) {
         return href(URI.create(attributes.get("href"))).type(attributes.get("type"));
      }

   }

   @XmlAttribute
   protected String type;

   @XmlAttribute
   protected URI href;

   protected BaseResource(URI href, String type) {
      this.type = type;
      this.href = href;
   }

   protected BaseResource() {
      // For JAXB
   }

   /**
    * The object type, specified as a MIME content type, of the object that the link references.
    * This attribute is present only for links to objects. It is not present for links to actions.
    * 
    * @return type definition, type, expressed as an HTTP Content-Type
    */
   public String getType() {
      return type;
   }

   /**
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
    * @see #getHref
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
      BaseResource<?> that = BaseResource.class.cast(o);
      return equal(href, that.href) && equal(type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(type, href);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("href", href).add("type", type);
   }
}