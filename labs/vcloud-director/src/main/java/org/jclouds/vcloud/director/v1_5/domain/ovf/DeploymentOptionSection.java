/**
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
package org.jclouds.vcloud.director.v1_5.domain.ovf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The DeploymentOptionSection specifies a discrete set of intended resource configurations. The
 * author of an OVF package can include sizing metadata for different configurations. A consumer of
 * the OVF shall select a configuration, for example, by prompting the user. The selected
 * configuration is visible in the OVF environment, enabling guest software to adapt to the selected
 * configuration.
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@XmlRootElement(name = "DeploymentOptionSection")
public class DeploymentOptionSection extends SectionType<DeploymentOptionSection> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromDeploymentOptionSection(this);
   }

   public static class Builder extends SectionType.Builder<DeploymentOptionSection> {
      protected Set<Configuration> configurations = Sets.newLinkedHashSet();

      /**
       * @see DeploymentOptionSection#getConfigurations
       */
      public Builder configuration(Configuration configuration) {
         this.configurations.add(checkNotNull(configuration, "configuration"));
         return this;
      }

      /**
       * @see DeploymentOptionSection#getConfigurations
       */
      public Builder configurations(Iterable<Configuration> configurations) {
         this.configurations = ImmutableSet.<Configuration>copyOf(checkNotNull(configurations, "configurations"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public DeploymentOptionSection build() {
         return new DeploymentOptionSection(info, required, configurations);
      }

      public Builder fromDeploymentOptionSection(DeploymentOptionSection in) {
         return info(in.getInfo()).configurations(in.getConfigurations());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSectionType(SectionType<DeploymentOptionSection> in) {
         return Builder.class.cast(super.fromSectionType(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder required(Boolean required) {
         return Builder.class.cast(super.required(required));
      }

   }

   @XmlElement(name = "Configuration")
   protected Set<Configuration> configurations;

   private DeploymentOptionSection(@Nullable String info, @Nullable Boolean required, Iterable<Configuration> configurations) {
      super(info, required);
      this.configurations = ImmutableSet.copyOf(configurations);
   }

   private DeploymentOptionSection() {
      // For JAXB
   }

   public Set<Configuration> getConfigurations() {
      return Collections.unmodifiableSet(configurations);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), configurations);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;

      DeploymentOptionSection other = (DeploymentOptionSection) obj;
      return super.equals(other) && Objects.equal(configurations, other.configurations);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string().add("configurations", configurations);
   }
}