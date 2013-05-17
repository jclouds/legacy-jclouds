/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.swift.domain.ObjectInfo;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * Class ObjectInfoImpl
 */
public class ObjectInfoImpl implements ObjectInfo {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromObjectInfoImpl(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected String container;
      protected URI uri;
      protected byte[] hash;
      protected Long bytes;
      protected String contentType;
      protected Date lastModified;

      /**
       * @see ObjectInfoImpl#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see ObjectInfoImpl#getContainer()
       */
      public T container(String container) {
         this.container = container;
         return self();
      }

      /**
       * @see ObjectInfoImpl#getUri()
       */
      public T uri(URI uri) {
         this.uri = uri;
         return self();
      }

      /**
       * @see ObjectInfoImpl#getHash()
       */
      public T hash(byte[] hash) {
         this.hash = hash;
         return self();
      }

      /**
       * @see ObjectInfoImpl#getBytes()
       */
      public T bytes(Long bytes) {
         this.bytes = bytes;
         return self();
      }

      /**
       * @see ObjectInfoImpl#getContentType()
       */
      public T contentType(String contentType) {
         this.contentType = contentType;
         return self();
      }

      /**
       * @see ObjectInfoImpl#getLastModified()
       */
      public T lastModified(Date lastModified) {
         this.lastModified = lastModified;
         return self();
      }

      public ObjectInfoImpl build() {
         return new ObjectInfoImpl(name, container, uri, hash, bytes, contentType, lastModified);
      }

      public T fromObjectInfoImpl(ObjectInfoImpl in) {
         return this
               .name(in.getName())
               .container(in.getContainer())
               .uri(in.getUri())
               .hash(in.getHash())
               .bytes(in.getBytes())
               .contentType(in.getContentType())
               .lastModified(in.getLastModified());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String name;
   private final String container;
   private final URI uri;
   private final byte[] hash;
   private final Long bytes;
   @Named("content_type")
   private final String contentType;
   @Named("last_modified")
   private final Date lastModified;

   @ConstructorProperties({
         "name", "container", "uri", "hash", "bytes", "content_type", "last_modified"
   })
   protected ObjectInfoImpl(String name, @Nullable String container, @Nullable URI uri, @Nullable byte[] hash, @Nullable Long bytes,
                            @Nullable String contentType, @Nullable Date lastModified) {
      this.name = checkNotNull(name, "name");
      this.container = container;
      this.uri = uri;
      this.hash = hash;
      this.bytes = bytes;
      this.contentType = contentType;
      this.lastModified = lastModified;
   }

   /**
    * {@inheritDoc}
    */
   public String getName() {
      return this.name;
   }

   /**
    * {@inheritDoc}
    */
   @Nullable
   public String getContainer() {
      return this.container;
   }

   /**
    * {@inheritDoc}
    */
   @Nullable
   public URI getUri() {
      return this.uri;
   }

   /**
    * {@inheritDoc}
    */
   @Nullable
   public byte[] getHash() {
      return this.hash;
   }

   /**
    * {@inheritDoc}
    */
   @Nullable
   public Long getBytes() {
      return this.bytes;
   }

   /**
    * {@inheritDoc}
    */
   @Nullable
   public String getContentType() {
      return this.contentType;
   }

   /**
    * {@inheritDoc}
    */
   @Nullable
   public Date getLastModified() {
      return this.lastModified;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, container);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ObjectInfoImpl that = ObjectInfoImpl.class.cast(obj);
      return Objects.equal(this.name, that.name)
            && Objects.equal(this.container, that.container);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name).add("container", container).add("uri", uri).add("hash", Arrays.toString(hash))
            .add("bytes", bytes).add("contentType", contentType).add("lastModified", lastModified);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(ObjectInfo other) {
      return ComparisonChain.start().compare(name, other.getName()).compare(container, other.getContainer(), Ordering.natural().nullsLast()).result();
   }
}
