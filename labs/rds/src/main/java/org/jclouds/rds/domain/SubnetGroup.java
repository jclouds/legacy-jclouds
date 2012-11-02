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
package org.jclouds.rds.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Relational Database Service routinely checks the health of each load-balanced Amazon EC2
 * securityGroup based on the configurations that you specify. If Relational Database Service finds
 * an unhealthy securityGroup, it stops sending traffic to the securityGroup and reroutes traffic to
 * healthy securityGroups.
 * 
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/DeveloperGuide/ConfigureSubnetGroup.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class SubnetGroup {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSubnetGroup(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected String description;
      protected String status;
      protected ImmutableSet.Builder<Subnet> subnets = ImmutableSet.<Subnet> builder();
      protected Optional<String> vpcId = Optional.absent();

      /**
       * @see SubnetGroup#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see SubnetGroup#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see SubnetGroup#getStatus()
       */
      public T status(String status) {
         this.status = status;
         return self();
      }

      /**
       * @see Instance#getSubnets()
       */
      public T subnets(Iterable<Subnet> subnets) {
         this.subnets.addAll(checkNotNull(subnets, "subnets"));
         return self();
      }

      /**
       * @see Instance#getSubnets()
       */
      public T subnet(Subnet subnet) {
         this.subnets.add(checkNotNull(subnet, "subnet"));
         return self();
      }

      /**
       * @see SubnetGroup#getVpcId()
       */
      public T vpcId(String vpcId) {
         this.vpcId = Optional.fromNullable(vpcId);
         return self();
      }

      public SubnetGroup build() {
         return new SubnetGroup(name, description, status, subnets.build(), vpcId);
      }

      public T fromSubnetGroup(SubnetGroup in) {
         return this.name(in.getName()).description(in.getDescription()).status(in.getStatus())
                  .subnets(in.getSubnets()).vpcId(in.getVpcId().orNull());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final String name;
   protected final String description;
   protected final String status;
   protected final Set<Subnet> subnets;
   protected final Optional<String> vpcId;

   protected SubnetGroup(String name, String description, String status, Iterable<Subnet> subnets, Optional<String> vpcId) {
      this.name = checkNotNull(name, "name");
      this.description = checkNotNull(description, "description");
      this.status = checkNotNull(status, "status");
      this.subnets = ImmutableSet.copyOf(checkNotNull(subnets, "subnets"));
      this.vpcId = checkNotNull(vpcId, "vpcId");
   }

   /**
    * Specifies the name of the DB Subnet Group.
    */
   public String getName() {
      return name;
   }

   /**
    * Provides the description of the DB Subnet Group.
    */
   public String getDescription() {
      return description;
   }

   /**
    * Provides the status of the DB Subnet Group.
    */
   public String getStatus() {
      return status;
   }

   /**
    * Provides the Subnets of the DB Subnet Group.
    */
   public Set<Subnet> getSubnets() {
      return subnets;
   }

   /**
    * Provides the VpcId of the DB Subnet Group.
    */
   public Optional<String> getVpcId() {
      return vpcId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, vpcId);
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
      SubnetGroup other = (SubnetGroup) obj;
      return Objects.equal(this.name, other.name) && Objects.equal(this.vpcId, other.vpcId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("description", description)
               .add("status", status).add("subnets", subnets).add("vpcId", vpcId.orNull());
   }

}
