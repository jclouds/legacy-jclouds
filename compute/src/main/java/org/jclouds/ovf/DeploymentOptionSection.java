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
package org.jclouds.ovf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

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
 */
public class DeploymentOptionSection extends Section<DeploymentOptionSection> {

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

   public static class Builder extends Section.Builder<DeploymentOptionSection> {
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
         this.configurations = ImmutableSet.<Configuration> copyOf(checkNotNull(configurations, "configurations"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public DeploymentOptionSection build() {
         return new DeploymentOptionSection(info, configurations);
      }

      public Builder fromDeploymentOptionSection(DeploymentOptionSection in) {
         return info(in.getInfo()).configurations(in.getConfigurations());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(Section<DeploymentOptionSection> in) {
         return Builder.class.cast(super.fromSection(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

   }

   protected final Set<Configuration> configurations;

   public DeploymentOptionSection(String info, Iterable<Configuration> configurations) {
      super(info);
      this.configurations = ImmutableSet.<Configuration> copyOf(checkNotNull(configurations, "configurations"));

   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((configurations == null) ? 0 : configurations.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      DeploymentOptionSection other = (DeploymentOptionSection) obj;
      if (configurations == null) {
         if (other.configurations != null)
            return false;
      } else if (!configurations.equals(other.configurations))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[info=%s, configurations=%s]", info, configurations);
   }

   public Set<Configuration> getConfigurations() {
      return configurations;
   }

}
