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

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/API_EC2SecurityGroup.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class EC2SecurityGroup extends Authorization {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromEC2SecurityGroup(this);
   }

   public static class Builder extends Authorization.Builder<Builder> {

      protected Optional<String> id = Optional.absent();
      protected String name;
      protected String ownerId;

      /**
       * @see EC2SecurityGroup#getId()
       */
      public Builder id(String id) {
         this.id = Optional.fromNullable(id);
         return this;
      }

      /**
       * @see EC2SecurityGroup#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see EC2SecurityGroup#getOwnerId()
       */
      public Builder ownerId(String ownerId) {
         this.ownerId = ownerId;
         return this;
      }

      public EC2SecurityGroup build() {
         return new EC2SecurityGroup(id, name, ownerId, rawStatus, status);
      }

      public Builder fromEC2SecurityGroup(EC2SecurityGroup in) {
         return fromAuthorization(in).id(in.getId().orNull()).name(in.getName()).ownerId(in.getOwnerId());
      }

      @Override
      protected Builder self() {
         return this;
      }
   }

   protected final Optional<String> id;
   protected final String name;
   protected final String ownerId;

   protected EC2SecurityGroup(Optional<String> id, String name, String ownerId, String rawStatus, Status status) {
      super(rawStatus, status);
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.ownerId = checkNotNull(ownerId, "ownerId");
   }

   /**
    * Specifies the id of the EC2 Security Group.
    */
   public Optional<String> getId() {
      return id;
   }

   /**
    * Specifies the name of the EC2 Security Group.
    */
   public String getName() {
      return name;
   }

   /**
    * Specifies the AWS ID of the owner of the EC2 security group specified as {@link #getName()}
    */
   public String getOwnerId() {
      return ownerId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, ownerId);
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
      EC2SecurityGroup other = EC2SecurityGroup.class.cast(obj);
      return Objects.equal(this.id, other.id) && Objects.equal(this.name, other.name)
               && Objects.equal(this.ownerId, other.ownerId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id.orNull()).add("name", name)
               .add("ownerId", ownerId).add("status", rawStatus).toString();
   }

}
