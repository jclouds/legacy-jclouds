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
package org.jclouds.elb.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * The security group that you can use as part of your inbound rules for your
 * LoadBalancer's back-end Amazon EC2 application instances. To only allow
 * traffic from LoadBalancers, add a security group rule to your back end
 * instance that specifies this source security group as the inbound source.
 * 
 * @see <a
 *     href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_LoadBalancerDescription.html"
 *     >doc</a>
 * 
 * @author Adrian Cole
 */
public class SecurityGroupAndOwner {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromSourceSecurityGroup(this);
   }

   public static class Builder {

      protected String name;
      protected String owner;

      /**
       * @see SecurityGroupAndOwner#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see SecurityGroupAndOwner#getOwner()
       */
      public Builder owner(String owner) {
         this.owner = owner;
         return this;
      }

      public SecurityGroupAndOwner build() {
         return new SecurityGroupAndOwner(name, owner);
      }

      public Builder fromSourceSecurityGroup(SecurityGroupAndOwner in) {
         return this.name(in.getName()).owner(in.getOwner());
      }
   }

   protected final String name;
   protected final String owner;

   protected SecurityGroupAndOwner(String name, String owner) {
      this.name = checkNotNull(name, "name");
      this.owner = checkNotNull(owner, "owner");
   }

   /**
    * Name of the source security group.
    */
   public String getName() {
      return name;
   }

   /**
    * Owner of the source security group.
    */
   public String getOwner() {
      return owner;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, owner);
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
      SecurityGroupAndOwner other = (SecurityGroupAndOwner) obj;
      return Objects.equal(this.name, other.name) && Objects.equal(this.owner, other.owner);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("owner", owner).toString();
   }

}
