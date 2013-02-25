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
package org.jclouds.iam.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * @see <a href="http://docs.amazonwebservices.com/IAM/latest/APIReference/API_GetInstanceProfile.html" />
 * 
 * @author Adrian Cole
 */
public final class InstanceProfile {

   private final String arn;
   private final String id;
   private final String name;
   private final String path;
   private final Date createDate;
   private final ImmutableList<Role> roles;

   private InstanceProfile(String arn, String id, String name, String path, Date createDate, ImmutableList<Role> roles) {
      this.arn = checkNotNull(arn, "arn");
      this.id = checkNotNull(id, "id for %s", arn);
      this.name = checkNotNull(name, "name for %s", arn);
      this.path = checkNotNull(path, "path for %s", arn);
      this.createDate = checkNotNull(createDate, "createDate for %s", arn);
      this.roles = checkNotNull(roles, "roles for %s", arn);
   }

   /**
    * how to specify the resource in the access policy language ex.
    * {@code arn:aws:<service>:<region>:<namespace>:<relative-id>}
    */
   public String getArn() {
      return arn;
   }

   /**
    * a globally unique identifier (GUID), returned from the api.
    */
   public String getId() {
      return id;
   }

   /**
    * friendly name ex. {@code Developers}
    */
   public String getName() {
      return name;
   }

   /**
    * path ex {@code  /division_abc/subdivision_xyz/product_1234/engineering/}
    */
   public String getPath() {
      return path;
   }

   /**
    * Date the instance profile was created
    */
   public Date getCreateDate() {
      return createDate;
   }

   /**
    * The roles assigned to the instance profile
    */
   public ImmutableList<Role> getRoles() {
      return roles;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(arn, id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      InstanceProfile that = InstanceProfile.class.cast(obj);
      return equal(this.arn, that.arn) && equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("arn", arn).add("id", id).add("name", name).add("path", path)
            .add("createDate", createDate).add("roles", roles).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static class Builder {
      private String arn;
      private String id;
      private String name;
      private String path;
      private Date createDate;
      private ImmutableList.Builder<Role> roles = ImmutableList.<Role> builder();

      /**
       * @see InstanceProfile#getArn()
       */
      public Builder arn(String arn) {
         this.arn = arn;
         return this;
      }

      /**
       * @see InstanceProfile#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see InstanceProfile#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see InstanceProfile#getPath()
       */
      public Builder path(String path) {
         this.path = path;
         return this;
      }

      /**
       * @see InstanceProfile#getCreateDate()
       */
      public Builder createDate(Date createDate) {
         this.createDate = createDate;
         return this;
      }

      /**
       * @see InstanceProfile#getRoles()
       */
      public Builder role(Role role) {
         this.roles.add(checkNotNull(role, "role"));
         return this;
      }

      /**
       * @see InstanceProfile#getRoles()
       */
      public Builder roles(Iterable<Role> roles) {
         this.roles.addAll(checkNotNull(roles, "roles"));
         return this;
      }

      public InstanceProfile build() {
         return new InstanceProfile(arn, id, name, path, createDate, roles.build());
      }

      public Builder from(InstanceProfile in) {
         return arn(in.arn).id(in.id).name(in.name).path(in.path).createDate(in.createDate).roles(in.roles);
      }
   }
}
