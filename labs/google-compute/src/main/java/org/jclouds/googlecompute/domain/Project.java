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
import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Project resource is the root collection and settings resource for all Google Compute Engine resources.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/projects"/>
 */
public class Project extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromProject(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {

      private Metadata.Builder commonInstanceMetadata = Metadata.builder().kind(Metadata.Kind.COMPUTE);
      private ImmutableSet.Builder<ProjectQuota> quotas = ImmutableSet.builder();
      private ImmutableSet.Builder<String> externalIpAddresses = ImmutableSet.builder();

      /**
       * @see Project#getCommonInstanceMetadata()
       */
      public T addCommonInstanceMetadata(String key, String value) {
         this.commonInstanceMetadata.addItem(key, value);
         return self();
      }

      /**
       * @see Project#getCommonInstanceMetadata()
       */
      public T commonInstanceMetadata(Metadata commonInstanceMetadata) {
         this.commonInstanceMetadata = Metadata.builder().fromMetadata(commonInstanceMetadata);
         return self();
      }

      /**
       * @see Project#getQuotas()
       */
      public T addQuota(String metric, double usage, double limit) {
         this.quotas.add(ProjectQuota.builder().metric(metric).usage(usage).limit(limit).build());
         return self();
      }

      /**
       * @see Project#getQuotas()
       */
      public T quotas(Set<ProjectQuota> quotas) {
         this.quotas.addAll(checkNotNull(quotas));
         return self();
      }

      /**
       * @see Project#getExternalIpAddresses()
       */
      public T addExternalIpAddress(String externalIpAddress) {
         this.externalIpAddresses.add(checkNotNull(externalIpAddress));
         return self();
      }

      /**
       * @see Project#getExternalIpAddresses()
       */
      public T externalIpAddresses(Set<String> externalIpAddresses) {
         this.externalIpAddresses.addAll(checkNotNull(externalIpAddresses));
         return self();
      }

      public Project build() {
         return new Project(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, commonInstanceMetadata.build(), quotas.build(), externalIpAddresses.build());
      }

      public T fromProject(Project in) {
         return super.fromResource(in).commonInstanceMetadata(in.getCommonInstanceMetadata()).quotas(in.getQuotas())
                 .externalIpAddresses(in.getExternalIpAddresses());
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Metadata commonInstanceMetadata;
   private final Set<ProjectQuota> quotas;
   private final Set<String> externalIpAddresses;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "commonInstanceMetadata", "quotas",
           "externalIpAddresses"
   })
   protected Project(String id, Date creationTimestamp, String selfLink, String name, String description,
                     Metadata commonInstanceMetadata, Set<ProjectQuota> quotas, Set<String> externalIpAddresses) {
      super(Kind.PROJECT, id, creationTimestamp, selfLink, checkNotNull(name), description);
      this.commonInstanceMetadata = commonInstanceMetadata != null ? commonInstanceMetadata : Metadata.builder()
              .build();
      this.quotas = quotas;
      this.externalIpAddresses = externalIpAddresses != null && !externalIpAddresses.isEmpty() ? externalIpAddresses
              : null;
   }

   /**
    * @return metadata key/value pairs available to all instances contained in this project.
    */
   public Metadata getCommonInstanceMetadata() {
      return commonInstanceMetadata;
   }

   /**
    * @return quotas assigned to this project.
    */
   public Set<ProjectQuota> getQuotas() {
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
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, creationTimestamp, selfLink, name, description, commonInstanceMetadata, quotas,
              externalIpAddresses);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Project that = Project.class.cast(obj);
      return super.equals(that)
              && equal(this.commonInstanceMetadata, that.commonInstanceMetadata)
              && equal(this.quotas, that.quotas)
              && equal(this.externalIpAddresses, that.externalIpAddresses);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("commonInstanceMetadata", commonInstanceMetadata).add("quotas",
                      quotas).add("externalIpAddresses", externalIpAddresses);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

}
