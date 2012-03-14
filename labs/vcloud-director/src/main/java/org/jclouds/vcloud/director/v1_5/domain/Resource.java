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

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A resource.
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "Resource")
public class Resource extends ResourceType<Resource> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromResource(this);
   }

   public static class Builder extends ResourceType.Builder<Resource> {

      @Override
      public Resource build() {
         return new Resource(href, type, links);
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         return Builder.class.cast(super.link(link));
      }

      @Override
      protected Builder fromResourceType(ResourceType<Resource> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }

      protected Builder fromResource(Resource in) {
         return fromResourceType(in);
      }
   }

   private Resource(URI href, String type, Set<Link> links) {
      super(href, type, links);
   }

   private Resource() {
      // For JAXB
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Resource that = Resource.class.cast(o);
      return super.equals(that);
   }
}