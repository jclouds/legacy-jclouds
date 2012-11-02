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
package org.jclouds.azure.management.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * System properties for the specified hosted service. These properties include the service name and
 * service type; the name of the affinity group to which the service belongs, or its location if it
 * is not part of an affinity group.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >api</a>
 * @author Adrian Cole
 */
public class HostedServiceProperties {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromHostedServiceProperties(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected Optional<String> description = Optional.absent();
      protected Optional<String> location = Optional.absent();
      protected Optional<String> affinityGroup = Optional.absent();
      protected String label;

      /**
       * @see HostedServiceProperties#getDescription()
       */
      public T description(String description) {
         this.description = Optional.fromNullable(description);
         return self();
      }

      /**
       * @see HostedServiceProperties#getLocation()
       */
      public T location(String location) {
         this.location = Optional.fromNullable(location);
         return self();
      }

      /**
       * @see HostedServiceProperties#getAffinityGroup()
       */
      public T affinityGroup(String affinityGroup) {
         this.affinityGroup = Optional.fromNullable(affinityGroup);
         return self();
      }

      /**
       * @see HostedServiceProperties#getLabel()
       */
      public T label(String label) {
         this.label = label;
         return self();
      }

      public HostedServiceProperties build() {
         return new HostedServiceProperties(description, location, affinityGroup, label);
      }

      public T fromHostedServiceProperties(HostedServiceProperties in) {
         return this.description(in.getDescription().orNull()).location(in.getLocation().orNull())
                  .affinityGroup(in.getAffinityGroup().orNull()).label(in.getLabel());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final Optional<String> description;
   protected final Optional<String> location;
   protected final Optional<String> affinityGroup;
   protected final String label;

   protected HostedServiceProperties(Optional<String> description, Optional<String> location,
            Optional<String> affinityGroup, String label) {
      this.description = checkNotNull(description, "description");
      this.location = checkNotNull(location, "location");
      this.affinityGroup = checkNotNull(affinityGroup, "affinityGroup");
      this.label = checkNotNull(label, "label");
   }

   /**
    * The description for the hosted service..
    */
   public Optional<String> getDescription() {
      return description;
   }

   /**
    * The geo-location of the hosted service in Windows Azure, if the hosted service is not
    * associated with an affinity group. If a location has been specified, the AffinityGroup element
    * is not returned.
    */
   public Optional<String> getLocation() {
      return location;
   }

   /**
    * The affinity group with which this hosted service is associated, if any. If the service is
    * associated with an affinity group, the Location element is not returned.
    */
   public Optional<String> getAffinityGroup() {
      return affinityGroup;
   }

   /**
    *  The name can be up to 100 characters in length. The name can be used identify the storage account for your tracking purposes.
    */
   public String getLabel() {
      return label;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(description, location, affinityGroup, label);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      HostedServiceProperties other = (HostedServiceProperties) obj;
      return Objects.equal(this.description, other.description) && Objects.equal(this.location, other.location)
               && Objects.equal(this.affinityGroup, other.affinityGroup) && Objects.equal(this.label, other.label);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("description", description.orNull())
               .add("location", location.orNull()).add("affinityGroup", affinityGroup.orNull()).add("label", label);
   }

}
