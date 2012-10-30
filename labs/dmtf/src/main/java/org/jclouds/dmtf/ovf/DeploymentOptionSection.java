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
package org.jclouds.dmtf.ovf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
@XmlType(name = "DeploymentOptionSection_Type")
public class DeploymentOptionSection extends SectionType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromDeploymentOptionSection(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends SectionType.Builder<B> {
      private Set<Configuration> configurations = Sets.newLinkedHashSet();

      /**
       * @see DeploymentOptionSection#getConfigurations
       */
      public B configuration(Configuration configuration) {
         this.configurations.add(checkNotNull(configuration, "configuration"));
         return self();
      }

      /**
       * @see DeploymentOptionSection#getConfigurations
       */
      public B configurations(Iterable<Configuration> configurations) {
         this.configurations = ImmutableSet.<Configuration>copyOf(checkNotNull(configurations, "configurations"));
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public DeploymentOptionSection build() {
         return new DeploymentOptionSection(this);
      }
      
      public B fromDeploymentOptionSection(DeploymentOptionSection in) {
         return fromSectionType(in).configurations(in.getConfigurations());
      }
   }

   @XmlElement(name = "Configuration")
   protected Set<Configuration> configurations;

   private DeploymentOptionSection(Builder<?> builder) {
      super(builder);
      this.configurations = ImmutableSet.copyOf(builder.configurations);
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
