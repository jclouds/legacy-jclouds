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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * @see <a href="http://docs.amazonwebservices.com/IAM/latest/APIReference/API_GetUser.html" />
 * 
 * @author Adrian Cole
 */
public final class User {

   private final Optional<String> path;
   private final Optional<String> name;
   private final String id;
   private final String arn;
   private final Date createDate;

   private User(String id, String arn, Optional<String> path, Optional<String> name, Date createDate) {
      this.id = checkNotNull(id, "id");
      this.arn = checkNotNull(arn, "arn for %s", id);
      this.path = checkNotNull(path, "path for %s", arn);
      this.name = checkNotNull(name, "name for %s", arn);
      this.createDate = checkNotNull(createDate, "createDate for %s", arn);
   }

   /**
    * a globally unique identifier (GUID), returned from the api.
    */
   public String getId() {
      return id;
   }

   /**
    * how to specify the resource in the access policy language ex.
    * {@code arn:aws:<service>:<region>:<namespace>:<relative-id>}
    */
   public String getArn() {
      return arn;
   }

   /**
    * path ex {@code  /division_abc/subdivision_xyz/product_1234/engineering/}
    */
   public Optional<String> getPath() {
      return path;
   }

   /**
    * friendly name ex. {@code Developers}
    */
   public Optional<String> getName() {
      return name;
   }

   /**
    * Date the user was created
    */
   public Date getCreateDate() {
      return createDate;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(id, arn);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      User other = (User) obj;
      return Objects.equal(this.id, other.id) && Objects.equal(this.arn, other.arn);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("path", path).add("name", name).add("id", id).add("arn", arn)
            .add("createDate", createDate).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static class Builder {
      private Optional<String> path = Optional.absent();
      private String id;
      private Optional<String> name = Optional.absent();
      private String arn;
      private Date createDate;

      /**
       * @see User#getPath()
       */
      public Builder path(String path) {
         this.path = Optional.fromNullable(path);
         return this;
      }

      /**
       * @see User#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see User#getName()
       */
      public Builder name(String name) {
         this.name = Optional.fromNullable(name);
         return this;
      }

      /**
       * @see User#getArn()
       */
      public Builder arn(String arn) {
         this.arn = arn;
         return this;
      }

      /**
       * @see User#getCreateDate()
       */
      public Builder createDate(Date createDate) {
         this.createDate = createDate;
         return this;
      }

      public User build() {
         return new User(id, arn, path, name, createDate);
      }

      public Builder from(User in) {
         return this.path(in.path.orNull()).name(in.name.orNull()).id(in.id).arn(in.arn).createDate(in.createDate);
      }
   }
}
