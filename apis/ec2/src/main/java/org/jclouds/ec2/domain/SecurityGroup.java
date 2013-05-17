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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;

/**
 *
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-SecurityGroupItemType.html"
 *      />
 * @author Adrian Cole
 */
public class SecurityGroup extends ForwardingSet<IpPermission> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSecurityGroup(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String region;
      protected String id;
      protected String name;
      protected String ownerId;
      protected String description;
      protected ImmutableSet.Builder<IpPermission> ipPermissions = ImmutableSet.<IpPermission> builder();

      /**
       * @see SecurityGroup#getRegion()
       */
      public T region(String region) {
         this.region = region;
         return self();
      }

      /**
       * @see SecurityGroup#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see SecurityGroup#getName()
       */
      public T name(String name) {
         this.name = name;
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
       * @see SecurityGroup#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see SecurityGroup#delegate()
       */
      public T role(IpPermission role) {
         this.ipPermissions.add(role);
         return self();
      }

      /**
       * @see SecurityGroup#delegate()
       */
      public T ipPermissions(Iterable<IpPermission> ipPermissions) {
         this.ipPermissions.addAll(checkNotNull(ipPermissions, "ipPermissions"));
         return self();
      }

      /**
       * @see SecurityGroup#delegate()
       */
      public T ipPermission(IpPermission ipPermission) {
         this.ipPermissions.add(checkNotNull(ipPermission, "ipPermission"));
         return self();
      }

      public SecurityGroup build() {
         return new SecurityGroup(region, id, name, ownerId, description, ipPermissions.build());
      }

      public T fromSecurityGroup(SecurityGroup in) {
         return region(in.region).id(in.id).name(in.name).ownerId(in.ownerId).description(in.description)
               .ipPermissions(in);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String region;
   private final String id;
   private final String name;
   private final String ownerId;
   private final String description;
   private final Set<IpPermission> ipPermissions;

   public SecurityGroup(String region, String id, String name, String ownerId, String description,
         Iterable<IpPermission> ipPermissions) {
      this.region = checkNotNull(region, "region");
      this.id = id;
      this.name = name;
      this.ownerId = ownerId;
      this.description = description;
      this.ipPermissions = ImmutableSet.copyOf(checkNotNull(ipPermissions, "ipPermissions"));
   }

   /**
    * To be removed in jclouds 1.6 <h4>Warning</h4>
    *
    * Especially on EC2 clones that may not support regions, this value is
    * fragile. Consider alternate means to determine context.
    */
   @Deprecated
   public String getRegion() {
      return region;
   }

   /**
    * id of the security group. Not in all EC2 impls
    */
   @Nullable
   public String getId() {
      return id;
   }

   /**
    * Name of the security group.
    */
   public String getName() {
      return name;
   }

   /**
    * AWS Access Key ID of the owner of the security group.
    */
   public String getOwnerId() {
      return ownerId;
   }

   /**
    * Description of the security group.
    */
   public String getDescription() {
      return description;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(region, id, name, ownerId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      SecurityGroup that = SecurityGroup.class.cast(obj);
      return Objects.equal(this.region, that.region)
            && Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.ownerId, that.ownerId);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("region", region).add("id", id).add("name", name)
            .add("ownerId", ownerId).add("description", description)
            .add("ipPermissions", ipPermissions.size() == 0 ? null : ipPermissions);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   protected Set<IpPermission> delegate() {
      return ipPermissions;
   }
}
