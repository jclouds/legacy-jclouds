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

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Project resource is the root collection and settings resource for all Google Compute Engine resources.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/projects"/>
 */
@Beta
public class Project extends Resource {

   private final Map<String, String> commonInstanceMetadata;
   private final Set<Quota> quotas;
   private final Set<String> externalIpAddresses;

   protected Project(String id, Date creationTimestamp, URI selfLink, String name, String description,
                     Map<String, String> commonInstanceMetadata, Set<Quota> quotas, Set<String> externalIpAddresses) {
      super(Kind.PROJECT, checkNotNull(id, "id of %s", name), fromNullable(creationTimestamp), checkNotNull(selfLink,
              "selfLink of %s", name), checkNotNull(name, "name"), fromNullable(description));
      this.commonInstanceMetadata = commonInstanceMetadata == null ? ImmutableMap.<String,
              String>of() : ImmutableMap.copyOf(commonInstanceMetadata);
      this.quotas = quotas == null ? ImmutableSet.<Quota>of() : ImmutableSet.copyOf(quotas);
      this.externalIpAddresses = externalIpAddresses == null ? ImmutableSet.<String>of() : ImmutableSet.copyOf
              (externalIpAddresses);
   }

   /**
    * @return metadata key/value pairs available to all instances contained in this project.
    */
   public Map<String, String> getCommonInstanceMetadata() {
      return commonInstanceMetadata;
   }

   /**
    * @return quotas assigned to this project.
    */
   public Set<Quota> getQuotas() {
      return quotas;
   }

   /**
    * @return internet available IP addresses available for use in this project.
    */
   @Nullable
   public Set<String> getExternalIpAddresses() {
      return externalIpAddresses;
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("commonInstanceMetadata", commonInstanceMetadata)
              .add("quotas", quotas)
              .add("externalIpAddresses", externalIpAddresses);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromProject(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private ImmutableMap.Builder<String, String> commonInstanceMetadata = ImmutableMap.builder();
      private ImmutableSet.Builder<Quota> quotas = ImmutableSet.builder();
      private ImmutableSet.Builder<String> externalIpAddresses = ImmutableSet.builder();

      /**
       * @see Project#getCommonInstanceMetadata()
       */
      public Builder addCommonInstanceMetadata(String key, String value) {
         this.commonInstanceMetadata.put(key, value);
         return this;
      }

      /**
       * @see Project#getCommonInstanceMetadata()
       */
      public Builder commonInstanceMetadata(Map<String, String> commonInstanceMetadata) {
         this.commonInstanceMetadata.putAll(checkNotNull(commonInstanceMetadata, "commonInstanceMetadata"));
         return this;
      }

      /**
       * @see Project#getQuotas()
       */
      public Builder addQuota(String metric, double usage, double limit) {
         this.quotas.add(Quota.builder().metric(metric).usage(usage).limit(limit).build());
         return this;
      }

      /**
       * @see Project#getQuotas()
       */
      public Builder quotas(Set<Quota> quotas) {
         this.quotas.addAll(checkNotNull(quotas));
         return this;
      }

      /**
       * @see Project#getExternalIpAddresses()
       */
      public Builder addExternalIpAddress(String externalIpAddress) {
         this.externalIpAddresses.add(checkNotNull(externalIpAddress, "externalIpAddress"));
         return this;
      }

      /**
       * @see Project#getExternalIpAddresses()
       */
      public Builder externalIpAddresses(Set<String> externalIpAddresses) {
         this.externalIpAddresses.addAll(checkNotNull(externalIpAddresses, "externalIpAddresses"));
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public Project build() {
         return new Project(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, commonInstanceMetadata.build(), quotas.build(), externalIpAddresses.build());
      }

      public Builder fromProject(Project in) {
         return super.fromResource(in).commonInstanceMetadata(in.getCommonInstanceMetadata()).quotas(in.getQuotas())
                 .externalIpAddresses(in.getExternalIpAddresses());
      }
   }

   /**
    * Quotas assigned to a given project
    *
    * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/projects#resource"/>
    */
   public static final class Quota {

      private String metric;
      private double usage;
      private double limit;

      @ConstructorProperties({
              "metric", "usage", "limit"
      })
      protected Quota(String metric, Double usage, Double limit) {
         this.metric = checkNotNull(metric, "metric");
         this.usage = checkNotNull(usage, "usage");
         this.limit = checkNotNull(limit, "limit");
      }

      /**
       * @return name of the quota metric.
       */
      public String getMetric() {
         return metric;
      }

      /**
       * @return current usage of this metric.
       */
      public Double getUsage() {
         return usage;
      }

      /**
       * @return quota limit for this metric.
       */
      public Double getLimit() {
         return limit;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(metric);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         Quota that = Quota.class.cast(obj);
         return equal(this.metric, that.metric);
      }

      /**
       * {@inheritDoc}
       */
      protected Objects.ToStringHelper string() {
         return toStringHelper(this)
                 .omitNullValues()
                 .add("metric", metric)
                 .add("usage", usage)
                 .add("limit", limit);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return string().toString();
      }

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromQuota(this);
      }

      public static class Builder {

         private String metric;
         private Double usage;
         private Double limit;

         /**
          * @see org.jclouds.googlecompute.domain.Project.Quota#getMetric()
          */
         public Builder metric(String metric) {
            this.metric = checkNotNull(metric, "metric");
            return this;
         }

         /**
          * @see org.jclouds.googlecompute.domain.Project.Quota#getUsage()
          */
         public Builder usage(Double usage) {
            this.usage = usage;
            return this;
         }

         /**
          * @see org.jclouds.googlecompute.domain.Project.Quota#getLimit()
          */
         public Builder limit(Double limit) {
            this.limit = limit;
            return this;
         }

         public Quota build() {
            return new Quota(metric, usage, limit);
         }

         public Builder fromQuota(Quota quota) {
            return new Builder().metric(quota.getMetric()).usage(quota.getUsage()).limit(quota.getLimit());
         }
      }
   }
}
