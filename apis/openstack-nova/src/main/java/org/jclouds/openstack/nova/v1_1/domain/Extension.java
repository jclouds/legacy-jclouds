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

import java.net.URI;
import java.util.Date;

import org.jclouds.openstack.domain.Resource;

import com.google.common.base.Objects;

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
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromExtension(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Resource.Builder<T>  {
      private URI namespace;
      private String alias;
      private Date updated;
      private String description;

      /**
       * @see Extension#getNamespace()
       */
      public T namespace(URI namespace) {
         this.namespace = namespace;
         return self();
      }

      /**
       * @see Extension#getAlias()
       */
      public T alias(String alias) {
         id(alias);
         this.alias = alias;
         return self();
      }

      /**
       * @see Extension#getUpdated()
       */
      public T updated(Date updated) {
         this.updated = updated;
         return self();
      }

      /**
       * @see Extension#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      public Extension build() {
         return new Extension(this);
      }

      public T fromExtension(Extension in) {
         return super.fromResource(in)
               .namespace(in.getNamespace())
               .alias(in.getAlias())
               .updated(in.getUpdated())
               .description(in.getDescription())
               ;
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   protected Extension() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
   
   private URI namespace;
   private String alias;
   private Date updated;
   private String description;

   protected Extension(Builder<?> builder) {
      super(builder);
      this.namespace = builder.namespace;
      this.alias = builder.alias;
      this.updated = builder.updated;
      this.description = builder.description;
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
   public Objects.ToStringHelper string() {
      return super.string()
            .add("namespace", namespace)
            .add("alias", alias)
            .add("updated", updated)
            .add("description", description);
   }
}
