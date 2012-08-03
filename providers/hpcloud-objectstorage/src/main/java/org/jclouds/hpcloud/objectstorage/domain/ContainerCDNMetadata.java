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
package org.jclouds.hpcloud.objectstorage.domain;

import java.beans.ConstructorProperties;
import java.net.URI;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @author James Murty
 */
public class ContainerCDNMetadata implements Comparable<ContainerCDNMetadata> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromContainerCDNMetadata(this);
   }

   public static abstract class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected boolean cdnEnabled;
      protected long ttl;
      protected URI CDNUri;
      protected String referrerAcl;
      protected String useragentAcl;
      protected boolean logRetention;

      /**
       * @see ContainerCDNMetadata#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see ContainerCDNMetadata#isCDNEnabled()
       */
      public T CDNEnabled(boolean cdnEnabled) {
         this.cdnEnabled = cdnEnabled;
         return self();
      }

      /**
       * @see ContainerCDNMetadata#getTTL
       */
      public T ttl(long ttl) {
         this.ttl = ttl;
         return self();
      }

      /**
       * @see ContainerCDNMetadata#getCDNUri()
       */
      public T CDNUri(URI CDNUri) {
         this.CDNUri = CDNUri;
         return self();
      }

      /**
       * @see ContainerCDNMetadata#getReferrerAcl()
       */
      public T referrerAcl(String referrerAcl) {
         this.referrerAcl = referrerAcl;
         return self();
      }

      /**
       * @see ContainerCDNMetadata#getUseragentAcl()
       */
      public T useragent_acl(String useragentAcl) {
         this.useragentAcl = useragentAcl;
         return self();
      }

      /**
       * @see ContainerCDNMetadata#isLogRetention()
       */
      public T logRetention(boolean logRetention) {
         this.logRetention = logRetention;
         return self();
      }

      public ContainerCDNMetadata build() {
         return new ContainerCDNMetadata(name, cdnEnabled, ttl, CDNUri, referrerAcl, useragentAcl, logRetention);
      }

      public T fromContainerCDNMetadata(ContainerCDNMetadata in) {
         return this
               .name(in.getName())
               .CDNEnabled(in.isCDNEnabled())
               .ttl(in.getTTL())
               .CDNUri(in.getCDNUri())
               .referrerAcl(in.getReferrerAcl())
               .useragent_acl(in.getUseragentAcl())
               .logRetention(in.isLogRetention());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String name;
   private final boolean cdnEnabled;
   private final long ttl;
   private final URI CDNUri;
   private final String referrerAcl;
   private final String useragentAcl;
   private final boolean logRetention;

   @ConstructorProperties({
         "name", "cdn_enabled", "ttl", "x-cdn-uri", "referrer_acl", "useragent_acl", "log_retention"
   })
   protected ContainerCDNMetadata(@Nullable String name, boolean cdnEnabled, long ttl, @Nullable URI CDNUri,
                                  @Nullable String referrerAcl, @Nullable String useragentAcl, boolean logRetention) {
      this.name = name;
      this.cdnEnabled = cdnEnabled;
      this.ttl = ttl;
      this.CDNUri = CDNUri;
      this.referrerAcl = referrerAcl;
      this.useragentAcl = useragentAcl;
      this.logRetention = logRetention;
   }

   /**
    * Beware: The container name is not available from HEAD CDN responses and will be null.
    *
    * @return the name of the container to which these CDN settings apply.
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   public boolean isCDNEnabled() {
      return this.cdnEnabled;
   }

   public long getTTL() {
      return this.ttl;
   }

   @Nullable
   public URI getCDNUri() {
      return this.CDNUri;
   }

   @Nullable
   public String getReferrerAcl() {
      return this.referrerAcl;
   }

   @Nullable
   public String getUseragentAcl() {
      return this.useragentAcl;
   }

   public boolean isLogRetention() {
      return this.logRetention;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, CDNUri);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ContainerCDNMetadata that = ContainerCDNMetadata.class.cast(obj);
      return Objects.equal(this.name, that.name) && Objects.equal(this.CDNUri, that.CDNUri);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name).add("cdnEnabled", cdnEnabled).add("ttl", ttl).add("CDNUri", CDNUri)
            .add("referrerAcl", referrerAcl).add("useragentAcl", useragentAcl).add("logRetention", logRetention);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public int compareTo(ContainerCDNMetadata o) {
      if (getName() == null)
         return -1;
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }
}
