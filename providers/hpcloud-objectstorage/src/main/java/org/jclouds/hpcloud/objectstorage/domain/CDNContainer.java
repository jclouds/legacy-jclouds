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
package org.jclouds.hpcloud.objectstorage.domain;

import java.beans.ConstructorProperties;
import java.net.URI;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Objects.ToStringHelper;

public class CDNContainer implements Comparable<CDNContainer> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromCDNContainer(this);
   }

   public static class Builder {

      protected String name;
      protected boolean cdnEnabled;
      protected boolean logRetention;
      protected long ttl;
      protected URI cdnUri;
      protected URI cdnSslUri;
      protected String referrerAcl;
      protected String useragentAcl;

      /**
       * @see CDNContainer#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see CDNContainer#isCDNEnabled()
       */
      public Builder CDNEnabled(boolean cdnEnabled) {
         this.cdnEnabled = cdnEnabled;
         return this;
      }

      /**
       * @see CDNContainer#getTTL
       */
      public Builder ttl(long ttl) {
         this.ttl = ttl;
         return this;
      }

      /**
       * @see CDNContainer#getCDNUri()
       */
      public Builder CDNUri(URI cdnUri) {
         this.cdnUri = cdnUri;
         return this;
      }

      /**
       * @see CDNContainer#getCDNSslUri()
       */
      public Builder CDNSslUri(URI cdnSslUri) {
         this.cdnSslUri = cdnSslUri;
         return this;
      }
      
      /**
       * @see CDNContainer#getReferrerAcl()
       */
      public Builder referrerAcl(String referrerAcl) {
         this.referrerAcl = referrerAcl;
         return this;
      }

      /**
       * @see CDNContainer#getUseragentAcl()
       */
      public Builder useragentAcl(String useragentAcl) {
         this.useragentAcl = useragentAcl;
         return this;
      }

      /**
       * @see CDNContainer#isLogRetention()
       */
      public Builder logRetention(boolean logRetention) {
         this.logRetention = logRetention;
         return this;
      }

      public CDNContainer build() {
         return new CDNContainer(name, cdnEnabled, ttl, cdnUri, cdnSslUri, referrerAcl, useragentAcl, logRetention);
      }

      public Builder fromCDNContainer(CDNContainer in) {
         return this.name(in.getName()).CDNEnabled(in.isCDNEnabled()).ttl(in.getTTL())
                  .CDNUri(in.getCDNUri()).CDNSslUri(in.getCDNSslUri()).referrerAcl(in.getReferrerAcl())
                  .useragentAcl(in.getUseragentAcl()).logRetention(in.isLogRetention());
      }
   }

   private final String name;
   private final boolean cdnEnabled;
   private final boolean logRetention;
   private final long ttl;
   private final URI CDNUri;
   private final URI CDNSslUri;
   private final String referrerAcl;
   private final String useragentAcl;

   @ConstructorProperties({ "name", "cdnEnabled", "ttl", "cdnUri", "cdnSslUri", "referrerAcl", "useragentAcl", 
                            "logRetention" })
   protected CDNContainer(@Nullable String name, boolean cdnEnabled, long ttl, @Nullable URI CDNUri,
           @Nullable URI CDNSslUri, @Nullable String referrerAcl, @Nullable String useragentAcl, 
           boolean logRetention) {
      this.name = Strings.emptyToNull(name);
      this.cdnEnabled = cdnEnabled;
      this.ttl = ttl;
      this.CDNUri = CDNUri;
      this.CDNSslUri = CDNSslUri;
      this.referrerAcl = Strings.emptyToNull(referrerAcl);
      this.useragentAcl = Strings.emptyToNull(useragentAcl);
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
   public URI getCDNSslUri() {
      return this.CDNSslUri;
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
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      CDNContainer that = CDNContainer.class.cast(obj);
      return Objects.equal(this.name, that.name) && Objects.equal(this.CDNUri, that.CDNUri);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("cdnEnabled", cdnEnabled)
               .add("ttl", ttl).add("CDNUri", CDNUri).add("CDNSslUri", CDNSslUri).add("referrerAcl", referrerAcl)
               .add("useragentAcl", useragentAcl).add("logRetention", logRetention);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public int compareTo(CDNContainer o) {
      if (getName() == null)
         return -1;
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }
}
