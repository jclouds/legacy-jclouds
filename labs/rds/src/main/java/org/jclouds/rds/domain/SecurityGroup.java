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
 * Amazon RDS allows you to control access to your DB Instances using DB Security Groups. A DB
 * Security Group acts like a firewall controlling network access to your DB Instance. By default,
 * network access is turned off to your DB Instances. If you want your applications to access your
 * DB Instance you can allow access from specific EC2 security groups or IP ranges. Once ingress is
 * configured, the same rules apply to all DB Instances associated with that DBSecurityGroup.
 * 
 * <h4>Important<h4/>
 * 
 * Please ensure you authorize only specific IP ranges or EC2 security groups. We highly discourage
 * authorizing broad IP ranges (for example, 0.0.0.0/0).
 * 
 * Note that you cannot use Amazon RDS DB Security Groups to restrict access to your Amazon EC2
 * instances. Similarly, you cannot apply an Amazon EC2 security group to your Amazon RDS DB
 * Instance.
 * 
 * 
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonRDS/latest/UserGuide/Overview.RDSSecurityGroups.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class SecurityGroup {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSecurityGroup(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected String description;
      protected ImmutableSet.Builder<EC2SecurityGroup> ec2SecurityGroups = ImmutableSet.<EC2SecurityGroup> builder();
      protected ImmutableSet.Builder<IPRange> ipRanges = ImmutableSet.<IPRange> builder();
      protected String ownerId;
      protected Optional<String> vpcId = Optional.absent();

      /**
       * @see SecurityGroup#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see SecurityGroup#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see SecurityGroup#getEC2SecurityGroups()
       */
      public T ec2SecurityGroups(Iterable<EC2SecurityGroup> ec2SecurityGroups) {
         this.ec2SecurityGroups.addAll(checkNotNull(ec2SecurityGroups, "ec2SecurityGroups"));
         return self();
      }

      /**
       * @see SecurityGroup#getEC2SecurityGroups()
       */
      public T ec2SecurityGroup(EC2SecurityGroup ec2SecurityGroup) {
         this.ec2SecurityGroups.add(checkNotNull(ec2SecurityGroup, "ec2SecurityGroup"));
         return self();
      }

      /**
       * @see SecurityGroup#getEC2SecurityGroups()
       */
      public T ipRanges(Iterable<IPRange> ipRanges) {
         this.ipRanges.addAll(checkNotNull(ipRanges, "ipRanges"));
         return self();
      }

      /**
       * @see SecurityGroup#getEC2SecurityGroups()
       */
      public T ipRange(IPRange ipRange) {
         this.ipRanges.add(checkNotNull(ipRange, "ipRange"));
         return self();
      }

      /**
       * @see SecurityGroup#getOwnerId()
       */
      public T ownerId(String ownerId) {
         this.ownerId = ownerId;
         return self();
      }

      /**
       * @see SecurityGroupGroup#getVpcId()
       */
      public T vpcId(String vpcId) {
         this.vpcId = Optional.fromNullable(vpcId);
         return self();
      }

      public SecurityGroup build() {
         return new SecurityGroup(name, description, ec2SecurityGroups.build(), ipRanges.build(), ownerId, vpcId);
      }

      public T fromSecurityGroup(SecurityGroup in) {
         return this.name(in.getName()).description(in.getDescription()).ec2SecurityGroups(in.getEC2SecurityGroups())
                  .ipRanges(in.getIPRanges()).ownerId(in.getOwnerId()).vpcId(in.getVpcId().orNull());
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
   protected final Set<EC2SecurityGroup> ec2SecurityGroups;
   protected final Set<IPRange> ipRanges;
   protected final String ownerId;
   protected final Optional<String> vpcId;

   protected SecurityGroup(String name, String description, Iterable<EC2SecurityGroup> ec2SecurityGroups,
            Iterable<IPRange> ipRanges, String ownerId, Optional<String> vpcId) {
      this.name = checkNotNull(name, "name");
      this.description = checkNotNull(description, "description");
      this.ownerId = checkNotNull(ownerId, "ownerId");
      this.ec2SecurityGroups = ImmutableSet.copyOf(checkNotNull(ec2SecurityGroups, "ec2SecurityGroups"));
      this.ipRanges = ImmutableSet.copyOf(checkNotNull(ipRanges, "ipRanges"));
      this.vpcId = checkNotNull(vpcId, "vpcId");
   }

   /**
    * Specifies the name of the DB Security Group.
    */
   public String getName() {
      return name;
   }

   /**
    * Provides the description of the DB Security Group.
    */
   public String getDescription() {
      return description;
   }

   /**
    * Contains a list of EC2SecurityGroup elements.
    */
   public Set<EC2SecurityGroup> getEC2SecurityGroups() {
      return ec2SecurityGroups;
   }

   /**
    * Contains a list of IPRange elements.
    */
   public Set<IPRange> getIPRanges() {
      return ipRanges;
   }

   /**
    * Provides the AWS ID of the owner of a specific DB Security Group..
    */
   public String getOwnerId() {
      return ownerId;
   }

   /**
    * Provides the VpcId of the DB Security Group.
    */
   public Optional<String> getVpcId() {
      return vpcId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name);
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
      SecurityGroup other = (SecurityGroup) obj;
      return Objects.equal(this.name, other.name);
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
               .add("ec2SecurityGroups", ec2SecurityGroups).add("ipRanges", ipRanges).add("ownerId", ownerId)
               .add("vpcId", vpcId.orNull());
   }

}
