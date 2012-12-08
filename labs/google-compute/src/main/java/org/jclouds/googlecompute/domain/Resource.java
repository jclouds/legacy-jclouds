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

package org.jclouds.googlecompute.domain;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.Date;

import static com.google.common.base.Objects.ToStringHelper;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for Google Compute Engine resources.
 *
 * @author David Alves
 */
public class Resource {

   public enum Kind {
      DISK,
      DISK_LIST,
      FIREWALL,
      FIREWALL_LIST,
      IMAGE,
      IMAGE_LIST,
      OPERATION,
      OPERATION_LIST,
      INSTANCE,
      INSTANCE_LIST,
      KERNEL,
      KERNEL_LIST,
      MACHINE_TYPE,
      MACHINE_TYPE_LIST,
      PROJECT,
      NETWORK,
      NETWORK_LIST,
      SNAPSHOT,
      ZONE,
      ZONE_LIST;

      public String value() {
         return Joiner.on("#").join("compute", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name()));
      }

      @Override
      public String toString() {
         return value();
      }

      public static Kind fromValue(String kind) {
         return valueOf(CaseFormat.LOWER_CAMEL.to(CaseFormat
                 .UPPER_UNDERSCORE,
                 Iterables.getLast(Splitter.on("#").split(checkNotNull(kind,
                         "kind")))));
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromResource(this);
   }

   public abstract static class Builder<T extends Builder<T>> {

      protected abstract T self();

      protected Kind kind;
      protected String id;
      protected Date creationTimestamp;
      protected String selfLink;
      protected String name;
      protected String description;

      /**
       * @see Resource#getKind()
       */
      protected T kind(Kind kind) {
         this.kind = kind;
         return self();
      }

      /**
       * @see Resource#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Resource#getCreationTimestamp()
       */
      public T creationTimestamp(Date creationTimestamp) {
         this.creationTimestamp = creationTimestamp;
         return self();
      }

      /**
       * @see Resource#getSelfLink()
       */
      public T selfLink(String selfLink) {
         this.selfLink = selfLink;
         return self();
      }

      /**
       * @see Resource#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Resource#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      public Resource build() {
         return new Resource(kind, id, creationTimestamp, selfLink, name, description);
      }

      public T fromResource(Resource in) {
         return this
                 .kind(in.getKind())
                 .id(in.getId())
                 .creationTimestamp(in.getCreationTimestamp())
                 .selfLink(in.getSelfLink())
                 .name(in.getName())
                 .description(in.getDescription());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final Kind kind;
   protected final String id;
   protected final Date creationTimestamp;
   protected final String selfLink;
   protected final String name;
   protected final String description;

   @ConstructorProperties({
           "kind", "id", "creationTimestamp", "selfLink", "name", "description"
   })
   protected Resource(Kind kind, String id, Date creationTimestamp, String selfLink, String name, String description) {
      this.kind = kind;
      this.id = id;
      this.creationTimestamp = creationTimestamp;
      this.selfLink = selfLink;
      this.name = name;
      this.description = description;
   }

   /**
    * @return the Type of the resource
    */
   public Kind getKind() {
      return kind;
   }

   /**
    * @return unique identifier for the resource; defined by the server (output only)
    */
   public String getId() {
      return id;
   }

   /**
    * @return creation timestamp in RFC3339 text format (output only).
    */
   public Date getCreationTimestamp() {
      return creationTimestamp;
   }

   /**
    * @return server defined URL for the resource (output only).
    */
   public String getSelfLink() {
      return selfLink;
   }

   /**
    * @return name of the resource.
    */
   public String getName() {
      return name;
   }

   /**
    * @return an optional textual description of the resource.
    */
   @Nullable
   public String getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, name, creationTimestamp, selfLink, name, description);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Resource that = Resource.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.id, that.id)
              && equal(this.name, that.name)
              && equal(this.creationTimestamp, that.creationTimestamp)
              && equal(this.selfLink, that.selfLink)
              && equal(this.name, that.name)
              && equal(this.description, that.description);
   }

   /**
    * {@inheritDoc}
    */
   protected ToStringHelper string() {
      return toStringHelper(this)
              .add("kind", kind).add("id", id).add("name", name).add("creationTimestamp",
                      creationTimestamp).add("selfLink",
                      selfLink).add("name", name).add("description", description);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static <T, E extends Collection<T>> E nullCollectionOnNullOrEmpty(E argument) {
      return argument != null && !argument.isEmpty() ? argument : null;
   }

}
