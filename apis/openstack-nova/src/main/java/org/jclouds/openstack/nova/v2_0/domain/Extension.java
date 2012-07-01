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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The OpenStack Compute API is extensible. Extensions serve two purposes: They
 * allow the introduction of new features in the API without requiring a version
 * change and they allow the introduction of vendor specific niche
 * functionality.
 * 
 * @author Adrian Cole
 * @see <a href=
      "http://docs.openstack.org/api/openstack-compute/2/content/Extensions-d1e1444.html"
      />
*/
public class Extension extends Resource {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromExtension(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Resource.Builder<T>  {
      protected URI namespace;
      protected String alias;
      protected Date updated;
      protected String description;
   
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
         this.alias = alias;
         return self();
      }

      /**
       * @see Extension#getAlias()
       */
      @Override
      public T id(String id) {
         return alias(id);
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
         return new Extension(name, links, namespace, alias, updated, description);
      }
      
      public T fromExtension(Extension in) {
         return super.fromResource(in)
                  .namespace(in.getNamespace())
                  .alias(in.getAlias())
                  .updated(in.getUpdated())
                  .description(in.getDescription());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final URI namespace;
   private final String alias;
   private final Date updated;
   private final String description;

   @ConstructorProperties({
      "name", "links", "namespace", "alias", "updated", "description"
   })
   protected Extension(@Nullable String name, Set<Link> links, URI namespace, String alias, @Nullable Date updated, String description) {
      super(alias, name, links);
      this.namespace = checkNotNull(namespace, "namespace");
      this.alias = checkNotNull(alias, "alias");
      this.updated = updated;
      this.description = checkNotNull(description, "description");
   }

   public URI getNamespace() {
      return this.namespace;
   }

   public String getAlias() {
      return this.alias;
   }

   @Nullable
   public Date getUpdated() {
      return this.updated;
   }

   public String getDescription() {
      return this.description;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(namespace, alias, updated, description);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Extension that = Extension.class.cast(obj);
      return super.equals(that) && Objects.equal(this.namespace, that.namespace)
               && Objects.equal(this.alias, that.alias)
               && Objects.equal(this.updated, that.updated)
               && Objects.equal(this.description, that.description);
   }
   
   protected ToStringHelper string() {
      return super.string()
            .add("namespace", namespace).add("alias", alias).add("updated", updated).add("description", description);
   }
   
}
