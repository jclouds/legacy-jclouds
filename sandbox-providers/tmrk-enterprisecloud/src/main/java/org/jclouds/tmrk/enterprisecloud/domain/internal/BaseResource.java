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
package org.jclouds.tmrk.enterprisecloud.domain.internal;

import javax.xml.bind.annotation.XmlAttribute;
import java.net.URI;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Location of a Rest resource
 * 
 * @author Adrian Cole
 * 
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
      this.type = checkNotNull(type, "type");
      this.href = checkNotNull(href, "href");
   }

   protected BaseResource() {
      //For JAXB
   }

   /**
    * 
    * @return type definition, type, expressed as an HTTP Content-Type
    */
   public String getType() {
      return type;
   }

   /**
    * 
    * @return an opaque reference and should never be parsed
    */
   public URI getHref() {
      return href;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      BaseResource that = (BaseResource) o;

      if (href != null ? !href.equals(that.href) : that.href != null)
         return false;
      if (type != null ? !type.equals(that.type) : that.type != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = type != null ? type.hashCode() : 0;
      result = 31 * result + (href != null ? href.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return String.format("[%s]",string());
   }

   protected String string() {
       return "href="+href+", type="+type;
   }
}