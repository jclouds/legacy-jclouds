/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Name 2.0 (the
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
package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Objects.toStringHelper;

import java.net.URI;
import java.util.Date;
import java.util.Set;

import org.jclouds.openstack.domain.Link;
import org.jclouds.openstack.domain.Resource;

/**
 * The OpenStack Compute API is extensible. Extensions serve two purposes: They
 * allow the introduction of new features in the API without requiring a version
 * change and they allow the introduction of vendor specific niche
 * functionality.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/2/content/Extensions-d1e1444.html"
 *      />
 */
public class Extension extends Resource {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromExtension(this);
   }

   public static class Builder extends Resource.Builder {

      private URI namespace;
      private String alias;
      private Date updated;
      private String description;

      public Builder namespace(URI namespace) {
         this.namespace = namespace;
         return this;
      }

      public Builder alias(String alias) {
         this.alias = alias;
         return this;
      }

      public Builder updated(Date updated) {
         this.updated = updated;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Extension build() {
         return new Extension(name, links, namespace, alias, updated, description);
      }

      public Builder fromExtension(Extension in) {
         return fromResource(in).namespace(in.getNamespace()).alias(in.getAlias()).updated(in.getUpdated())
               .description(in.getDescription());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder id(String id) {
         return alias(id);
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
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource in) {
         return Builder.class.cast(super.fromResource(in));
      }
   }

   private URI namespace;
   private String alias;
   private Date updated;
   private String description;

   protected Extension(String name, Set<Link> links, URI namespace, String alias, Date updated, String description) {
      super(alias, name, links);
      this.namespace = namespace;
      this.alias = alias;
      this.updated = updated;
      this.description = description;
   }

   public URI getNamespace() {
      return this.namespace;
   }

   @Override
   public String getId() {
      return this.alias;
   }

   public String getAlias() {
      return this.alias;
   }

   public Date getUpdated() {
      return this.updated;
   }

   public String getDescription() {
      return this.description;
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", getId()).add("name", name).add("links", links).add("namespace", namespace)
            .add("alias", alias).add("updated", updated).add("description", description).toString();
   }

}
