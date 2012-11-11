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

import com.google.common.base.Objects;

import java.beans.ConstructorProperties;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Quotas assigned to a given project
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/projects#resource"/>
 */
public class ProjectQuota {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromQuota(this);
   }

   public static class Builder {

      private String metric;
      private double usage;
      private double limit;

      /**
       * @see ProjectQuota#getMetric()
       */
      public Builder metric(String metric) {
         this.metric = checkNotNull(metric);
         return this;
      }

      /**
       * @see ProjectQuota#getUsage()
       */
      public Builder usage(double usage) {
         this.usage = usage;
         return this;
      }

      /**
       * @see ProjectQuota#getLimit()
       */
      public Builder limit(double limit) {
         this.limit = limit;
         return this;
      }

      public ProjectQuota build() {
         return new ProjectQuota(metric, usage, limit);
      }

      public Builder fromQuota(ProjectQuota quota) {
         return new Builder().metric(quota.getMetric()).usage(quota.getUsage()).limit(quota.getLimit());
      }
   }

   private String metric;
   private double usage;
   private double limit;

   @ConstructorProperties({
           "metric", "usage", "limit"
   })
   protected ProjectQuota(String metric, double usage, double limit) {
      this.metric = checkNotNull(metric);
      this.usage = usage;
      this.limit = limit;
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
   public double getUsage() {
      return usage;
   }

   /**
    * @return quota limit for this metric.
    */
   public double getLimit() {
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
      ProjectQuota that = ProjectQuota.class.cast(obj);
      return equal(this.metric, that.metric);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("metric", metric).add("usage", usage).add("limit", limit);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
